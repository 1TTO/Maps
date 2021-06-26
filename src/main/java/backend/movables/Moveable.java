package backend.movables;

import backend.map.Node;
import backend.map.WayType;
import javafx.application.Platform;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import frontend.map.Map;

abstract public class Moveable extends Thread{
    private final Node startNode, destinationNode;
    private final WayType wayType;
    private final int maxSpeedKMH;
    private final Map map;
    private final double FPS;
    private Shape shape;

    Moveable(Node startNode, Node destinationNode, WayType wayType, Map map, int maxSpeedKMH, double FPS){
        this.FPS = FPS;
        this.maxSpeedKMH = maxSpeedKMH;
        this.map = map;
        this.wayType = wayType;
        this.startNode = startNode;
        this.destinationNode = destinationNode;
    }

    void setShape(Shape shape){
        this.shape = shape;
    }

    public abstract void scale(double scaleFactor);

    public void run(){
        ArrayList<Node> route = getRoute(startNode, destinationNode, wayType);

        if (route == null) {
            System.err.println(this + ": Not routable!");
            //return;
        } else route(route);
    }

    void route (ArrayList<Node> routeNodes){
        TimeUnit time = TimeUnit.MICROSECONDS;
        double nodeDistanceKM, lastConnectionCoveredDistance = 0;
        double distanceKM;
        int i = 0;

        Platform.runLater(()->{
            map.getChildren().add(shape);
        });

        while (i + 1 < routeNodes.size() - 1){
            distanceKM = maxSpeedKMH * map.getSimulation().getSimulationSpeed() * ((1 / FPS) / (60 * 60));
            // 1 lat/lon = 111km
            nodeDistanceKM = 111 * Math.sqrt(Math.pow(routeNodes.get(i).getLat() - routeNodes.get(i + 1).getLat(), 2) + Math.pow(routeNodes.get(i).getLon() - routeNodes.get(i + 1).getLon(), 2));
            lastConnectionCoveredDistance += distanceKM;

            while (lastConnectionCoveredDistance > nodeDistanceKM){
                if (i + 1 < routeNodes.size() - 1) i++;
                lastConnectionCoveredDistance -= nodeDistanceKM;
                nodeDistanceKM = 111 * Math.sqrt(Math.pow(routeNodes.get(i).getLat() - routeNodes.get(i + 1).getLat(), 2) + Math.pow(routeNodes.get(i).getLon() - routeNodes.get(i + 1).getLon(), 2));
            }

            double[] newPos = getPositionBetweenNodes(routeNodes.get(i), routeNodes.get(i + 1), lastConnectionCoveredDistance / 111);
            Platform.runLater(()->{
                shape.setTranslateX(map.getMapX(newPos[0]));
                shape.setTranslateY(map.getMapY(newPos[1]));
            });

            try {
                time.sleep((long)((1 / FPS) * 1000 * 1000));
            } catch (InterruptedException e) {
                System.err.println(this.toString() + ": Could not move forward!\nArrivaltime may vary!");
            }
        }
    }

    ArrayList<Node> getRoute(Node startNode, Node endNode, WayType wayType){
        Node currentNode;
        Node startNodeCopy = startNode.clone();
        startNodeCopy.setParent(null);

        ArrayList<Node> notVisitedList = new ArrayList<>();
        ArrayList<Node> visitedList = new ArrayList<>();

        notVisitedList.add(startNodeCopy);

        while (notVisitedList.size() > 0){
            currentNode = notVisitedList.get(0);

            for (Node notVisitedNode : notVisitedList){
                if (notVisitedNode.getF() < currentNode.getF()){
                    currentNode = notVisitedNode;
                }
            }

            notVisitedList.remove(currentNode);
            visitedList.add(currentNode);

            if (currentNode.equals(endNode)){
                ArrayList<Node> path = new ArrayList<>();
                Node current = currentNode;

                while (current != null){
                    path.add(current);
                    current = current.getParent();
                }

                Collections.reverse(path);
                return path;
            }

            for (Node child : currentNode.getConfiningNodes()){
                boolean isNewNode = true;
                Node childCopy;

                if (wayType.getWayTypeNodes().get(child.getID()) == null) continue;
                if (notVisitedList.contains(child)) continue;
                if (child.equals(currentNode.getParent())) continue;
                if (visitedList.contains(child)){
                    double currentPathG = currentNode.getG() + Math.pow(Math.pow((currentNode.getLat() - child.getLat()), 2) + Math.pow((currentNode.getLon() - child.getLon()), 2), 0.5);

                    if (currentPathG < child.getG()) isNewNode = false;
                    else continue;
                }

                childCopy = child.clone();
                childCopy.setParent(currentNode);
                childCopy.setG(currentNode.getG() + Math.pow(Math.pow((currentNode.getLat() - child.getLat()), 2) + Math.pow((currentNode.getLon() - child.getLon()), 2), 0.5));
                childCopy.setH(Math.pow(Math.pow(child.getLon() - endNode.getLon(), 2) + Math.pow(child.getLat() - endNode.getLat(), 2), 0.5));
                childCopy.setF(childCopy.getG() + childCopy.getH());

                if (isNewNode) notVisitedList.add(childCopy);
            }
        }
        return null;
    }

    private double[] getPositionBetweenNodes(Node startNode, Node endNode, double distanceFromStartNode){
        double deltaX = endNode.getLon() - startNode.getLon();
        double deltaY = endNode.getLat() - startNode.getLat();
        double distanceBetweenNodes = Math.sqrt(Math.pow(endNode.getLon() - startNode.getLon(), 2) + Math.pow(endNode.getLat() - startNode.getLat(), 2));

        double xPos = (distanceFromStartNode / distanceBetweenNodes) * deltaX + startNode.getLon();
        double yPos = (distanceFromStartNode / distanceBetweenNodes) * deltaY + startNode.getLat();

        return new double[]{xPos, yPos};
    }
}
