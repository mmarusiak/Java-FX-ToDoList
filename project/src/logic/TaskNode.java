package logic;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class TaskNode extends ListNode{

    private ListNode parent;
    private float state = 0;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

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
        System.out.println(this.parent.getNodeName() + " - " + parent.getNodeName());
        if(this.parent == parent) return;
        if(this.parent != null) this.parent.removeChild(this);
        parent.addChild(this);
        this.parent = parent;
    }

    public void setParentQuietly(ListNode parent) {
        if(this.parent == parent) return;
        if(this.parent != null) this.parent.removeChild(this);
        parent.addChildQuietly(this);
        this.parent = parent;
    }

    public float getState() {
        return state;
    }

    public void setState(float state) {
        setState(state, false);
    }

    public void setState(float state, boolean shouldLookForChildren) {
        support.firePropertyChange("state", this.state, state);
        this.state = state;

        parent.updateNodeState(this);

        if(!shouldLookForChildren) return;
        for(var child : getChildren()) child.setState(state, true);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
