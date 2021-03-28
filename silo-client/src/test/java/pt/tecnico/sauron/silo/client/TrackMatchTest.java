package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackMatchTest extends BaseIT{

    // static members
    static SiloFrontend frontend;


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){
        try {
            frontend = new SiloFrontend("localhost", "2181", 1);
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
    public void testTrackMatchPersonBegin() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("9915*").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(1,  size);
        assertEquals("alameda", response.getSpotterObservations(0).getCameraName());
        assertEquals(38.737613, response.getSpotterObservations(0).getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations(0).getCoordinates().getLongitude());
        assertEquals("person", response.getSpotterObservations(0).getType());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:05Z", time1);
        assertEquals("991556", response.getSpotterObservations(0).getIdentifier());
    }

    @Test
    public void testTrackMatchPersonMiddle() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("9915*6").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(1,  size);
        assertEquals("alameda", response.getSpotterObservations(0).getCameraName());
        assertEquals(38.737613, response.getSpotterObservations(0).getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations(0).getCoordinates().getLongitude());
        assertEquals("person", response.getSpotterObservations(0).getType());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:05Z", time1);
        assertEquals("991556", response.getSpotterObservations(0).getIdentifier());
    }

    @Test
    public void testTrackMatchPersonEnd() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("*56").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(1,  size);
        assertEquals("alameda", response.getSpotterObservations(0).getCameraName());
        assertEquals(38.737613, response.getSpotterObservations(0).getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations(0).getCoordinates().getLongitude());
        assertEquals("person", response.getSpotterObservations(0).getType());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:05Z", time1);
        assertEquals("991556", response.getSpotterObservations(0).getIdentifier());
    }


    @Test
    public void testTrackMatchPersonsBegin() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("991*").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("person", response.getSpotterObservations(i).getType());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:03Z", time1);
        assertEquals("1999-12-14T11:55:05Z", time2);
        assertEquals("991006", response.getSpotterObservations(0).getIdentifier());
        assertEquals("991556", response.getSpotterObservations(1).getIdentifier());
    }

    @Test
    public void testTrackMatchPersonsMiddle() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("991*6").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("person", response.getSpotterObservations(i).getType());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:03Z", time1);
        assertEquals("1999-12-14T11:55:05Z", time2);
        assertEquals("991006", response.getSpotterObservations(0).getIdentifier());
        assertEquals("991556", response.getSpotterObservations(1).getIdentifier());
    }

    @Test
    public void testTrackMatchPersonsEnd() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("*6").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("person", response.getSpotterObservations(i).getType());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:03Z", time1);
        assertEquals("1999-12-14T11:55:05Z", time2);
        assertEquals("991006", response.getSpotterObservations(0).getIdentifier());
        assertEquals("991556", response.getSpotterObservations(1).getIdentifier());
    }

    @Test
    public void testTrackMatchCarBegin() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("RA0*").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(1,  size);
        assertEquals("alameda", response.getSpotterObservations(0).getCameraName());
        assertEquals(38.737613, response.getSpotterObservations(0).getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations(0).getCoordinates().getLongitude());
        assertEquals("car", response.getSpotterObservations(0).getType());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:01Z", time1);
        assertEquals("RA00TH", response.getSpotterObservations(0).getIdentifier());
    }

    @Test
    public void testTrackMatchCarMiddle() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("RA0*H").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(1,  size);
        assertEquals("alameda", response.getSpotterObservations(0).getCameraName());
        assertEquals(38.737613, response.getSpotterObservations(0).getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations(0).getCoordinates().getLongitude());
        assertEquals("car", response.getSpotterObservations(0).getType());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:01Z", time1);
        assertEquals("RA00TH", response.getSpotterObservations(0).getIdentifier());
    }

    @Test
    public void testTrackMatchCarEnd() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("*TH").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(1,  size);
        assertEquals("alameda", response.getSpotterObservations(0).getCameraName());
        assertEquals(38.737613, response.getSpotterObservations(0).getCoordinates().getLatitude());
        assertEquals( 9.303164, response.getSpotterObservations(0).getCoordinates().getLongitude());
        assertEquals("car", response.getSpotterObservations(0).getType());
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:01Z", time1);
        assertEquals("RA00TH", response.getSpotterObservations(0).getIdentifier());
    }


    @Test
    public void testTrackMatchCarsBegin() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("RA*").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("car", response.getSpotterObservations(i).getType());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:01Z", time1);
        assertEquals("1999-12-14T11:55:00Z", time2);
        assertEquals("RA00TH", response.getSpotterObservations(0).getIdentifier());
        assertEquals("RA41OH", response.getSpotterObservations(1).getIdentifier());

    }

    @Test
    public void testTrackMatchCarsMiddle() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("RA*H").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("car", response.getSpotterObservations(i).getType());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:01Z", time1);
        assertEquals("1999-12-14T11:55:00Z", time2);
        assertEquals("RA00TH", response.getSpotterObservations(0).getIdentifier());
        assertEquals("RA41OH", response.getSpotterObservations(1).getIdentifier());
    }

    @Test
    public void testTrackMatchCarsEnd() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("*H").build());
        int size =  response.getSpotterObservationsList().size();
        assertEquals(2,  size);
        for ( int i = 0; i < size; i++ ){
            assertEquals("alameda", response.getSpotterObservations(i).getCameraName());
            assertEquals(38.737613, response.getSpotterObservations(i).getCoordinates().getLatitude());
            assertEquals( 9.303164, response.getSpotterObservations(i).getCoordinates().getLongitude());
            assertEquals("car", response.getSpotterObservations(i).getType());
        }
        String time1 = Instant.ofEpochSecond(response.getSpotterObservations(0).getDateHour().getSeconds()).toString();
        String time2 = Instant.ofEpochSecond(response.getSpotterObservations(1).getDateHour().getSeconds()).toString();
        assertEquals("1999-12-14T11:55:01Z", time1);
        assertEquals("1999-12-14T11:55:00Z", time2);
        assertEquals("RA00TH", response.getSpotterObservations(0).getIdentifier());
        assertEquals("RA41OH", response.getSpotterObservations(1).getIdentifier());
    }

    @Test
    public void testNoFoundPersonTrackMatch() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("1234").build());
        assertEquals(0, response.getSpotterObservationsList().size());
    }

    @Test
    public void testNoFoundCarTrackMatch() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("1234AA").build() );
        assertEquals(0, response.getSpotterObservationsList().size());
    }


    @Test
    public void testTrackMatchEmptyId() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").build());
        assertEquals(0, response.getSpotterObservationsList().size());
    }

    @Test
    public void testTrackMatchInvalidIdPerson() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("person").setId("1234AA").build());
        assertEquals(0, response.getSpotterObservationsList().size());
    }

    @Test
    public void testTrackMatchInvalidIdCar() {
        TrackMatchResponse response = frontend.trackMatch(TrackMatchRequest.newBuilder().setType("car").setId("6969ABC").build());
        assertEquals(0, response.getSpotterObservationsList().size());
    }

    @Test
    public void testTrackMatchInvalidType() { assertThrows(RuntimeException.class, () -> frontend.trackMatch(TrackMatchRequest.newBuilder().setType("cat").setId("1234").build())); }

    @Test
    public void testTrackMatchEmptyType() { assertThrows(RuntimeException.class, () -> frontend.trackMatch(TrackMatchRequest.newBuilder().setId("1234").build()) ); }

    @Test
    public void testTrackMatchEmptyRequest() { assertThrows(RuntimeException.class, () -> frontend.trackMatch(TrackMatchRequest.newBuilder().build()) ); }

}
