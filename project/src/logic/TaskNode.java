package logic;

public class TaskNode extends ListNode{

    private ListNode parent;
    private float state = 0;

    public TaskNode(String name, String description, ListNode parent){
        super(name, description);
        this.parent = parent;
    }

    @Override
    public void updateNodeState(TaskNode nodeChanged) {

        if(nodeChanged.getState() == 1) nodeDone(nodeChanged);

        setState(calculateState());
    }

    public float calculateState(){

        float sum = 0;
        for (var child : getChildren()) sum += child.getState();

        return sum / getChildren().size();
    }

    private void nodeDone(TaskNode nodeDone) {

    }

    public ListNode getParent() {
        return parent;
    }

    public void setParent(ListNode parent) {
        if(this.parent != null) this.parent.removeChild(this);
        parent.addChild(this);
        this.parent = parent;
    }

    public float getState() {
        return state;
    }

    public void setState(float state) {
        this.state = state;

        parent.updateNodeState(this);
    }
}
