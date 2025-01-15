package logic;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class TitleNode extends ListNode {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public TitleNode(String nodeName, String nodeDescription) {
        super(nodeName, nodeDescription);
    }

    @Override
    public void updateNodeState(TaskNode nodeChanged) {
        if (nodeChanged.getState() < 1 && this == doneTasks) {
            removeChild(nodeChanged);
            System.out.println(this.getNodeName());
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

    public static TitleNode highPriorityTasks = new TitleNode("High priority tasks" , "Tasks that are important");
    public static TitleNode mediumPriorityTasks = new TitleNode("Medium priority tasks" , "Tasks that are less important");
    public static TitleNode lowPriorityTasks = new TitleNode("Low priority tasks" , "Tasks that are not important");
    public static TitleNode unassignedTasks = new TitleNode("Unassigned tasks" , "Tasks that are unassigned");
    public static TitleNode doneTasks = new TitleNode("Done & won't do tasks" , "Tasks that are done or won't do tasks");
}
