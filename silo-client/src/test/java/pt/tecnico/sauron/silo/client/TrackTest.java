package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackTest extends BaseIT{

    // static members
    static SiloFrontend frontend;


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){
        try {
            frontend = new SiloFrontend("localhost", "2181", -1);
        } catch ( ZKNamingException e ){
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
    public void testTrackPerson() {
        TrackResponse response = frontend.track(TrackRequest.newBuilder().setType("person").setId("991006").build());
        assertEquals("alameda", response.getSpotterObservations().getCameraName());
        assertEquals(38.737613, response.getSpotterObservations().getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations().getCoordinates().getLongitude());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations().getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:03Z", time1);
        assertEquals("person", response.getSpotterObservations().getType());
        assertEquals("991006", response.getSpotterObservations().getIdentifier());
    }

    @Test
    public void testTrackCar() {
        TrackResponse response = frontend.track(TrackRequest.newBuilder().setType("car").setId("RA41OH").build());
        assertEquals("alameda", response.getSpotterObservations().getCameraName());
        assertEquals(38.737613, response.getSpotterObservations().getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations().getCoordinates().getLongitude());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations().getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:00Z", time1);
        assertEquals("car", response.getSpotterObservations().getType());
        assertEquals("RA41OH", response.getSpotterObservations().getIdentifier());
    }

    @Test
    public void testNoFoundPersonTrack() {
        TrackResponse response = frontend.track(TrackRequest.newBuilder().setType("person").setId("1234").build());
        assertEquals(true, response.getIsEmpty());
    }

    @Test
    public void testNoFoundCarTrack() {
        TrackResponse response = frontend.track(TrackRequest.newBuilder().setType("car").setId("1234AA").build());
        assertEquals(true, response.getIsEmpty());
    }

    @Test
    public void testTrackEmptyId() { assertThrows(RuntimeException.class, () -> frontend.track(TrackRequest.newBuilder().setType("person").build()) ); }

    @Test
    public void testTrackInvalidIdPerson() { assertThrows(RuntimeException.class, () -> frontend.track(TrackRequest.newBuilder().setType("person").setId("1234AA").build())); }

    @Test
    public void testTrackInvalidIdCar() { assertThrows(RuntimeException.class, () -> frontend.track(TrackRequest.newBuilder().setType("car").setId("6969ABC").build())); }

    @Test
    public void testTrackInvalidType() { assertThrows(RuntimeException.class, () -> frontend.track(TrackRequest.newBuilder().setType("cat").setId("1234").build())); }

    @Test
    public void testTrackEmptyType() { assertThrows(RuntimeException.class, () -> frontend.track(TrackRequest.newBuilder().setId("1234").build()) ); }

    @Test
    public void testTrackEmptyRequest() { assertThrows(RuntimeException.class, () -> frontend.track(TrackRequest.newBuilder().build()) ); }

}
