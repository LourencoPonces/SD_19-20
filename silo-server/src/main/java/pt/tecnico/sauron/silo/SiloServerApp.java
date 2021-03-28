package pt.tecnico.sauron.silo;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.io.IOException;
import java.util.*; 

public class SiloServerApp {
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(SiloServerApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 6) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", SiloServerApp.class.getName());
			return;
		}

		final String _zooHost = args[0];
		final String _zooPort = args[1];
		final String _currentReplicNumber = args[2];
		final String _host = args[3];
		final String _port = args[4];
		final String _total = args[5];
		int time = 30;
		String _zooPath = "/grpc/sauron/silo/";

		ZKNaming zkNaming = new ZKNaming(_zooHost, _zooPort);
		final BindableService impl = new SiloServiceImpl(_currentReplicNumber, _total);

		try {
			_zooPath = _zooPath.concat(_currentReplicNumber);

			zkNaming.rebind(_zooPath, _host, _port);
			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(Integer.parseInt(_port)).addService(impl).build();

			// Start the server
			server.start();

			// Server threads are running in the background.
			System.out.println("Replica " + _currentReplicNumber + " starting...");

			Timer timer = new Timer();
			timer.schedule(new Gossip(zkNaming, _currentReplicNumber, impl), 0, time*1000);

			new Thread(() -> {
				System.out.println("<Press enter to shutdown>");
				new Scanner(System.in).nextLine();

				server.shutdown();
				timer.cancel();
				timer.purge();
			}).start();
			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();
		} catch(ZKNamingException | IOException e ){
			System.out.println(e.getMessage());
		} finally {
				try {
					zkNaming.unbind(_zooPath, _host, String.valueOf(_port));
					System.out.println("Unbind done");
				} catch (ZKNamingException e) {
					System.out.println(e.getMessage());
				}
		}
	}

	static class Gossip extends TimerTask {
		private String _replicNumber;
		private ZKNaming _zkNaming;
		private SiloServiceImpl _impl;
		public Gossip(ZKNaming zk, String replicNum, BindableService impl){
			_replicNumber = replicNum;
			_zkNaming = zk;
			_impl = (SiloServiceImpl) impl;
		}

		/**
		 * Method to start the entire process of gossip
		 */
		@Override
		public void run() {
			_impl.run(_zkNaming, _replicNumber);
		}
	}
}
