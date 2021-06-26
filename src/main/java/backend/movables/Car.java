package backend.movables;

import backend.map.Node;
import backend.map.WayType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import frontend.map.Map;

public class Car extends Moveable{
    Circle shape = new Circle();

    public Car(Node currentNode, Node destinationNode, WayType wayType, Map map, int maxSpeed, double FPS) {
        super(currentNode, destinationNode, wayType, map, maxSpeed, FPS);

        setShape(shape);
        shape.setRadius(10);
        shape.setFill(Color.RED);
    }

    @Override
    public void scale(double scaleFactor) {
        shape.setRadius(shape.getRadius() / scaleFactor);
    }
}
