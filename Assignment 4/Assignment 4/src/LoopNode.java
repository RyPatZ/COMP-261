public class LoopNode implements RobotProgramNode {
    RobotProgramNode block;

    public LoopNode(RobotProgramNode Block) {
        this.block = Block;
    }

    @Override
    public void execute(Robot robot) {
        // TODO Auto-generated method stub
        while (true) {
            block.execute(robot);
        }
    }

    @Override
    public String toString() {
        return "loop {\n" + block + "}";
    }

}
