import java.util.ArrayList;
import java.util.List;

public class Trips {
    public  String tripsID;
    public List<String> trips_seq;
    public List<Connections> co;


public Trips(String id, List trips_seq){
    this.tripsID = id;
    this.trips_seq = trips_seq;
    co = new ArrayList<>();
}
public void addConnection(Connections c){//add connection
    this.co.add(c);
}
public String getId(){//for debug
    return tripsID;
}
}
