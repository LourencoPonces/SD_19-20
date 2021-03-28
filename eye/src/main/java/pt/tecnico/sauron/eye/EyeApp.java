package pt.tecnico.sauron.eye;

import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.*;

import static io.grpc.Status.Code.DEADLINE_EXCEEDED;
import static io.grpc.Status.Code.UNAVAILABLE;


public class EyeApp {
	private static final String CMD_WAIT = "zzz";
	private static final String CMD_COMMENT = "#";

	public static void main(String[] args) {

		System.out.println(EyeApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		final int nArgs = args.length;
		if (nArgs < 5) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", EyeApp.class.getSimpleName());
			return;
		}
		else if (nArgs > 6) {
			System.out.println("Too many arguments!");
			System.out.printf("Usage: java %s host port%n", EyeApp.class.getSimpleName());
			return;
		}
		
		
		try {
			final String zooHost = args[0];
			final String zooPort = args[1];
			final String cameraName = args[2];
			final double longitude = Double.parseDouble(args[3]);
			final double latitude = Double.parseDouble(args[4]);
			int replicNumber = -1;
			if (nArgs == 6)
				replicNumber = Integer.parseInt(args[5]);
			boolean reconecting = false;
			try (Scanner scanner = new Scanner(System.in)) {
				//Creating a new camera and connecting to the frontend
				CamJoinRequest camJoinRequest = CamJoinRequest.newBuilder().setCoordinates(
						Coordinates
								.newBuilder()
								.setLatitude(latitude)
								.setLongitude(longitude)
								.build())
						.setCameraName(cameraName)
						.build();

				SiloFrontend frontend = new SiloFrontend(zooHost, zooPort, replicNumber);
				frontend.camJoin(camJoinRequest);
				ReportRequest.Builder reportRequest = ReportRequest.newBuilder().setCameraName(cameraName);
				String line = "";
				String[] fields = line.split(" ");

				while(true){

					try {
						if(reconecting) {
							//if there are problems connecting to the frontend, eye try to reconnect to a random replica
							frontend = new SiloFrontend(zooHost, zooPort, -1);
							reconecting = false;
							frontend.camJoin(camJoinRequest); //and do a new cam join request
						} else {
							line = scanner.nextLine();
							fields = line.split(",");
						}
						if (fields[0].equals(CMD_WAIT)) {
							Thread.sleep(Integer.parseInt(fields[1]));
						} else if (line.isEmpty()) {

							frontend.report(reportRequest.build());
							reportRequest.clearEyeObservations();
						} else if (line.startsWith(CMD_COMMENT)) {
							// comment
						} else if (fields.length == 2) {
							makeObservation(reportRequest, fields);
						} else {
							System.out.println("Invalid Command!!!");
							break;
						}
					} catch (NoSuchElementException e){
						System.out.println(e.getMessage());
						frontend.report(reportRequest.build());
						reportRequest.clearEyeObservations();
						break;
					} catch (InterruptedException e){
						System.out.println(e.getMessage());
						break;
					} catch (StatusRuntimeException e){
						if( e.getStatus().getCode() == UNAVAILABLE || e.getStatus().getCode() == DEADLINE_EXCEEDED ) {
							//if the frontend isnt available or the time to response ends, the eye try to reconect with another replica
							reconecting = true;
						} else {
							System.out.println(e.getStatus().getDescription());
							reportRequest.clearEyeObservations();
						}
					}
				}
			}catch (StatusRuntimeException e){
				System.out.println(e.getStatus().getDescription());			}
		} catch(ZKNamingException e ) {
			System.out.println(e.getMessage());

		} finally {
			System.out.println("Closing eye");
		}
	}

	/**
	 * Create an eye observation
	 * @param reportRequest
	 * @param fields
	 */
	private static void makeObservation(ReportRequest.Builder reportRequest, String[] fields) {
		String type = fields[0];
		String id = fields[1];

		ReportRequest.EyeObservation newReport = ReportRequest
				.EyeObservation
				.newBuilder()
				.setType(type)
				.setId(id)
				.build();

		reportRequest.addEyeObservations(newReport);
	}
}
