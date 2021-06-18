public  class turnRNode extends ActionNode implements  RobotProgramNode{
    turnRNode(turnRNode ActionNode){
        super(ActionNode);
    }

    public turnRNode() {

    }

    @Override
    public void execute(Robot robot) {
        robot.turnRight();
    }
    public String toString(){
        return "turn Right";
    }
}
