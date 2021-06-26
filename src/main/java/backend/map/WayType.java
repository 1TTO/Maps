package backend.map;

import java.util.ArrayList;
import java.util.HashMap;
import frontend.map.Map;

public class WayType extends ArrayList<Way> {
    private final HashMap<Long, Node> wayTypeNodes;
    private final double strokeWidth;
    private final Map map;

    public WayType(Map map, double strokeWidth){
        this.map = map;
        wayTypeNodes = new HashMap<>();
        this.strokeWidth = strokeWidth;
    }

    public double getStrokeWidth(){
        return strokeWidth;
    }

    public void addNodeList(ArrayList<Node> nodes){
        for (Node node : nodes){
            wayTypeNodes.put(node.getID(), node);
        }
    }

    public void scale(double scaleFactor){
        for (Way way : this){
            way.scale(scaleFactor);
        }
    }

    public void draw(){
        for (Way way : this){
            way.draw(map);
        }
    }

    public void connectNodes(){
        for (Way way : this){
            way.connectNodes(strokeWidth);
        }
    }

    public HashMap<Long, Node> getWayTypeNodes(){
        return wayTypeNodes;
    }
}
