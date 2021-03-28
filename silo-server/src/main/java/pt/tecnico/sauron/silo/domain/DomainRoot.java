package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.domain.exception.*;
import pt.tecnico.sauron.silo.grpc.GossipObservation;
import pt.tecnico.sauron.silo.grpc.ReportRequest;

import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

public class DomainRoot {
    private static final int MIN_CAMERA_NAME_SIZE = 3;
    private static final int MAX_CAMERA_NAME_SIZE = 15;
    private static final String TYPE_PERSON = "person";
    private static final String TYPE_CAR = "car";

    int _count;
    private int _currentReplicNumber;
    private int _totalReplicNumber;
    private VectorClock _vector;
    private Map<String, List<Observation>> _listObservations = new HashMap<>();
    private Map<String, Camera> _hashCameras = new HashMap<>();
    private Map<Integer, List<Observation>> _listReplicObservations;

    public DomainRoot(int current, int total){
        _currentReplicNumber = current;
        _totalReplicNumber = total;
        _count = 1;
        _vector = new VectorClock(_totalReplicNumber);
        _listReplicObservations = new HashMap<>(_totalReplicNumber);
        for (int i = 1; i <= _totalReplicNumber; i++){
            _listReplicObservations.put(i, new ArrayList<>());
        }
    }

    /**
     * Adds a new camera to the list of existing cameras
     * @param cameraName
     * @param latitude
     * @param longitude
     * @throws WrongCameraNameException
     * @throws WrongCameraCoordinatesException
     * @throws WrongCoordinatesException
     */
    public void camJoin(String cameraName, double latitude, double longitude) throws WrongCameraNameException, WrongCameraCoordinatesException, WrongCoordinatesException {
        addCamera(cameraName, latitude, longitude);
    }

    /**
     * Returns the camera with the given cameraName
     * @param  cameraName
     * @return Camera
     * @throws WrongCameraNameException
     * @throws CamNotFoundException
     */
    public Camera camInfo(String cameraName) throws WrongCameraNameException, CamNotFoundException {
        checkCameraName(cameraName);
        return findCamera(cameraName);
    }

    /**
     * Registers a report done by a specific camera
     * @param reports
     * @param cam
     * @throws InvalidIdException
     * @throws TypeNotFoundException
     */
    public void report(List<ReportRequest.EyeObservation> reports, Camera cam) throws InvalidIdException, TypeNotFoundException{
        for (ReportRequest.EyeObservation rep : reports) {
            registReport(rep, cam);
        }
    }

    /**
     * Returns the most recent observation for an id
     * @param id
     * @param type
     * @return Observation
     * @throws TypeNotFoundException
     * @throws InvalidIdException
     * @throws IdNotFoundException
     */
    public Observation track(String id, String type) throws TypeNotFoundException, InvalidIdException, IdNotFoundException {
        validateAll(id, type);
        Collections.sort(_listObservations.get(id), Collections.reverseOrder());
        return _listObservations.get(id).get(0);
    }

    /**
     * Returns the most recent observation for every single id that matches the partial id given
     * @param id
     * @param type
     * @return List<Observation>
     * @throws TypeNotFoundException
     * @throws IdNotFoundException
     */
    public List<Observation> trackMatch(String id, String type) throws TypeNotFoundException, IdNotFoundException {
        validateType(type);
        List<String> ids = searchPartialIds(id);
        if(ids.isEmpty()) {
            throw new IdNotFoundException();
        }
        List<Observation> obs = new ArrayList<>();
        //creating list with the observations of the ids that match the partial id
        for (String idNew : ids){
            Collections.sort(_listObservations.get(idNew), Collections.reverseOrder());
            if(_listObservations.get(idNew).get(0).getType().toString().equals(type)){
                obs.add(_listObservations.get(idNew).get(0));
            }
        }
        return obs;
    }

    /**
     * Returns all the observations for a specific id ordered from the most recent to oldest
     * @param id
     * @param type
     * @return List<Observation>
     * @throws IdNotFoundException
     * @throws InvalidIdException
     * @throws TypeNotFoundException
     */
    public List<Observation> trace(String id, String type) throws IdNotFoundException, InvalidIdException, TypeNotFoundException {
        validateAll(id, type);
        Collections.sort(_listObservations.get(id), Collections.reverseOrder());
        return _listObservations.get(id);
    }

    /**
     * Clears all the data structures used and resets variables
     */
    public void clearServer() {
        _listObservations.clear();
        _hashCameras.clear();
        _vector.resetVector();
        _count = 1;
    }

