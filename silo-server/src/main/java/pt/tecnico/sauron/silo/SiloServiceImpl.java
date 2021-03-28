package pt.tecnico.sauron.silo;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.*;
import pt.tecnico.sauron.silo.domain.exception.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.time.*;
import java.util.*;

import static io.grpc.Status.*;

public class SiloServiceImpl extends SiloGrpc.SiloImplBase {
    private final DomainRoot _domainRoot;

    public SiloServiceImpl( String n, String total){
        _domainRoot = new DomainRoot(Integer.parseInt(n), Integer.parseInt(total));
}
    /**
     * Receives a camJoinRequest and creates a camJoinResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void camJoin(CamJoinRequest request, StreamObserver<CamJoinResponse> responseObserver) {
        try {
            _domainRoot.camJoin(request.getCameraName(), request.getCoordinates().getLatitude(), request.getCoordinates().getLongitude());
            CamJoinResponse response = CamJoinResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }
        catch (WrongCameraNameException | WrongCameraCoordinatesException | WrongCoordinatesException  e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Receives a camInfoRequest and creates a camInfoResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void camInfo(CamInfoRequest request, StreamObserver<CamInfoResponse> responseObserver) {
        try {
            Camera cam = _domainRoot.camInfo(request.getCameraName());
            CamInfoResponse response = CamInfoResponse.newBuilder().setCoordinates(
                    Coordinates
                        .newBuilder()
                        .setLatitude(cam.getLatitude())
                        .setLongitude(cam.getLongitude())
                        .build())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (WrongCameraNameException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
        catch (CamNotFoundException e){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    /**
     * Receives a reportRequest and returns a reportResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {
        String cameraName = request.getCameraName();
        try {
            Camera cam = _domainRoot.camInfo(cameraName);
            _domainRoot.report(request.getEyeObservationsList(), cam);
            ReportResponse response = ReportResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch(InvalidIdException |  WrongCameraNameException e ) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
        catch (TypeNotFoundException | CamNotFoundException e){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Receives a trackRequest and returns a trackResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void track(TrackRequest request, StreamObserver<TrackResponse> responseObserver) {
        TrackResponse.Builder response = TrackResponse.newBuilder();
        try {
            Observation obs = _domainRoot.track(request.getId(), request.getType());
            response.setIsEmpty(false);
            response.addAllVectorClock(_domainRoot.getVectorClock().getVectorClock());
            response.setSpotterObservations(createSpotterObservationResponse(obs));
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch( TypeNotFoundException | InvalidIdException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (IdNotFoundException e){
            response.setIsEmpty(true);
            response.addAllVectorClock(_domainRoot.getVectorClock().getVectorClock());
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    /**
     * Receives a trackMatchRequest and returns a trackMatchResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void trackMatch(TrackMatchRequest request, StreamObserver<TrackMatchResponse> responseObserver) {
        TrackMatchResponse.Builder response = TrackMatchResponse.newBuilder();
        try {
            List<Observation> obs = _domainRoot.trackMatch(request.getId(), request.getType());
            Collections.sort(obs, (o1, o2) -> o1.getType().getId().compareTo(o2.getType().getId()));
            for (Observation ob : obs) {
                response.addSpotterObservations(createSpotterObservationResponse(ob));
            }
            trackMatchAux(responseObserver, response);
        } catch( TypeNotFoundException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch(IdNotFoundException e ) {
            trackMatchAux(responseObserver, response);
        }
    }

    /**
     * Aux method for trackMatch in order to keep code more readable
     * @param responseObserver
     * @param response
     */
    private void trackMatchAux(StreamObserver<TrackMatchResponse> responseObserver, TrackMatchResponse.Builder response) {
        response.addAllVectorClock(_domainRoot.getVectorClock().getVectorClock());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    /**
     * Receives a traceRequest and returns a traceResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void trace(TraceRequest request, StreamObserver<TraceResponse> responseObserver) {
        TraceResponse.Builder response = TraceResponse.newBuilder();
        try {
            List<Observation> obs = _domainRoot.trace(request.getId(), request.getType());
            for(Observation ob : obs){
                response.addSpotterObservations(createSpotterObservationResponse(ob));
            }
            traceAux(responseObserver, response);
        } catch( InvalidIdException |  TypeNotFoundException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch( IdNotFoundException e ) {
            traceAux(responseObserver, response);
        }
    }

    /**
     * Aux method for trace in order to keep code more readable
     * @param responseObserver
     * @param response
     */
    private void traceAux(StreamObserver<TraceResponse> responseObserver, TraceResponse.Builder response) {
        response.addAllVectorClock(_domainRoot.getVectorClock().getVectorClock());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    /**
     * Receives a pingRequest and returns a pingResponse
     * @param request
     * @param responseObserver
     */
    @Override 
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver){
        String output = pingServer(request);
        PingResponse response = PingResponse.newBuilder()
            .setOutputText(output)
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Returns the answer to be passed to the pingResponse
     * @param request
     * @return String
     */
    private String pingServer(PingRequest request){
        String input = request.getInputText();
        return "Hello " + input + "!";
    }

    /**
     * Receives a clearRequest and returns a clearResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void clear(ClearRequest request, StreamObserver<ClearResponse> responseObserver) {
        _domainRoot.clearServer();
        ClearResponse response = ClearResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Receives a initRequest and returns a initResponse
     * @param request
     * @param responseObserver
     */
    @Override
    public void init(InitRequest request, StreamObserver<InitResponse> responseObserver) {
        try {
            _domainRoot.initServer();
            InitResponse response = InitResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (WrongCameraNameException | WrongCoordinatesException  e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Receives a gossipRequest and returns a gossipResponse with the list of observations added
     * @param request
     * @param responseObserver
     */
    @Override
    public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
        ArrayList<Observation> obs = _domainRoot.gossip(request.getVectorList());
        GossipResponse.Builder resp = GossipResponse.newBuilder();
        for(Observation ob : obs) {
            resp.addObservations(createGossipObservationResponse(ob));
        }
        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();
    }

    /**
     * Starts and completes the gossip process
     * @param _zkNaming
     * @param _replicNumber
     */
    public void run(ZKNaming _zkNaming, String _replicNumber) {
        GossipFrontend gossipFrontend = null;
        try {
            Collection<ZKRecord> records = _zkNaming.listRecords("/grpc/sauron/silo");
            ZKRecord[] recordsArray = records.toArray(new ZKRecord[records.size()]);
            //Random order in the choice of the replicas
            List<ZKRecord> recordsList = Arrays.asList(recordsArray);
            Collections.shuffle(recordsList);
            recordsList.toArray(recordsArray);
            //Initializing the gossip of the replica
            System.out.println("Replica " + _replicNumber + " initiating gossip...");
            for (ZKRecord rec : recordsArray) {
                String[] path = rec.getPath().split("/");
                if(!path[path.length - 1].equals(_replicNumber)) {
                    System.out.println("Contacting replica " + path[path.length - 1] + " at localhost:808" + path[path.length - 1] + "...");
                    //Making a gossip request and getting the response
                    gossipFrontend = new GossipFrontend(rec);
                    GossipRequest req = createGossipRequest();
                    GossipResponse resp = gossipFrontend.gossip(req);
                    List<GossipObservation> obs = resp.getObservationsList();
                    //Saving the observations given by the response
                    for(GossipObservation ob : obs) {
                        Instant time = conversionInstant(ob.getDateHour());
                        GossipObservationToString(ob, time);
                        _domainRoot.save(ob, time);
                    }
                    gossipFrontend.close();
                }
            }
        } catch(ZKNamingException | WrongCameraNameException | WrongCameraCoordinatesException | WrongCoordinatesException e ){
            System.out.println("Caught exception " + e.getMessage() + " during the gossip process.");
        } catch( StatusRuntimeException e ){
            System.out.println("Problems connecting to the replica");
            gossipFrontend.close();
        }
    }

    /**
     * Prints to the console information related to the received observation after gossip
     * @param obs
     * @param time
     */
    private void GossipObservationToString(GossipObservation obs, Instant time) {
        System.out.println("\nFrontend received observation: ");
        System.out.println("Id of the " + obs.getType() + ": " + obs.getIdentifier());
        System.out.println("Camera: " + obs.getCameraName());
        System.out.println("Time: " + time + "\n");
    }

    /**
     * Converts instant into timestamp
     * @param instant
     * @return Timestamp
     */
    private Timestamp conversionTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    /**
     * Converts timestamp into instant
     * @param time
     * @return Instant
     */
    private Instant conversionInstant(Timestamp time) {
        return Instant.ofEpochSecond(time.getSeconds(), time.getNanos());
    }

    /**
     * Creates a spotterObservationRequest based on an observation
     * @param observ
     * @return SpotterObservation
     */
    private synchronized SpotterObservation createSpotterObservationResponse(Observation observ) {
        return SpotterObservation.newBuilder()
                .setType(observ.getType().toString())
                .setIdentifier(observ.getType().getId())
                .setDateHour(conversionTimestamp(observ.getDate()))
                .setCameraName(observ.getCamera().getName())
                .setCoordinates(
                        Coordinates
                                .newBuilder()
                                .setLatitude(observ.getCamera().getLatitude())
                                .setLongitude(observ.getCamera().getLongitude())
                                .build())
                .build();
    }

    /**
     * Creates a gossipObservationResponse based on an observation
     * @param observ
     * @return GossipObservation
     */
    private synchronized GossipObservation createGossipObservationResponse(Observation observ) {
        return GossipObservation.newBuilder()
                .setType(observ.getType().toString())
                .setIdentifier(observ.getType().getId())
                .setDateHour(conversionTimestamp(observ.getDate()))
                .setCameraName(observ.getCamera().getName())
                .setReplic(observ.getReplic())
                .setSeqN(observ.getSeqN())
                .setCoordinates(
                        Coordinates
                                .newBuilder()
                                .setLatitude(observ.getCamera().getLatitude())
                                .setLongitude(observ.getCamera().getLongitude())
                                .build())
                .build();
    }

    /**
     * Creates a gossipRequest
     * @return GossipRequest
     */
    private GossipRequest createGossipRequest(){
        VectorClock vector = _domainRoot.getVectorClock();
        System.out.println("Sending:");
        vector.printVector();
        return GossipRequest.newBuilder().addAllVector(vector.getVectorClock()).build();
    }
}