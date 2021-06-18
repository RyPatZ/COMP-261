
public class addNode implements OP  {
    ExprNode left;
    ExprNode right;
    public addNode (ExprNode left , ExprNode right){
        this.left = left;
        this.right = right;
    }


    @Override
    public int compute(Robot robot) {
        int i = left.compute(robot) + right.compute(robot);
        return i ;
    }

    public String toString (){return "add(" + left + "," + right +")"; }
}
