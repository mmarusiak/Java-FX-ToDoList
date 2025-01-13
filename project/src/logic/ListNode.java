package logic;

import java.util.ArrayList;
import java.util.List;

public abstract class ListNode {

    private String nodeName, nodeDescription;
    private ArrayList<TaskNode> children = new ArrayList<>();

    public ListNode(String nodeName, String nodeDescription) {
        this.nodeName = nodeName;
        this.nodeDescription = nodeDescription;
    }

    public abstract void updateNodeState(TaskNode nodeChanged);

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public List<TaskNode> getChildren() {
        return children;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    public void setChildren(ArrayList<TaskNode> children) {
        this.children = children;
    }

    public void addChild(TaskNode child) {
        children.add(child);
    }

    public void removeChild(TaskNode child) {
        children.remove(child);
    }
}
