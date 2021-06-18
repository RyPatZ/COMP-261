import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class extendsGUI extends GUI {
    public static Map<String, Stops> stops = new HashMap<>(); //  a Collection of Stops (graph)
    public static List<Connections> connections = new ArrayList<>();          // a Collection of Connections
    public static Map <String,Trips > trips = new HashMap<>();  // a Collection of trips
    private Location origin = new Location(0,0);//origin location
    private Location bottom_right_origin = new Location(Double.MAX_VALUE,Double.MAX_VALUE);// bottom_right location
    private double scale = 50;
    private Color highlight = Color.red;
    private Color initial_color = Color.black;
    private double ORIGIN_MOVEMENT_AMOUNT = 100;
    private double ZOOM_FACTOR = 1.2;           // defines the scale when zooming and panning
    private double MAX_DISTANCE = 0.5;    // defines how far away from a node you can click to choose.
    Stops target = null;
    Stops target1 = null;
    private Trie root; // root of Trie
    public Set <Trips> highlightedTrips = new HashSet<>();
    public Set <Connections> highlightedconnections = new HashSet<>();
    @Override
    protected void redraw(Graphics g) {
        for (Stops n : stops.values()){    //draw stops
            g.setColor(Color.blue);
            n.draw(g,origin,scale);
        }

        for(Connections s : connections){        //draw connection
            g.setColor(initial_color);
            s.draw(g, origin, scale);
        }
        if (target != null) {//draw highlight point when mouse clicked
            g.setColor(highlight);
            target.draw(g, origin, scale);
        }
        if (target1 != null) {//draw highlight point when search
            g.setColor(highlight);
            target1.draw(g, origin, scale);
        }
        if(highlightedTrips != null){//draw highlight point when search
            g.setColor(Color.red);
            for(Trips t : highlightedTrips){
                for(String s : t.trips_seq) {
                      stops.get(s).draw(g,origin,scale);
                    }
            }
        }
        if (highlightedconnections!=null){
            for(Connections s : highlightedconnections){        //draw highlight connection when search
                g.setColor(highlight);
                s.draw(g, origin, scale);
            }
        }
    }

    @Override
    protected void onClick(MouseEvent e) {
        Point mouse_point = e.getPoint();   //get mouse's point
        Location mouse_location = Location.newFromPoint(mouse_point, this.origin,this.scale); //convert to location
        double distance = Double.MAX_VALUE;//distance between target location and mouse location

        // find the closet node first
        for(Stops n : stops.values()){
            if(n.location.distance(mouse_location) < distance){
                distance = n.location.distance(mouse_location);   // get the closest distance
                target = n;
                Set <String> set = new HashSet<>();//create a set to store trips ID
                for (Trips t  : trips.values()){
                    for(String l : t.trips_seq){
                        if(l.equals(target.getId())){
                            set.add(t.tripsID);
                        }
                        getTextOutputArea().setText("Stop name :" + target.getStopName());
                        getTextOutputArea().append("\ntrips id going through this stop:");
                        for (String a : set){
                            getTextOutputArea().append("\n" + a);
                        }

                    }
                }

            }
        }
    }

    @Override
    protected void onSearch() {
       // if (root == null){return;}
        String input = getSearchBox().getText();
        //List<Trips> trie_results = getALL(input.toCharArray());  //get results from the trie
       // boolean exact_matched = false;

        for(Trips t:trips.values()){
            //see if the stop exactly match the user input
            //if so , find the stop store it to field
            for(String s : t.trips_seq) {
                for (String keyset : stops.keySet()) {
                    Stops searchBox = stops.get(keyset);
                    if (searchBox.getStopName().equals(input)) {
                        //exact_matched = true;
                        this.target1 = searchBox;
                    }
                }
            }
            //find trips going through the stop searched
            for (Trips r : trips.values()) {
                for (String l : r.trips_seq) {
                    if (l.equals(target1.getId())) {
                        highlightedTrips.add(r);
                    }
                }
            }
            //find all the connections and store them into field
            for (Trips h : highlightedTrips){
                for (int i = 1; i< h.trips_seq.size();i++){
                    Connections co = new Connections(h.tripsID,h.trips_seq.get(i-1),h.trips_seq.get(i));
                    highlightedconnections.add(co);
                }

            }
        }
    }

    @Override
    protected void onMove(Move m) {
        if (m == GUI.Move.NORTH){
            origin = origin.moveBy(0, ORIGIN_MOVEMENT_AMOUNT / scale); // divide by scale to keep the move amount unchanged at every zoom condition
        }
        else if(m == Move.SOUTH){
            origin = origin.moveBy(0, -ORIGIN_MOVEMENT_AMOUNT / scale);
        }
        else if(m == Move.EAST){
            origin = origin.moveBy(ORIGIN_MOVEMENT_AMOUNT / scale,0);
        }
        else if(m == Move.WEST){
            origin = origin.moveBy(-ORIGIN_MOVEMENT_AMOUNT / scale,0);
        }
        else if(m == Move.ZOOM_IN){
            scale = scale*ZOOM_FACTOR;     //set scale
            double width = bottom_right_origin.x - origin.x; //getALL new width and height
            double height = origin.y - bottom_right_origin.y;
            double dx = (width*ZOOM_FACTOR - width)/2;       // getALL x gap and y gap
            double dy = (height * ZOOM_FACTOR - height)/2;
            origin.moveBy(-dx,-dy);        // move origin(top left)
            bottom_right_origin.moveBy(dx,dy); // move bottom right origin
        }

        else if(m == Move.ZOOM_OUT){
            scale = scale / ZOOM_FACTOR;
            double width = bottom_right_origin.x - origin.x;
            double height = origin.y - bottom_right_origin.y;
            double dx = (width - width/ZOOM_FACTOR)/2;
            double dy = (height - height/ZOOM_FACTOR)/2;
            origin.moveBy(dx,dy);
            bottom_right_origin.moveBy(-dx,-dy);
        }
    }

    @Override
    protected void onLoad(File stopFile, File tripFile) {
        readStops(stopFile);//load file
        readTrips(tripFile);
    }

    //methods that I created
//use bufferedReader to read file and save stops into field
    public void readStops(File s){
        try {
            FileReader fr = new FileReader(s);
            BufferedReader reader = new BufferedReader(fr);
            String the_line;
            String[] node_info;
            reader.readLine();

            while (true) {
                the_line = reader.readLine();
                if (the_line == null) {
                    break;
                } else {
                    node_info = the_line.split("\t");
                    String id = node_info[0];
                    String stopName = node_info[1];
                    double x = toDouble(node_info[2]);
                    double y = toDouble(node_info[3]);
                    Stops stop = new Stops (id,stopName, x, y);
                    this.stops.put(id,stop);
                }
            }
            reader.close();

        } catch (Exception e) {
            throw new RuntimeException("file reading failed");
        }

    }
    //methods that I created
//use bufferedReader to read file and save trips into field
// and use trip sequence to create connection object to store them in the field
    public void readTrips(File t) throws RuntimeException {
        try {
            FileReader fr = new FileReader(t);
            BufferedReader reader = new BufferedReader(fr);
            String the_line;
            String[] trip_info;
            reader.readLine();

            while (true) {
                the_line = reader.readLine();
                if (the_line == null) {
                    break;
                } else {
                    trip_info = the_line.split("\t");
                    String id = trip_info[0];
                    List <String> stop_sequence = new ArrayList<>();
                    for (int i=1;i<=trip_info.length-1;i++){
                        if (trip_info[i]== null){
                            break;
                        }
                            stop_sequence.add(trip_info[i]);
                    }
                    for (int i = 1; i< stop_sequence.size();i++){
                        Connections co = new Connections(id,stop_sequence.get(i-1),stop_sequence.get(i));
                        connections.add(co);
                    }
                    Trips trip = new Trips(id,stop_sequence);
                    this.trips.put(id,trip);
                }
            }
            reader.close();

        } catch (Exception e) {
            throw new RuntimeException("file reading failed");
        }
    }



    /*
    Helper method to parse raw data
     */
    public double toDouble (String str){
        return Double.parseDouble(str);
    }
    public int toInt(String str){
        return Integer.parseInt(str);
    }



    public static void main(String[] args){
        new extendsGUI();
    }
}

