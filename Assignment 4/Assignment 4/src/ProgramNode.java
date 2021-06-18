import java.util.*;

public class ProgramNode implements  RobotProgramNode{//program node start
    List<RobotProgramNode> statementNode;
    public ProgramNode(ArrayList<RobotProgramNode> children){
        this.statementNode = children;
    }



    @Override
    public void execute(Robot robot) {
        for (int i=0; i < this.statementNode.size();i++){//iterate the nodes in the array list
            this.statementNode.get(i).execute(robot);
        }
    }
    public void  addNode(STMTNode newStatement){
        this.statementNode.add(newStatement);
    }
    public String toString() {// print  the children arraylist
        String str = "";
        for (int i = 0; i < this.statementNode.size(); i++){
            str += this.statementNode.get(i).toString() + "\n";
        }
        return str;
    }
}