    /**
     * Initializes objects needed for the tests
     * @throws WrongCameraNameException
     * @throws WrongCoordinatesException
     */
    public synchronized  void initServer() throws WrongCameraNameException, WrongCoordinatesException{
        Camera camera = new Camera("alameda", 38.737613, 9.303164);
        _hashCameras.put(camera.getName(), camera);

        //setting the specifics of the data
        List<Type> types = createTypeList();
        Instant now = Instant.now();
        ZonedDateTime gmt = now.atZone(ZoneId.of("Europe/Lisbon"));
        ZonedDateTime zonedDateTime = gmt.withHour(11)
                .withMinute(55)
                .withSecond(0)
                .withYear(1999)
                .withMonth(12)
                .withDayOfMonth(14);

        //inserting into the list the information
        for(int i = 0; i < 4; i++){
            List<Observation> newList = new ArrayList<Observation>();
            ZonedDateTime zonedDateTimeTests = zonedDateTime.withSecond(i);
            Instant instantTests = zonedDateTimeTests.toInstant();
            Observation observTests = new Observation(types.get(i), camera, instantTests, _currentReplicNumber, _count);
            newList.add(observTests);
            _count++;
            _listObservations.put(observTests.getType().getId(), newList);
        }

        //This for is needed in order to insert two observations of the same id
        List<Observation> newList2 = new ArrayList<Observation>();
        for(int i = 4; i < 6; i++){
            ZonedDateTime zonedDateTime2 = zonedDateTime.withSecond(i);
            Instant instant2 = zonedDateTime2.toInstant();
            Observation observ2 = new Observation(types.get(i), camera, instant2, _currentReplicNumber, _count);
            newList2.add(observ2);
            _count++;
            if(i == 5){
                _listObservations.put(observ2.getType().getId(), newList2);
            }
        }
    }

    /**
     * Creates list of persons and cars, with the sole purpose of testing
     * @return List<Type>
     */
    private List<Type> createTypeList() {
        List<Type> types = new ArrayList<>();
        Car car1 = new Car("RA41OH");
        Car car2 = new Car("RA00TH");
        Car car3 = new Car("IM94LA");
        Person per1 = new Person("991006");
        Person per2 = new Person("991556");
        types.add(car1);
        types.add(car2);
        types.add(car3);
        types.add(per1);
        types.add(per2);
        types.add(per2);
        return types;
    }

    /**
     * Get the vector clock
     * @return VectorClock
     */
    public VectorClock getVectorClock(){
        return _vector;
    }

    /**
     * Executes the gossip using the vector clock received and returns the observations that are different
     * @param vectorClock
     * @return ArrayList<Observation>
     */
    public synchronized ArrayList<Observation> gossip(List<Integer> vectorClock){
        VectorClock newVecClock = new VectorClock(_totalReplicNumber);
        newVecClock.setVectorClock(vectorClock);
        ArrayList<Observation> obs = new ArrayList<>();
        for(int i = 1; i <= _totalReplicNumber; i++){
            //compares and saves the observations that are in one vector but not the in other in order to send them
            int offset = _vector.compareVectorClock(newVecClock, i);
            if(offset > 0) {
                ArrayList<Observation> obsAux = getLastNObservations(offset, i, newVecClock.getPositionValueVectorClock(i) + 1);
                for (Observation aux : obsAux) {
                    obs.add(aux);
                }
            }
        }
        return obs;
    }

    /**
     * Get the last N Observations using the offset given by the comparision and the index
     * @param offset - number of observations to return
     * @param replic - number of the replica
     * @param index - first index we want to search for
     * @return ArrayList<Observation>
     */
    private synchronized ArrayList<Observation> getLastNObservations(int offset, int replic, int index) {
        ArrayList<Observation> obs = new ArrayList<>();
        List<Observation> obsAux = _listReplicObservations.get(replic);
        Collections.sort(obsAux, Collections.reverseOrder());
        int initInd = index; // saves the value of the first index we want to search for
        // guarantees that we will never look for observations that exceed the number of the first index adeed to the number of observations to return
        while(index < initInd + offset) {
            for (Observation ob : obsAux) {
                if (ob.getSeqN() == index) { // adding the observation when the sequence number is equal to the index
                    obs.add(ob);
                    break;
                }
            }
            index++;
        }
        return obs;
    }

