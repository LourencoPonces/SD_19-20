package pt.tecnico.sauron.spotter.domain;


import java.util.ArrayList;
import java.util.List;

public class Cache {
    private List<Request> _requestList;
    private int _seqN;
    private int _size;
    public Cache(int size){
        _requestList = new ArrayList<>(size);
        _seqN = 0;
        _size = size;
    }

    /**
     * Searches for the request in cache and updates it if needed
     * @param r
     * @return Request
     */
    public Request searchRequest(Request r) {
        int i = _seqN;
        int minorSeqN = _seqN;
        int index = 0;
        for(Request req : _requestList) {
            if(req.equals(r)){
                if(req.compareVectorClock(r)) { //updates the position
                    _requestList.set(index, r);
                    r.setSeqN(_seqN);
                    _seqN++;
                    return r;
                }
                else { //returns what was previously in cache
                    return req;
                }
            }
            if( minorSeqN > req.getSeqN()) { //gets the lowest seq number
                i = index;                 // saves the index to substitute in that position
                minorSeqN = req.getSeqN();
            }
            index++;
        }

        //Updating the request list in the i position and returning the answer
        if(_requestList.size() < _size) {
            _requestList.add(r);
        }
        else {
            _requestList.set(i, r);
        }
        r.setSeqN(_seqN);
        _seqN++;
         return r;
    }

    /**
     * Prints whats in the cache (debug purposes)
     */
    public void printCache(){
        for(Request r : _requestList)
            System.out.println(r.toString());
    }
}
