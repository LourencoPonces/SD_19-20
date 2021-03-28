package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CamJoinTest extends BaseIT {

    // static members
    static SiloFrontend frontend;

    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){
        try {
            frontend = new SiloFrontend("localhost", "2181", -1);
        } catch (ZKNamingException e ){
            System.out.println(e.getMessage());
        }
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
    public void testSetCamJoinNullName() {
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidNameMin() {
        final String cameraName = "aa";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidNameNonAlphanumeric() {
        final String cameraName = "alameda@";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidNameMax() {
        final String cameraName = "thisisaverybigname";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinValidNameMin() {
        final String cameraName = "abc";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        CamJoinResponse response = frontend.camJoin(request);
        assertTrue(response.isInitialized());
    }

    @Test
    public void testSetCamJoinValidNameMax() {
        final String cameraName = "abcdefghijlmnko";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        CamJoinResponse response = frontend.camJoin(request);
        assertTrue(response.isInitialized());
    }

    @Test
    public void testSetCamJoinDupNameDifCoord() {
        final String cameraName = "canonAL1";
        final Double latitude1 = 38.737613;
        final Double longitude1 = 9.303164;
        final Double latitude2 = 20.737613;
        final Double longitude2 = 19.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude1)
                        .setLongitude(longitude1)
                        .build())
                .setCameraName(cameraName)
                .build();

        CamJoinResponse response1 = frontend.camJoin(request);
        assertTrue(response1.isInitialized());

        CamJoinRequest request2 =  CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude2)
                        .setLongitude(longitude2)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request2));

    }

    @Test
    public void testSetCamJoinDupNameSameCoord() {
        final String cameraName = "canonAE1";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        CamJoinResponse response1 = frontend.camJoin(request);
        assertTrue(response1.isInitialized());

        CamJoinResponse response = frontend.camJoin(request);
        assertTrue(response.isInitialized());
    }

    @Test
    public void testSetCamJoinInvalidMaxLongitudeAndLatitudeCoords() {
        final String cameraName = "Kodak6";
        final Double latitude = 90.01;
        final Double longitude = 180.01;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidMinLongitudeAndLatitudeCoords() {
        final String cameraName = "Kodak7";
        final Double latitude = -90.01;
        final Double longitude = -180.01;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidMaxLatitudeCoords() {
        final String cameraName = "Kodak2";
        final Double latitude = 90.1;
        final Double longitude = 20.0;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidMinLatitudeCoords() {
        final String cameraName = "Kodak3";
        final Double latitude = -90.01;
        final Double longitude = 20.0;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidMaxLongitudeCoords() {
        final String cameraName = "Kodak4";
        final Double latitude = 20.0;
        final Double longitude = 180.01;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }

    @Test
    public void testSetCamJoinInvalidMinLongitudeCoords() {
        final String cameraName = "Kodak5";
        final Double latitude = 20.0;
        final Double longitude = -180.01;
        CamJoinRequest request = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        assertThrows(RuntimeException.class, () -> frontend.camJoin(request));
    }
}
