package frontend.map;

import backend.map.Node;
import backend.map.Way;
import backend.map.WayType;
import backend.movables.Moveable;
import backend.simulation.Simulation;
import backend.utilities.Connection;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Map extends Zoomable {
    private final HashMap<Long, Node> nodes = new HashMap<>();
    private final WayType streets, cycleways, footways, buildings;
    private final ArrayList<WayType> wayTypes;
    private final Simulation simulation;
    private final double minLongitude, minLatitude, maxLongitude, maxLatitude;
    private double ratio;
    private final Rectangle background;

    public Map(int mapSize, double maxScale, double minLongitude, double minLatitude, double maxLongitude, double maxLatitude, Simulation simulation){
        super(1.2, maxScale, mapSize, mapSize);
        this.simulation = simulation;
        this.minLongitude = minLongitude;
        this.minLatitude = minLatitude;
        this.maxLongitude = maxLongitude;
        this.maxLatitude = maxLatitude;

        this.background = new Rectangle(mapSize, mapSize, Color.WHITE);
        this.getChildren().add(background);

        setRatio();
        this.streets = new WayType(this, ratio / 20000);
        this.footways = new WayType(this, ratio / 20000);
        this.cycleways = new WayType(this, ratio / 20000);
        this.buildings = new WayType(this, ratio / 20000);
        this.wayTypes = new ArrayList<>(Arrays.asList(streets, footways, cycleways, buildings));

        setup();
        draw();
    }

    void setup(){
        setNodesAndWayTypes();

        for (WayType wayType: wayTypes){
            wayType.connectNodes();
        }
    }

    void draw(){
        streets.draw();
        buildings.draw();
    }

    private void setRatio(){
        double differenceLat = Math.abs(maxLatitude - minLatitude);
        double differenceLon = Math.abs(maxLongitude - minLongitude);

        if (differenceLat >= differenceLon) ratio = background.getWidth() / differenceLat;
        else ratio = background.getHeight() / differenceLon;
    }

    private void setNodesAndWayTypes(){
        JSONObject data;
        String url = "https://api.openstreetmap.org/api/0.6/map.json?bbox=" + minLongitude + "," + minLatitude + "," + maxLongitude + "," + maxLatitude;

        try {
             data = new JSONObject(Connection.request(url));
        }catch (Exception e){
            System.err.println("Boundries to big!\nLower your parameters for the boundries!");
            simulation.stop();
            return;
        }

        JSONArray elements = data.getJSONArray("elements");
        for (Object element : elements){
            JSONObject jsonElement = (JSONObject) element;

            if (jsonElement.getString("type").equals("node")){
                long ID = jsonElement.getLong("id");

                this.nodes.put(ID, new Node(ID, jsonElement.getDouble("lat"), jsonElement.getDouble("lon"), this));
            }else {
                ArrayList<Node> wayNodes = new ArrayList<>();

                try {
                    JSONArray nodes = jsonElement.getJSONArray("nodes");

                    for (int i = 0; i < nodes.length(); i++) {
                        wayNodes.add(this.nodes.get(nodes.getLong(i)));
                    }

                    for (int i = 0; i < wayNodes.size(); i++){
                        if (i - 1 >= 0) wayNodes.get(i).getConfiningNodes().add(wayNodes.get(i - 1));
                        if (i < wayNodes.size() - 1) wayNodes.get(i).getConfiningNodes().add(wayNodes.get(i + 1));
                    }

                    try {
                        ArrayList<WayType> wayTypes = new ArrayList<>();
                        JSONObject tags = jsonElement.getJSONObject("tags");

                        try {
                            tags.getString("building");
                            wayTypes.add(buildings);
                        } catch (JSONException ignored) { }

                        try {
                            tags.getString("foot");
                            wayTypes.add(footways);
                        } catch (JSONException ignored) { }

                        try {
                            tags.getString("bicycle");
                            wayTypes.add(cycleways);
                        } catch (JSONException ignored) { }

                        try {
                            String highway = tags.getString("highway");

                            if (highway.equals("footway") && !wayTypes.contains(footways)) wayTypes.add(footways);
                            if (highway.equals("cycleway") && !wayTypes.contains(cycleways)) wayTypes.add(cycleways);
                            if (!(highway.equals("cycleway")) && !(highway.equals("footway"))) wayTypes.add(streets);
                        } catch (JSONException ignored) { }

                        Way way = new Way(wayNodes);
                        for (WayType wayType : wayTypes){
                            wayType.add(way);
                            wayType.addNodeList(wayNodes);
                        }
                    } catch (JSONException ignored) { }
                }catch (JSONException ignored) { }
            }
        }
    }

    public double getMapX(double lon){
        return  (lon - getMinLongitude()) * getRatio();
    }

    public double getMapY(double lat){
        return  getMapSize() - (lat - getMinLatitude()) * getRatio();
    }

    public double getRatio(){
        return ratio;
    }

    public double getMinLongitude(){
        return minLongitude;
    }

    public double getMinLatitude(){
        return minLatitude;
    }

    public double getMapSize(){
        return background.getWidth();
    }

    public Simulation getSimulation(){
        return simulation;
    }

    public WayType getStreets(){return streets;}

    @Override
    void afterScale(double scaleFactor) {
        for (WayType wayType: wayTypes){
            wayType.scale(1/scaleFactor);
        }

        for (Moveable moveable : simulation.getMoveables()){
            moveable.scale(scaleFactor);
        }
    }

    @Override
    void beforeScale(double scaleFactor) { }

    @Override
    void afterMove() { }

    @Override
    void beforeMove() { }
}
