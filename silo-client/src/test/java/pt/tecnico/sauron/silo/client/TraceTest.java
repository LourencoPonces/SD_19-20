package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TraceTest extends BaseIT {
    // static members
    static SiloFrontend frontend ;

    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){
        try {
            frontend = new SiloFrontend("localhost", "2181", -1);
        } catch (ZKNamingException e ){
            System.out.println(e.getMessage());
        }

        InitRequest request = InitRequest.newBuilder().build();
        InitResponse res = frontend.init(request);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        ClearRequest request = ClearRequest.newBuilder().build();
        ClearResponse res = frontend.clear(request);
    }

    // initialization and clean-up for each test

    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {

    }



    @Test
    public void testTrace() {
        TraceResponse response = frontend.trace(TraceRequest.newBuilder().setType("person").setId("991556").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("person", response.getSpotterObservations(i).getType());
            assertEquals("991556", response.getSpotterObservations(i).getIdentifier());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:05Z", time1);
        assertEquals("1999-12-14T11:55:04Z", time2);
    }

    @Test
    public void testNoFoundPersonTrace() {
        TraceResponse response = frontend.trace(TraceRequest.newBuilder().setType("person").setId("1234").build());
        assertEquals(0, response.getSpotterObservationsList().size());
    }

    @Test
    public void testNoFoundCarTrace() {
        TraceResponse response = frontend.trace(TraceRequest.newBuilder().setType("car").setId("1234AA").build());
        assertEquals(0, response.getSpotterObservationsList().size());
    }

    @Test
    public void testTraceEmptyId() { assertThrows(RuntimeException.class, () -> frontend.trace(TraceRequest.newBuilder().setType("person").build()) ); }

    @Test
    public void testTraceInvalidIdPerson() { assertThrows(RuntimeException.class, () -> frontend.trace(TraceRequest.newBuilder().setType("person").setId("1234AA").build())); }

    @Test
    public void testTraceInvalidIdCar() { assertThrows(RuntimeException.class, () -> frontend.trace(TraceRequest.newBuilder().setType("car").setId("6969ABC").build())); }

    @Test
    public void testTraceInvalidType() { assertThrows(RuntimeException.class, () -> frontend.trace(TraceRequest.newBuilder().setType("cat").setId("1234").build())); }

    @Test
    public void testTraceEmptyType() { assertThrows(RuntimeException.class, () -> frontend.trace(TraceRequest.newBuilder().setId("1234").build()) ); }

    @Test
    public void testTraceEmptyRequest() { assertThrows(RuntimeException.class, () -> frontend.trace(TraceRequest.newBuilder().build()) ); }


}
