public  class waitNode extends ActionNode{
    waitNode(waitNode ActionNode){
        super(ActionNode);
    }
ExprNode step;
    public waitNode() {
    }

    public waitNode (ExprNode step){
        this.step = step;
    }

    public void execute(Robot robot){
        robot.idleWait();
    }

    @Override
    public String toString() {
        return "wait(" + step + ")";
    }
}
