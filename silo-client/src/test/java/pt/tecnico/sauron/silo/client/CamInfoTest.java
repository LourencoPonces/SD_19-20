package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import java.lang.RuntimeException;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.CamInfoRequest;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CamInfoTest extends BaseIT {

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

    // tests


    @Test
    public void testSetCamInfoNullName() {
        CamInfoRequest request = CamInfoRequest.newBuilder().
                build();

        assertThrows(RuntimeException.class, () -> frontend.camInfo(request));
    }

    @Test
    public void testSetCamInfoNotFound() {
        final String cameraName = "Leica";
        CamInfoRequest request = CamInfoRequest.newBuilder().
                setCameraName(cameraName).
                build();

        assertThrows(RuntimeException.class, () -> frontend.camInfo(request));
    }

    @Test
    public void testSetCamInfoCoords() {
        final String cameraName = "Kodak";
        final Double latitude = 38.737613;
        final Double longitude = 9.303164;
        CamJoinRequest request1 = CamJoinRequest.newBuilder().setCoordinates(
                Coordinates
                        .newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build())
                .setCameraName(cameraName)
                .build();

        frontend.camJoin(request1);
        CamInfoRequest request2 = CamInfoRequest.newBuilder().
                setCameraName(cameraName).
                build();

        assertEquals(latitude, frontend.camInfo(request2).getCoordinates().getLatitude());
        assertEquals(longitude, frontend.camInfo(request2).getCoordinates().getLongitude());
    }
}