import javax.swing.*;

public class STMTNode implements RobotProgramNode  {
    RobotProgramNode executingStatement;
//    public STMTNode(ConditionNode conditionNode){
//        this.executingStatement = (RobotProgramNode) conditionNode;
//    }
    public STMTNode(ActionNode actionNode){
        this.executingStatement= actionNode;
    }

    public STMTNode(LoopNode loopNode){
        this.executingStatement=loopNode;

    }
//    public STMTNode(IfNode ifNode){
//        this.executingStatement = ifNode;
//
//    }
//    public STMTNode(whileNode whileNode){
//        this.executingStatement=whileNode;
//    }
    @Override
    public void execute(Robot robot) {
        executingStatement.execute(robot);
    }

    public String toString(){
        return (executingStatement.toString());
    }
}
