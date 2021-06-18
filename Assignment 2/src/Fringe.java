public class Fringe {
    public Node currentNode, previousNode;
    double costFromStart,estimatedTotal;

    public Fringe(Node n, Node prev, double costFromStart, double estimatedTotal){
        this.currentNode = n;
        this.previousNode = prev;
        this.costFromStart = costFromStart;
        this.estimatedTotal = estimatedTotal;
    }
    /**compare f(node) of each element to get the minimal **/
    public int compareTo(Fringe f) {
        if(this.estimatedTotal < f.estimatedTotal){return -1;}
        else if(this.estimatedTotal > f.estimatedTotal){return 1;}
        else{return 0;}
    }
}
