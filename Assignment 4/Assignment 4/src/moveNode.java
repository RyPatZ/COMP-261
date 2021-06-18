public class moveNode extends ActionNode implements  RobotProgramNode{
    ExprNode exp;

    public moveNode(ExprNode e){
        exp = e;
    }
    public moveNode(ActionNode ActionNode) {
        super(ActionNode);
    }

    public moveNode() {
        super();
    }


    @Override
    public void execute(Robot robot) {
        if(exp == null){ robot.move(); }
        else{
            int stop = exp.compute(robot);//compute the total oil left
            for(int i = 0; i < stop; i++){
                robot.move();//how many steps can robot go
            }
        }
    }

    public String toString() {
        if(exp == null){ return "move"; }
        return "move" + exp.toString();
    }
}
