public class notNode implements ConditionNode {
    private ConditionNode con;//sensor

    public notNode(ConditionNode con){
        this.con = con;
    }

    @Override
    public boolean compute(Robot robot) {
        if(!con.compute(robot)){// if less than
            return true;
        }
        return false;
    }
    @Override
    public String toString(){
        return "not ("+con+")";
    }
}