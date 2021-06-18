
public class barrelFBNode implements Sensor {

    private ExprNode exp =null;
    barrelFBNode(){}

    public barrelFBNode(ExprNode e){
        exp = e;
    }

    @Override
    public int compute(Robot robot) {
        if(exp == null){return robot.getClosestBarrelFB();}
        return robot.getBarrelFB(exp.compute(robot));
    }

    @Override
    public String toString(){
        if(exp == null){return "barrelFB";}
        return "barrelFB"+ exp.toString();
    }

}