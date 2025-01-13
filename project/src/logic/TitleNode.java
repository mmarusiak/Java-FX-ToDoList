package logic;

public class TitleNode extends ListNode {

    public TitleNode(String nodeName, String nodeDescription) {
        super(nodeName, nodeDescription);
    }

    @Override
    public void updateNodeState(TaskNode nodeChanged) {
        if (nodeChanged.getState() < 1) return; // just change some information like 30% tasks done

        removeChild(nodeChanged);
        moveToDone(nodeChanged);
    }

    private void moveToDone(TaskNode doneTask){
        doneTask.addChild(doneTask);
    }

    public static TitleNode highPriorityTasks = new TitleNode("High priority tasks" , "Tasks that are important");
    public static TitleNode mediumPriorityTasks = new TitleNode("Medium priority tasks" , "Tasks that are less important");
    public static TitleNode lowPriorityTasks = new TitleNode("Low priority tasks" , "Tasks that are not important");
    public static TitleNode unassignedTasks = new TitleNode("Unassigned tasks" , "Tasks that are unassigned");
    public static TitleNode doneTasks = new TitleNode("Done & won't do tasks" , "Tasks that are done or won't do tasks");
}