    /**
     * Saves the observations given by the gossip
     * @param report
     * @param time
     * @throws WrongCameraNameException
     * @throws WrongCameraCoordinatesException
     * @throws WrongCoordinatesException
     */
    public void save(GossipObservation report, Instant time) throws WrongCameraNameException, WrongCameraCoordinatesException, WrongCoordinatesException{
        Pattern patCarPer = Pattern.compile("^(person|car)");
        if (patCarPer.matcher(report.getType()).matches()) {
            saveType(report, time);
        }
    }

    /**
     * Saves the observations given by the gossip
     * @param report
     * @param time
     * @throws WrongCameraNameException
     * @throws WrongCameraCoordinatesException
     * @throws WrongCoordinatesException
     */
    private void saveType(GossipObservation report, Instant time) throws WrongCameraNameException, WrongCameraCoordinatesException, WrongCoordinatesException{
        Camera cam = new Camera(report.getCameraName(), report.getCoordinates().getLatitude(), report.getCoordinates().getLongitude());

        //the repetition of code was needed because the obj could not be accessed outside of the if/else
        if (report.getType().equals(TYPE_CAR)) {
            Car obj = new Car(report.getIdentifier());
            Observation obs = new Observation(obj, cam, time, report.getReplic(), report.getSeqN());
            addElementListObservation(report.getIdentifier(), obs);
            _vector.incrementPositionVectorClock(report.getReplic());
        }
        else if (report.getType().equals(TYPE_PERSON)) {
            Person obj = new Person(report.getIdentifier());
            Observation obs = new Observation(obj, cam, time, report.getReplic(), report.getSeqN());
            addElementListObservation(report.getIdentifier(), obs);
            _vector.incrementPositionVectorClock(report.getReplic());
        }
    }

    /**
     * Checks if the camera name given is valid or not
     * @param cameraName
     * @throws WrongCameraNameException
     */
    private void checkCameraName(String cameraName) throws WrongCameraNameException{
        if ((cameraName.length() < MIN_CAMERA_NAME_SIZE || cameraName.length() > MAX_CAMERA_NAME_SIZE) || !cameraName.matches("[A-Za-z0-9]+")){
            throw new WrongCameraNameException();
        }
    }

    /**
     * Checks if a camera with the name has already been registered
     * @param cameraName
     * @return boolean
     */
    private synchronized boolean existsCamera(String cameraName){
        return _hashCameras.containsKey(cameraName);
    }

    /**
     * Checks if there is already a camera with the same coordinates
     * @param cameraName
     * @param latitude
     * @param longitude
     * @throws WrongCameraCoordinatesException
     */
    private synchronized void haveSameCoordinates(String cameraName, double latitude, double longitude) throws WrongCameraCoordinatesException {
        Camera cam = _hashCameras.get(cameraName);
        if (cam.getLatitude() != latitude || cam.getLongitude() != longitude){
            throw new WrongCameraCoordinatesException();
        }
    }

    /**
     * Adds a camera to the list of registered cameras
     * @param cameraName
     * @param latitude
     * @param longitude
     * @throws WrongCameraNameException
     * @throws WrongCoordinatesException
     * @throws WrongCameraCoordinatesException
     */
    public synchronized void addCamera(String cameraName, double latitude, double longitude) throws WrongCameraNameException, WrongCoordinatesException, WrongCameraCoordinatesException{
        if (existsCamera(cameraName)){
            haveSameCoordinates(cameraName, latitude, longitude);
        }
        Camera newCamera = new Camera(cameraName, latitude, longitude);
        _hashCameras.put(cameraName, newCamera);
    }

    /**
     * Returns the camera with the given name
     * @param cameraName
     * @return Camera
     * @throws CamNotFoundException
     */
    private synchronized Camera findCamera(String cameraName) throws CamNotFoundException {
        Camera cam = _hashCameras.get(cameraName);
        if (cam == null) { throw new CamNotFoundException(); }
        return cam;
    }

    /**
     * Validates and registers a new observation inserted by the eye
     * @param rep
     * @param cam
     * @throws TypeNotFoundException
     * @throws InvalidIdException
     */
    private void registReport(ReportRequest.EyeObservation rep, Camera cam) throws TypeNotFoundException, InvalidIdException {
        Pattern patCarPer = Pattern.compile("^(person|car)");
        if (patCarPer.matcher(rep.getType()).matches()) {
            registerType(rep, cam);
        } else {
            throw new TypeNotFoundException();
        }
    }

