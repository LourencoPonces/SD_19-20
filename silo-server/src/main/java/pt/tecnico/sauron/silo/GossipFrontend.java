package pt.tecnico.sauron.silo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.concurrent.TimeUnit;

public class GossipFrontend implements AutoCloseable {

    private final ManagedChannel _channel;
    private final SiloGrpc.SiloBlockingStub _stub;
    private int deadlineMs = 1000;

    public GossipFrontend(ZKRecord zkRecord){
        String target = zkRecord.getURI();
        _channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        _stub = SiloGrpc.newBlockingStub(_channel);
    }

    /**
     * Executes the gossip for the request received
     * @param request
     * @return GossipResponse
     */
    public GossipResponse gossip(GossipRequest request){ return _stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS).gossip(request); }

    /**
     * Closes the channel
     */
    @Override
    public final void close() { _channel.shutdown(); }
}
