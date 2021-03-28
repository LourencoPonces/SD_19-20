package pt.tecnico.sauron.spotter;

import io.grpc.Status.Code.*;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.spotter.domain.Cache;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.*;
import pt.tecnico.sauron.spotter.domain.Request;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static io.grpc.Status.Code.*;

public class SpotterApp {
	private static final String HELP_CMD = "help";
	private static final String INFO_CMD = "info";
	private static final String SPOT_CMD = "spot";
	private static final String TRAIL_CMD = "trail";
	private static final String CTRL_PING = "ctrl_ping";
	private static final String CTRL_CLEAR = "ctrl_clear";
	private static final String CTRL_INIT = "ctrl_init";
	private static final String CTRL_EXIT = "ctrl_exit";

	private static final Cache _cache = new Cache(4);

	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		final int nArgs = args.length;
		if (nArgs < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", SpotterApp.class.getSimpleName());
			return;
		}
		else if (nArgs > 3) {
			System.out.println("Too many arguments!");
			System.out.printf("Usage: java %s host port%n", SpotterApp.class.getSimpleName());
			return;
		}

		try {
			final String zooHost = args[0];
			final String zooPort = args[1];
			int replicNumber = -1;
			if (nArgs == 3)
				replicNumber = Integer.parseInt(args[2]);
			boolean reconecting = false;

			try {
				Scanner scanner = new Scanner(System.in);
				SiloFrontend frontend = new SiloFrontend(zooHost, zooPort, replicNumber);
				String line = "";
				String[] fields = line.split(" ");
				List<SpotterObservation> list;
				List<Integer> vector;
				while (true) {
					if(reconecting) {
						frontend = new SiloFrontend(zooHost, zooPort, -1);
						reconecting = false;
					}
					else {
						line = scanner.nextLine();
						fields = line.split(" ");
					}
					try {
						//receives the call for a certain operation and procedes to do it
						if (HELP_CMD.equals(line)) {
							printHelpMenu();
						} else if (INFO_CMD.equals(fields[0]) && fields.length == 2) {    //INFO CAM CALL
							CamInfoResponse response = frontend.camInfo(CamInfoRequest.newBuilder().setCameraName(fields[1]).build());
							printCamInfoResponse(response);
						} else if (SPOT_CMD.equals(fields[0]) && fields.length == 3) {
							if (!hasAsterisk(fields[2])) { 								  //TRACK CALL
								TrackResponse response = frontend.track(TrackRequest.newBuilder().setType(fields[1]).setId(fields[2]).build());
								list = new ArrayList<>();
								if(!response.getIsEmpty()){
									list.add((response.getSpotterObservations()));
								}
								Request res = _cache.searchRequest(new Request(response.getVectorClockList(), list, fields[2], fields[1],  fields[0] ));
								printSearchResponse(res.getSpotterObservations());
							} else {													  //TRACKMATCH CALL
								TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType(fields[1]).setId(fields[2]).build());
								Request req = _cache.searchRequest(new Request(response.getVectorClockList(), response.getSpotterObservationsList(), fields[2], fields[1],  fields[0] ));
								printSearchResponse(req.getSpotterObservations());
							}
						} else if (TRAIL_CMD.equals(fields[0]) && fields.length == 3) {   //TRACE CALL
							TraceResponse response = frontend.trace(TraceRequest.newBuilder().setType(fields[1]).setId(fields[2]).build());
							Request req = _cache.searchRequest(new Request(response.getVectorClockList(), response.getSpotterObservationsList(), fields[2], fields[1],  fields[0] ));
							printSearchResponse(req.getSpotterObservations());
						} else if (CTRL_INIT.equals(line)) {              				  //CTRL_INIT CALL
							frontend.init(InitRequest.newBuilder().build());
						} else if (CTRL_CLEAR.equals(line)) { 							  //CTRL_CLEAR CALL
							frontend.clear(ClearRequest.newBuilder().build());
						} else if (CTRL_PING.equals(line)) { 							  //CTRL_PING CALL
							PingResponse response = frontend.ping(PingRequest.newBuilder().setInputText("Spotter").build());
							System.out.println(response.getOutputText());
						} else if (CTRL_EXIT.equals(line)) {
							break;
						} else {
							System.out.println("Invalid command!!!");
						}
					} catch (StatusRuntimeException e) {
						if(e.getStatus().getCode() == UNAVAILABLE || e.getStatus().getCode() == DEADLINE_EXCEEDED ) {
							//if the frontend isnt available or the time to response ends, the spotter try to reconect with another replica
							reconecting = true;
						}
						else {
							System.out.println(e.getStatus().getDescription());
						}
					}
				}
			} catch(ZKNamingException e ) {
				System.out.println(e.getMessage());
			} finally {
				System.out.println("Closing spotter");
			}

		} catch(NumberFormatException e){
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Prints the help menu to the console
	 */
	private static void printHelpMenu(){
		System.out.println("--------------- Help Menu ---------------");
		System.out.println("Commands you can do:");
		System.out.println("\t spot 'type' 'id'");
		System.out.println("\t trail 'type' 'id'");
		System.out.println("\t ctrl_init 'host' 'port'");
		System.out.println("\t ctrl_clear");
		System.out.println("\t ctrl_ping");
		System.out.println("\t exit");
		System.out.println("-----------------------------------------");

	}

	/**
	 * Verifies if the id is partial or not
	 * @param id
	 * @return boolean
	 */
	private static boolean hasAsterisk(String id) { return id.contains("*"); }

	/**
	 * Prints the response of the search made by the spotter
	 * @param resp
	 */
	private static void printSearchResponse(List<SpotterObservation> resp) {
		if(resp.size() == 0) {
			System.out.println();
		}
		for (SpotterObservation res : resp) {
			try {
				String time = Instant.ofEpochSecond(res.getDateHour().getSeconds()).toString();
				System.out.println(res.getType() + "," + res.getIdentifier() + "," + time.substring(0, time.length() - 1) + "," + res.getCameraName() + "," + res.getCoordinates().getLatitude() + "," + res.getCoordinates().getLongitude());
			}
			catch(DateTimeException e){
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Prints the response of a cam info
	 * @param response
	 */
	private static void printCamInfoResponse(CamInfoResponse response) {
		System.out.println(response.getCoordinates().getLatitude() + " " + response.getCoordinates().getLongitude());
	}
}

