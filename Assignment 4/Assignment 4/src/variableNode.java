public class variableNode implements RobotProgramNode {
    ExprNode assignment ;
    String variable;
    public variableNode (String variable ,ExprNode assignment) {
        this.variable = variable;
        this.assignment = assignment;
    }
    @Override
    public void execute(Robot robot) {

    }

    public String toString(){
        return variable+ "="+ assignment;
    }
}
