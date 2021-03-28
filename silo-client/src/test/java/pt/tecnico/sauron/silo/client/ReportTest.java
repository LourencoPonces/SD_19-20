package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.*;

import javax.print.attribute.standard.RequestingUserName;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReportTest extends BaseIT {

    // Static Members
    private static SiloFrontend frontend;

    // One-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp() {
        try {
            frontend = new SiloFrontend("localhost", "2181", -1);
        } catch (ZKNamingException e ){
            System.out.println(e.getMessage());
        }

        InitRequest initRequest = InitRequest.newBuilder().build();
        InitResponse init = frontend.init(initRequest);
    }

    @AfterAll
    public static void oneTimeTearDown() { 
        ClearRequest clearRequest = ClearRequest.newBuilder().build();
		ClearResponse clear = frontend.clear(clearRequest);
    }


    // Initialization and clean-up for each test
    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {

    }


    // Tests
    @Test
    public void makeWrongCameraNameReport(){
        String cameraName = "cam";
        String type = "person";
        String id = "123456";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeOnePersonReport(){
        String cameraName = "alameda";
        String type = "person";
        String id = "123456";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        ReportResponse response = frontend.report(report);
        assertTrue(response.isInitialized());
    }

    @Test
    public void makeOneCarReport() {
        String cameraName = "alameda";
        String type = "car";
        String id = "36NJ70";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        ReportResponse response = frontend.report(report);
        assertTrue(response.isInitialized());
    }

    @Test
    public void makeWrongTypeReport() {
        String cameraName = "alameda";
        String type = "wrongType";
        String id = "36NJ70";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);
        
        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeWrongCarIdReport() {
        String cameraName = "alameda";
        String type = "car";
        String id = "123456";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeWrongPersonIdEmptyReport() {
        String cameraName = "alameda";
        String type = "person";
        String id = "";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeWrongPersonIdLetersReport() {
        String cameraName = "alameda";
        String type = "person";
        String id = "AAAAA";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeWrongCaIdFormatReport() {
        String cameraName = "alameda";
        String type = "car";
        String id = "A1A1A1";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeWrongCarIdNumbersReport() {
        String cameraName = "alameda";
        String type = "car";
        String id = "111";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeWrongCarIdLowerReport(){
        String cameraName = "alameda";
        String type = "car";
        String id = "aa11aa";

        ReportRequest.EyeObservation observation = makeObservation(type, id);
        ReportRequest report = makeReport(cameraName, observation);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }

    @Test
    public void makeSeveralObservationsReport(){
        String cameraName = "alameda";

        List<ReportRequest.EyeObservation> observations = new ArrayList<>();
        ReportRequest.EyeObservation observation;
        String type = "person";
        String id;

        for (int i = 0; i < 100; i++){
            id = String.valueOf(i);
            observation = makeObservation(type, id);
            observations.add(observation);
        }

        ReportRequest report = makeReport(cameraName, observations);

        ReportResponse response = frontend.report(report);
        assertTrue(response.isInitialized());
    }

    @Test
    public void makeWrongSeveralObservationsReport(){
        String cameraName = "alameda";

        List<ReportRequest.EyeObservation> observations = new ArrayList<>();
        ReportRequest.EyeObservation observation;
        String type = "person";
        String id;

        for (int i = 0; i < 100; i++){
            id = String.valueOf(i);
            observation = makeObservation(type, id);
            observations.add(observation);
        }

        id = "a";
        observation = makeObservation(type, id);
        observations.add(observation);

        ReportRequest report = makeReport(cameraName, observations);

        assertThrows(RuntimeException.class, () -> frontend.report(report));
    }


    // Aux Methods
    private ReportRequest.EyeObservation makeObservation(String type, String id){
        ReportRequest.EyeObservation observation = ReportRequest
                .EyeObservation
                .newBuilder()
                .setType(type)
                .setId(id)
                .build();
        return observation;
    }

    private ReportRequest makeReport(String cameraName, ReportRequest.EyeObservation observation){
        ReportRequest report = ReportRequest
                .newBuilder()
                .setCameraName(cameraName)
                .addEyeObservations(observation)
                .build();
        return report;
    }

    private ReportRequest makeReport(String cameraName, List<ReportRequest.EyeObservation> observations){
        ReportRequest.Builder report = ReportRequest
                .newBuilder()
                .setCameraName(cameraName);

        for (int i = 0; i < observations.size(); i++){
            report.addEyeObservations(observations.get(i));
        }

        return report.build();
    }
}
