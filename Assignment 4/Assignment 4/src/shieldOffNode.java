
public class shieldOffNode implements RobotProgramNode {

    @Override
    public void execute(Robot robot) {
        robot.setShield(false);//take off the shield
    }

    @Override
    public String toString(){
        return "shieldOff;";
    }

}