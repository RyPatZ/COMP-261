import java.util.ArrayList;
import java.util.List;

public class If implements RobotProgramNode{
    private List<ConditionNode> conditions;
    private List<RobotProgramNode> blocks;
    private boolean els = false;
    private List<RobotProgramNode> elsblocks;
    boolean elf = false;
    List<elif> elifs ;
    //List<ConditionNode> elfconditions ;//condition list

    public If(List<ConditionNode> c, List<RobotProgramNode> bl,boolean els, List<RobotProgramNode> ebl,boolean elf, List<elif> elifs){
        conditions = c;
        blocks = bl;
        this.els = els;
        elsblocks = ebl;
        this.elf = elf;
this.elifs = elifs;
    }

    @Override
    public void execute(Robot robot) {
        int size = conditions.size();//list of conditions
        for (int i = 0; i <= size; i++) {//every condition
            if (i == size) {//when finish all action
                break;//break
            } else if (conditions.get(i).compute(robot)) {//compute every conditions
                blocks.get(i).execute(robot);// make robot do every action in block
                break;
            }
        }
        if (elf) {
            for (elif e : elifs) {
            for (int i = 0; i <= size; i++) {//every condition
                if (i == size) {//when finish all action
                    break;//break
                } else if (!(conditions.get(i).compute(robot))) {//compute every conditions

                        List<RobotProgramNode> elfb = e.elfb;
                        List<ConditionNode> elfconditions = e.elfconditions;
                        for (int j = 0; j <= elfconditions.size(); j++) {//every condition
                            if (j == elfconditions.size()) {//when finish all action
                                break;//break
                            } else if (elfconditions.get(j).compute(robot)) {//compute every conditions
                                elfb.get(i).execute(robot);// make robot do every action in block
                                break;
                            }
                        }
                        for(ConditionNode c : elfconditions){
                            conditions.add(c);
                        }
                    }
                }
            }
        }
        if (els) {
            for (int i = 0; i <= size; i++) {//every condition
                if (i == size) {//when finish all action
                    break;//break
                } else if (!(conditions.get(i).compute(robot))) {//compute every conditions
                    elsblocks.get(i).execute(robot);// make robot do every action in block
                    break;
                }
            }
        }
    }

    @Override
    public String toString(){
        String s = "if(" + conditions.get(0).toString() + ")" + blocks.get(0).toString();
        if(elf){
            for (elif e : elifs){
                s += "\nelif(" + e.elfconditions.get(0).toString() + e.elfb.get(0).toString();
            }
        }
        if (els) {
            s+="\n else" + elsblocks.get(0).toString();

        }
//        else if(elf){
//            return ("if(" + conditions.get(0).toString() + ")" + blocks.get(0).toString()+"\nelif(" + elfconditions.get(0).toString() + ")";
//        }
        return s;
    }
}