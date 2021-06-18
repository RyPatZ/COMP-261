public class takeFuelNode extends  ActionNode implements RobotProgramNode{
    takeFuelNode(takeFuelNode ActionNode){
        super(ActionNode);
    }

    public takeFuelNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.takeFuel();
    }
    public String toString(){
        return "take fuel";
    }
}
