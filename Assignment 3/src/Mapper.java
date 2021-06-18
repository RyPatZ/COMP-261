import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

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
     static HashSet<Node> AP = new HashSet<>();
     static Set <Node> Forest = new HashSet<>();
     static Set<Segment> tree= new HashSet <> ();
	// our data structures.
	private static Graph graph;


	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale,AP, tree);
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
			graph.setHighlight(closest);
			getTextOutputArea().setText(closest.toString());
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
	/**iterate AP */

	public static void iterativeAP(){
		for(int key : graph.nodes.keySet()) {
			Node root = graph.nodes.get(key);//select a random point
			if(root.depth == Integer.MAX_VALUE) {
				root.depth = 0;
				int numOfSubTrees = 0;
				for (Node neigh : getNeigh(root)) {
					if (neigh.depth == Integer.MAX_VALUE) {
						iterArtPts(neigh, 1, root);
						numOfSubTrees++;
					}
				}
					if (numOfSubTrees > 1) {
						AP.add(root);
					}
				}
			}
		System.out.println(AP.size());
	}


	public static void iterArtPts(Node firstNode, int depth, Node root){
		Stack<Articulation> stack = new Stack<Articulation>();
		stack.add(new Articulation(firstNode, depth, root));
		while (stack.size()!=0){
			Node nodeArt = stack.peek().firstNode;
			int depthArt = stack.peek().Depth;
			Node parentArt = stack.peek().root;
			if (nodeArt.depth == Integer.MAX_VALUE) {
				nodeArt.depth=depthArt;
				nodeArt.reachBack=depthArt;
				for (Node n: getNeigh(nodeArt)){
					if(n.nodeID != parentArt.nodeID){
						nodeArt.Children.add(n);
					}
				}
			}
			else if (nodeArt.Children.size() != 0){
				Node child = nodeArt.Children.remove(0);
				if(child.depth < Integer.MAX_VALUE){
					nodeArt.reachBack=Math.min(child.depth,nodeArt.reachBack);
				}
				else{
					stack.push(new Articulation(child, depthArt+1, nodeArt));
				}
			}
			else{
				if(nodeArt.nodeID != firstNode.nodeID ){
					parentArt.reachBack = Math.min(nodeArt.reachBack, parentArt.reachBack);
					if(nodeArt.reachBack>= parentArt.depth){
						AP.add(parentArt);
					}
				}
				stack.pop();
			}
		}
	}
    /**finding minium spanning tree*/
	public static void miniumSpanningTree(){ tree = KruskalsAlgorithm();}

	public static Set <Segment>  KruskalsAlgorithm(){
		for(int key : graph.nodes.keySet()) {
			Node root = graph.nodes.get(key);
			makeSet(root);
		}
		PriorityQueue <Segment> fringe = new PriorityQueue<>(Segment::compareTo);
		for (Segment s : graph.segments){fringe.add(s);}
		Set <Segment> tree = new HashSet<>();

		while (Forest.size()>1 && fringe.size()!=0){
			Segment f = fringe.poll();
			if (findRoot(f.start)!= findRoot(f.end)){
				Union(f.start,f.end);
				tree.add(f);
			}
		}
		System.out.println(tree.size());//for debugging
		return tree;
	}
// method makeset() findroot() and Union() are for disjoint set struture
	public static void makeSet(Node node){
		node.Parent = node;
		node.MSTDepth=0;
		Forest.add(node);
	}

	public static Node findRoot(Node node){
		if(node.Parent==node){
			return node;
		}
		else{
			Node root = findRoot(node.Parent);
			return root;
		}
	}

	public static void Union (Node x, Node y){
		Node xRoot = findRoot(x);
		Node yRoot = findRoot(y);
		if (xRoot == yRoot ){
			return ;
		}
		else {
			if(xRoot.MSTDepth<yRoot.MSTDepth){
				xRoot.Parent = yRoot;
				Forest.remove(xRoot);
			}
			else{
				yRoot.Parent = xRoot;
				Forest.remove(yRoot);
				if (xRoot.MSTDepth == yRoot.MSTDepth){
					xRoot.MSTDepth++;
				}
			}
		}
	}

	//get Neighbours of input node
	public static Set<Node> getNeigh(Node node){
         Set<Node> neigh = new HashSet<>() ;;
		for (Segment s : node.segments){

			neigh.add(s.end);

			if(s.start.nodeID != node.nodeID) {
			neigh.add(s.start);
			}
		}
		return neigh;
	}



	public static void main(String[] args) {
		new Mapper();
	}
}

// code for COMP261 assignments