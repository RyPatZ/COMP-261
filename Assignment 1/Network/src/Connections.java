

import java.awt.*;

public class Connections {
    public String tripId;
    public Stops from, to;
    public Trips trips;
    public Location[] locations;


    public Connections(String tripId, String stopID1, String stopID2) {
        this.from = extendsGUI.stops.get(stopID1);
        this.to = extendsGUI.stops.get(stopID2);
    }


    public void draw(Graphics g, Location origin, double scale) {//draw method to draw connection
        Point p1 = from.location.asPoint(origin, scale);
        Point p2 = to.location.asPoint(origin, scale);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

}
