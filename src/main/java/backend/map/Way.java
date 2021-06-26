package backend.map;

import frontend.map.Map;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Way {
    private final ArrayList<Node> nodes;
    private final ArrayList<Line> connections;

    public Way(ArrayList<Node> nodes){
        this.connections = new ArrayList<>();
        this.nodes = nodes;
    }

    public ArrayList<Line> getConnections(){return connections;}

    public void connectNodes(double strokeWidth){
        for (int i = 0; i < nodes.size() - 1; i++){
            Line newConnection = new Line(nodes.get(i).getMapX(), nodes.get(i).getMapY(), nodes.get(i + 1).getMapX(), nodes.get(i + 1).getMapY());

            newConnection.setStrokeWidth(strokeWidth);
            connections.add(newConnection);
        }
    }

    public void scale(double scaleFactor){
        for (Line connection : connections){
            connection.setStrokeWidth(connection.getStrokeWidth() * scaleFactor);
        }
    }

    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public void draw(Map map){
        for (Line connection : connections){
            try{
                map.getChildren().add(connection);
            }catch (IllegalArgumentException ignored){}

        }
    }
}
