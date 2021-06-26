package backend.simulation;

import backend.movables.Moveable;
import frontend.map.Map;

import java.util.ArrayList;

public class Simulation{
    private double simulationSpeed;
    private final Map map;
    private final ArrayList<Moveable> moveables;

    public Simulation(){
        map = new Map(800, 27, 11.64, 46.7, 11.67, 46.73, this);
        moveables = new ArrayList<>();
        simulationSpeed = 1;
    }

    public void stop(){
        for (Moveable moveable : moveables){
            moveable.stop();
        }
    }

    public ArrayList<Moveable> getMoveables(){
        return moveables;
    }

    public void addMovable(Moveable moveable){
        moveables.add(moveable);
    }

    public void startMovable(Moveable moveable){
        if (moveables.contains(moveable)){
            moveable.start();
        }
    }

    public void setSimulationSpeed(double simulationSpeed){
        this.simulationSpeed = simulationSpeed;
    }

    public Map getMap(){
        return map;
    }

    public double getSimulationSpeed(){
        return simulationSpeed;
    }
}
