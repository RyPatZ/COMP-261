public class andNode implements ConditionNode {
    private ConditionNode left;//sensor
    private ConditionNode right;//expression

    public andNode(ConditionNode l, ConditionNode r){
        left = l;
        right = r;
    }

    @Override
    public boolean compute(Robot robot) {
        if(left.compute(robot) &&right.compute(robot)){// if less than
            return true;
        }
        return false;
    }
    @Override
    public String toString(){
        return "and ("+left+", "+right+")";
    }
}