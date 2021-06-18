public class barrelLRNode implements Sensor {

    private ExprNode exp = null;
    public barrelLRNode(){}

    public barrelLRNode(ExprNode e){
        exp = e;
    }

    @Override
    public int compute(Robot robot) {
        if(exp == null){return robot.getClosestBarrelLR();}
        return robot.getBarrelLR(exp.compute(robot));
    }

    @Override
    public String toString(){
        if(exp == null){return "barrelLR" ;}
        return "barrelLR" + exp.toString();
    }

}