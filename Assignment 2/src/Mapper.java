import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale;

	// our data structures.
	private Graph graph;


	boolean distanceOrTime = false;

	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onClick(MouseEvent e) {
		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.nodes.values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			if (graph.highlightedNode != null) {//check if user clicked once
					aStarSearch(graph.highlightedNode, closest);//make the click(highlighted node)start and cloest (the  one clicked  later)the goal
					graph.highlightedNode = null;// inatialise every thing

			} else {
				graph.setHighlight(closest);
				getTextOutputArea().setText(closest.toString());
			}
		}
	}

	@Override
	protected void onSearch() {
		// Does nothing
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.ZOOM_IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.ZOOM_OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, roads, segments, polygons);
		origin = new Location(-250, 250); // close enough
		scale = 1;
	}

	@Override
	protected void onTime() {
		this.distanceOrTime = true;
	}

	@Override
	protected void onDistance() {
		this.distanceOrTime = false;
	}

	/**
	 * This method does the nasty logic of making sure we always zoom into/out
	 * of the centre of the screen. It assumes that scale has just been updated
	 * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	 * (zooming out). The passed boolean should correspond to this, ie. be true
	 * if the scale was just increased.
	 */
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}


	// code for COMP261 assignments
	/** Apply A* Search to find the shortest path then highlight them and print them
	 * **/
	public void aStarSearch(Node start, Node goal) {
		//initialize previous node and make no unvisited
		for(Node n : graph.nodes.values()) {
			n.isVisited = false;//make every nodes in the graph unvisited
			n.previousNode = null;//make every node's field previous node null
		}
		Fringe first = new Fringe(start, null, 0, start.location.distance(goal.location));//create a element fringe represent start
		PriorityQueue<Fringe> fringe = new PriorityQueue<>(Fringe::compareTo);//create a priority queue to store fringe component
		fringe.offer(first);// offer star fringe objecct into priority queue
		while (!fringe.isEmpty()) {//check if there is element in the queue
			Fringe temp = fringe.poll();  // expand the minimal with lowest f
			if (!temp.currentNode.isVisited) {//check if element are visited
				temp.currentNode.setVisited();//make it visted
				temp.currentNode.setPrev(temp.previousNode);//set pre
				if(temp.currentNode.nodeID  ==  goal.nodeID){break;}//if reach the goal break
				for(Node n : temp.currentNode.getNeigh()){// expand to neighbours
					if(!n.isVisited){//check if neighbour are visited
						double costFromStart = temp.costFromStart + findEdgeWeight(temp.currentNode,n);//calculate h(mode)
						double estimatedDistance = costFromStart + n.location.distance(goal.location);//calculate f(node)
						fringe.add(new Fringe(n, temp.currentNode, costFromStart, estimatedDistance));//add new ellment into queue
					}
				}
			}
		}
		List<Node> route = new ArrayList<>();//create a list to store fastest route's node
		//add each node into the list in order
		Node temp = goal;
		while(temp != start){
			route.add(temp);
			temp = temp.previousNode;
		}
		route.add(start);// System.out.println(route.toString());//to check

        for(Node n : route){
			graph.setHighlightNodes(n);//highlight the nodes in the route
		}

		List <Segment> route_seg = new ArrayList<>();////create a list to store fastest route's segment
		double length = 0.0; 	// route length
		// put segments into the list
		for (int i = route.size() - 1; i > 0; i--){
			Segment seg = findSegment(route.get(i),route.get(i-1));
			length = length + seg.length;
			route_seg.add(seg);
		}

		for(Segment s : route_seg){
			graph.setHighlightSegments(s);//hightlight segments in the roads
		}
		Set <String> roadNames = new HashSet<>();//create hash set to store road names to avoid duplicate
		for(Segment s: route_seg){
			roadNames.add(s.road.name);//add names in
		}
		Map<String, Double> RoadsLength = new HashMap<>();//store a map to store length of each road
		for(String s: roadNames){
			RoadsLength.put(s,0.0);
		}
		for(String name : roadNames){
			for(Segment s : route_seg){
				if(s.road.name.equals(name)){
					RoadsLength.put(name,RoadsLength.get(name) + s.length);//put length of each road in
				}
			}
		}
		String output = "";
		for(String name: roadNames){
			output = output + "Road name: " + name + ".     Length from last road: " + RoadsLength.get(name) + ". \n";
		}
		getTextOutputArea().setText(  "Sequence of road segments: \n" + output
				+ "\n" + "Route total length:  " + length);
	}
	/**find edgeweight of a node to get cost from start**/
	public double findEdgeWeight (Node start, Node goal){
		double weight = 0.0;
		for (Segment s : start.segments){
			if (goal.segments.contains(s)) {
				weight = s.length;
				break;
			}
		}
		return weight;
	}
/**find segments related to the node**/
	public Segment findSegment (Node start, Node goal){

		Segment temp = null;
		for (Segment s: start.segments){
			if (goal.segments.contains(s)){
				temp = s;
				break;
			}
		}
		return temp;
	}




}