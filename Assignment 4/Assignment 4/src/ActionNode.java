import javax.swing.*;

public class ActionNode implements RobotProgramNode {
    ActionNode ActionNode;

    public ActionNode(ActionNode ActionNode){
        this.ActionNode=ActionNode;
    }
    public ActionNode() {

    }
    public String toString(){
        return (this.ActionNode.toString());
    }

    @Override
    public void execute(Robot robot) {
    }
}


