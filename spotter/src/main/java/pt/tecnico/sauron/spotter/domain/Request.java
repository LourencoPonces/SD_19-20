package pt.tecnico.sauron.spotter.domain;

import pt.tecnico.sauron.silo.grpc.*;
import java.time.*;

import java.util.*;

public class Request {
    private List<Integer> _vectorClock = new ArrayList<>();
    private List<SpotterObservation> _spotterObservations = new ArrayList<>();
    private String _id;
    private String _type;
    private String _typeRequest;
    private int _seqN = 0;

    public Request(List<Integer> vectorClock, List<SpotterObservation> obser, String id, String type, String typeRequest){
        _vectorClock = vectorClock;
        _spotterObservations = obser;
        _id = id;
        _type = type;
        _typeRequest = typeRequest;
    }

    /**
     * Gets the vector clock
     * @return List<Integer>
     */
    public List<Integer> getVectorClock() {
        return _vectorClock;
    }

    /**
     * Sets the vector clock
     * @param _vectorClock
     */
    public void setVectorClock(List<Integer> _vectorClock) {
        this._vectorClock = _vectorClock;
    }

    /**
     * Gets the spotter observations
     * @return List<SpotterObservation>
     */
    public List<SpotterObservation> getSpotterObservations() {
        return this._spotterObservations;
    }

    /**
     * Sets the spotter observations
     * @param _spotterObservations
     */
    public void setSpotterObservations(List<SpotterObservation> _spotterObservations) {
        this._spotterObservations = _spotterObservations;
    }

    /**
     * Gets the id
     * @return String
     */
    public String getId() {
        return this._id;
    }

    /**
     * Sets the id
     * @param id
     */
    public void setId(String id) {
        this._id = id;
    }

    /**
     * Gets the type of the id
     * @return String
     */
    public String getType() {
        return this._type;
    }

    /**
     * Sets the type of the id
     * @param type
     */
    public void setType(String type) {
        this._type = type;
    }

    /**
     * Gets the type of the request
     * @return String
     */
    public String getTypeRequest() {
        return this._typeRequest;
    }

    /**
     * Sets the type of the request
     * @param type
     */
    public void getTypeRequest(String type) {
        this._typeRequest = type;
    }

    /**
     * Gets the sequence number
     * @return int
     */
    public int getSeqN() {
        return this._seqN;
    }

    /**
     * Sets the sequence number
     * @param seqN
     */
    public void setSeqN(int seqN) {
        this._seqN = seqN;
    }

    /**
     * Compares vector clock, returns false if there is no position in the vector of the request that is higher than of
     * the vector of the current request
     * @param request
     * @return boolean
     */
    public boolean compareVectorClock(Request request) {
       for( int i = 0; i < _vectorClock.size(); i++ ) {
           if(this._vectorClock.get(i) > request.getVectorClock().get(i)){
               return false;
           }
       }
       return true;
    }

    /**
     * Checks if two requests are equal
     * @param request
     * @return boolean
     */
    public boolean equals(Request request) {
        return request.getType().equals(this._type) && request.getTypeRequest().equals(this._typeRequest) && request.getId().equals(this._id);
    }

    /**
     * Request displayed in a string format
     * @return
     */
    public String toString() {
        return getSeqN() + " " + getTypeRequest() + " " + getType() + " " + getId() + "\n";
    }
}