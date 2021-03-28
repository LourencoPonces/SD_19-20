package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SiloFrontend implements AutoCloseable {

	private final ManagedChannel _channel;
	private final SiloGrpc.SiloBlockingStub _stub;
	private final ZKNaming _zkNaming;
	private final Random _rand = new Random();
	private Collection<ZKRecord> _recordsCollection;
	private ZKRecord[] _records;
	private ZKRecord _record;
	private String _zooPath = "/grpc/sauron/silo";
	private int deadlineMs = 2000;

	public SiloFrontend(String host, String port, int replicNumber) throws ZKNamingException {
		this._zkNaming = new ZKNaming(host, port);

		//Process done during the reconnect
		if (replicNumber == -1){
			this._recordsCollection = _zkNaming.listRecords(_zooPath);
			int size = _recordsCollection.size();
			this._records = _recordsCollection.toArray(new ZKRecord[size]);
			int index = _rand.nextInt(size); //chooses randomly
			this._record = _records[index];
		}
		//Process done when there is a replica number previously sent to the frontend
		else {
			String index = String.valueOf(replicNumber);
			index = "/" + index;
			this._zooPath = _zooPath.concat(index);
			try {
				_record = _zkNaming.lookup(_zooPath);
			}
			catch (ZKNamingException e){
				System.out.println("There is no replica with number " + replicNumber + "!");
				System.exit(-1);
			}
		}

		String target = _record.getURI();
		this._channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		this._stub = SiloGrpc.newBlockingStub(_channel);
	}

	/**
	 * Executes the cam join operation
	 * @param request
	 * @return
	 */
	public CamJoinResponse camJoin(CamJoinRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).camJoin(request); }

	/**
	 * Executes the cam info operation
	 * @param request
	 * @return
	 */
	public CamInfoResponse camInfo(CamInfoRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).camInfo(request); }

	/**
	 * Executes the report operation
	 * @param request
	 * @return
	 */
	public ReportResponse report(ReportRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).report(request); }

	/**
	 * Executes the track operation
	 * @param request
	 * @return
	 */
	public TrackResponse track(TrackRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).track(request); }

	/**
	 * Executes the trackMatch operation
	 * @param request
	 * @return
	 */
	public TrackMatchResponse trackMatch(TrackMatchRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).trackMatch(request); }

	/**
	 * Executes the trace operation
	 * @param request
	 * @return
	 */
	public TraceResponse trace(TraceRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).trace(request); }

	/**
	 * Executes the ping operation
	 * @param request
	 * @return
	 */
	public PingResponse ping (PingRequest request) { return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).ping(request); }

	/**
	 * Executes the clear operation
	 * @param request
	 * @return
	 */
	public ClearResponse clear(ClearRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).clear(request); }

	/**
	 * Executes the init operation
	 * @param request
	 * @return
	 */
	public InitResponse init(InitRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).init(request); }

	/**
	 * Closes the channel
	 */
	@Override
	public final void close() { _channel.shutdown(); }
}