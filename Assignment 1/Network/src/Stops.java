import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Stops {

    public  String stopID;
    public String stopName;
    public  double lat,longi;
    public  List<Connections> outgoing_connection,incoming_connection;
    Location location ;

    public Stops (String id,String stopName ,double latitude, double longitude){
        this.stopID = id;
        this.stopName = stopName;
        this.lat =latitude;
        this.longi =longitude;
        this.location = Location.newFromLatLon(latitude,longitude);
        outgoing_connection = new ArrayList<>();
        incoming_connection = new ArrayList<>();
    }

    public void draw(Graphics g, Location origin, double scale){//draw method for draw method
        Point point = this.location.asPoint(origin,scale); // getALL point
        int size = 4;
        g.fillRect(point.x-size/2, point.y-size/2, size,size);
    }

    public String getId(){//for debug
        return stopID;
    }

    public String getStopName(){
        return stopName;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }

    public Location getLocation(){
        return location;
    }

    public List getTrips(){//for debug
        List <Trips> list = new ArrayList<>();
        for (Trips t  : extendsGUI.trips.values()) {
            for (String l : t.trips_seq) {
                if (l.equals(this.stopID)) {
                    list.add(t);
                }
            }
        }
        return list;
    }
}
