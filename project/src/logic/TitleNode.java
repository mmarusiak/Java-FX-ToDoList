package logic;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class TitleNode extends ListNode {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String background;

    public TitleNode(String nodeName, String nodeDescription, String background) {
        super(nodeName, nodeDescription);
        this.background = background;
    }

    @Override
    public void updateNodeState(TaskNode nodeChanged) {
        if (nodeChanged.getState() < 1 && this == doneTasks) {
            removeChild(nodeChanged);
            nodeChanged.setParent(unassignedTasks);
            return;
        }else if(nodeChanged.getState() < 1) return;

        removeChild(nodeChanged);
        moveToDone(nodeChanged);
    }

    private void moveToDone(TaskNode doneTask){
        doneTask.setParent(doneTasks);
    }

    @Override
    public void addChild(TaskNode child) {
        super.addChild(child);
        support.firePropertyChange("child", this, child);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getBackground() {
        return background;
    }

    public static TitleNode highPriorityTasks = new TitleNode("High priority tasks" , "Tasks that are important", "#fa824c");
    public static TitleNode mediumPriorityTasks = new TitleNode("Medium priority tasks" , "Tasks that are less important", "#e9c46a");
    public static TitleNode lowPriorityTasks = new TitleNode("Low priority tasks" , "Tasks that are not important", "#3c91e6");
    public static TitleNode unassignedTasks = new TitleNode("Unassigned tasks" , "Tasks that are unassigned", "#638475");
    public static TitleNode doneTasks = new TitleNode("Done & won't do tasks" , "Tasks that are done or won't do tasks", "#8e5572");
}