    /**
     * Regists a new observation inserted by the eye
     * @param rep
     * @param cam
     * @throws TypeNotFoundException
     * @throws InvalidIdException
     */
    private void registerType(ReportRequest.EyeObservation rep, Camera cam) throws TypeNotFoundException, InvalidIdException {
        //the repetition of code was needed because the obj could not be accessed outside of the if/else
        if (rep.getType().equals(TYPE_CAR)) {
            validateId(rep.getId(), rep.getType());
            Car obj = new Car(rep.getId());
            Instant time = getTime();
            Observation obs = new Observation(obj, cam, time, _currentReplicNumber, _count);
            addElementListObservation(rep.getId(), obs);
            _vector.incrementPositionVectorClock(_currentReplicNumber);
            _count++;
        }
        else if (rep.getType().equals(TYPE_PERSON)) {
            validateId(rep.getId(), rep.getType());
            Person obj = new Person(rep.getId());
            Instant time =  getTime();
            Observation obs = new Observation(obj, cam, time, _currentReplicNumber, _count);
            addElementListObservation(rep.getId(), obs);
            _vector.incrementPositionVectorClock(_currentReplicNumber);
            _count++;
        }
        else {
            throw new TypeNotFoundException();
        }
    }

    /**
     * Adds an observation to the list of the id given
     * @param id
     * @param obs
     */
    private synchronized void addElementListObservation(String id, Observation obs){
        List<Observation> list = _listObservations.get(id);
        if (list == null){
            List<Observation> newList = new ArrayList<Observation>();
            newList.add(obs);
            _listObservations.put(id, newList);
        }else {
            _listObservations.get(id).add(obs);
        }
        _listReplicObservations.get(obs.getReplic()).add(obs);
        if(obs.getReplic() == _currentReplicNumber && obs.getSeqN() > _count){
            _count = obs.getSeqN();
        }
    }

    /**
     * Returns all ids that match the partial id given
     * @param id
     * @return List<String>
     */
    private synchronized List<String> searchPartialIds(String id) {
        List<String> ids = new ArrayList<>();
        String replaced = id.replaceAll("\\*", ".*");
        for(String identifier: _listObservations.keySet()) {
            if(identifier.matches(replaced)) {
                ids.add(identifier);
            }
        }
        return ids;
    }

    /**
     * Validates the id and the type given to see if they violate any of the rules and checks if id exists
     * @param id
     * @param type
     * @throws InvalidIdException
     * @throws IdNotFoundException
     * @throws TypeNotFoundException
     */
    private void validateAll(String id, String type) throws InvalidIdException, IdNotFoundException, TypeNotFoundException {
        validateType(type);
        validateId(id, type);
        validateUser(id);
    }

    /**
     * Validates if id exists
     * @param id
     * @throws IdNotFoundException
     */
    private synchronized void validateUser(String id) throws  IdNotFoundException{
        if(_listObservations.get(id) == null) {
            throw new IdNotFoundException();
        }
    }

    /**
     * Validates if id respects the rules
     * @param id
     * @param type
     * @throws InvalidIdException
     */
    private void validateId(String id, String type) throws InvalidIdException {

        if(type.equals(TYPE_CAR)) {
            if(id.matches("([A-Z]{2}|[0-9]{2})([A-Z]{2}|[0-9]{2})([A-Z]{2}|[0-9]{2})")){
                int digits = id.replaceAll("\\D", "").length();
                if((6-digits) != 4 && digits != 4) {
                    throw new InvalidIdException();
                }
            }else{
                throw new InvalidIdException();
            }
        }
        else if(type.equals(TYPE_PERSON)) {
            if(!id.matches("[0-9]+")){
                throw new InvalidIdException();
            }
        }
    }

    /**
     * Validates if the type exists
     * @param type
     * @throws TypeNotFoundException
     */
    private void validateType(String type) throws TypeNotFoundException{
        if(!(type.equals(TYPE_CAR) || type.equals(TYPE_PERSON))){
            throw new TypeNotFoundException();
        }
    }

    /**
     * Gets current time
     * @return Instant
     */
    private synchronized Instant getTime(){
        return LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }

    /**
     * Prints all the observations in the replic(debug purposes)
     */
    public void printReplicObservations(){
        for(Map.Entry<Integer, List<Observation>> entry : _listReplicObservations.entrySet()) {
            List<Observation> value = entry.getValue();
            for(Observation ob : value) {
                System.out.println(ob.getType() + ob.getType().getId() + ob.getReplic() + ob.getSeqN());
            }
        }
    }
}


