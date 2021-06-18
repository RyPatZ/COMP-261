
public class FuelLeftNode implements Sensor{

    @Override
    public int compute(Robot robot) {
        return robot.getFuel();
    }

    @Override
    public String toString(){
        return "fuelLeft";
    }

}