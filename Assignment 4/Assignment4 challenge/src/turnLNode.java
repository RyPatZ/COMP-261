public class turnLNode extends ActionNode implements  RobotProgramNode{
    turnLNode(turnLNode ActionNode){
        super(ActionNode);
    }

    public turnLNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.turnLeft();
    }
    public String toString(){
        return "turn Left";
    }
}
