package backend.map;

import frontend.map.Map;

import java.util.ArrayList;

public class Node {
    private Node parent;
    private final long ID;
    private final double lat, lon, mapX, mapY;
    private double g, h, f;
    private final ArrayList<Node> confiningNodes;
    private final Map map;

    public Node(long ID, double lat, double lon, Map map){
        this.parent = null;
        this.map = map;
        this.confiningNodes = new ArrayList<>();
        this.ID = ID;
        this.lat = lat;
        this.lon = lon;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        mapX = map.getMapX(lon);
        mapY = map.getMapY(lat);
    }

    public Node(long ID, double lat, double lon, Map map, double g, double h, double f, ArrayList<Node> confiningNodes, Node parent){
        this.parent = parent;
        this.map = map;
        this.confiningNodes = confiningNodes;
        this.ID = ID;
        this.lat = lat;
        this.lon = lon;
        this.g = g;
        this.h = h;
        this.f = f;
        mapX = (lon - map.getMinLongitude()) * map.getRatio();
        mapY = map.getMapSize() - (lat - map.getMinLatitude()) * map.getRatio();
    }

    @Override
    public Node clone(){
        return new Node(ID, lat, lon, map, g, h, f, confiningNodes, parent);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if((obj == null) || (obj.getClass() != this.getClass())) return false;
        return ID == ((Node) obj).getID();
    }

    public ArrayList<Node> getConfiningNodes() {
        return confiningNodes;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public long getID() {
        return ID;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getMapX() {
        return mapX;
    }

    public double getMapY() {
        return mapY;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }
}