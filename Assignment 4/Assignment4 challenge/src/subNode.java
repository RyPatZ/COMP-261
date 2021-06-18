
public class subNode implements OP  {
    ExprNode left;
    ExprNode right;
    public subNode (ExprNode left , ExprNode right){
        this.left = left;
        this.right = right;
    }


    @Override
    public int compute(Robot robot) {
        int i = left.compute(robot) - right.compute(robot);
        return i ;
    }

    public String toString (){return "sub(" + left + "," + right +")"; }
}