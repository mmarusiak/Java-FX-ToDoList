package logic;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class NodeBox extends VBox {

    private boolean isTitleNode = false;

    public NodeBox(){

    }

    public NodeBox(boolean isTitleNode) {
        this.isTitleNode = isTitleNode;
    }

    public NodeBox(double v, boolean isTitleNode) {
        super(v);
        this.isTitleNode = isTitleNode;
    }

    public NodeBox(boolean isTitleNode, Node... nodes) {
        super(nodes);
        this.isTitleNode = isTitleNode;
    }

    public NodeBox(double v, boolean isTitleNode, Node... nodes) {
        super(v, nodes);
        this.isTitleNode = isTitleNode;
    }

    public boolean isTitleNode() {
        return isTitleNode;
    }

    public void setTitleNode(boolean titleNode) {
        isTitleNode = titleNode;
    }
}
