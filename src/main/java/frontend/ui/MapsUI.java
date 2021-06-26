package frontend.ui;

import frontend.map.Map;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MapsUI extends AnchorPane {
    private final Map map;
    private final Button simSpeedMinus, simSpeedPlus;

    public MapsUI(Map map){
        this.map = map;

        this.simSpeedPlus = new Button("+");
        simSpeedPlus.setPrefWidth(35);
        simSpeedPlus.setPrefHeight(35);

        this.simSpeedMinus = new Button("-");
        simSpeedMinus.setPrefWidth(35);
        simSpeedMinus.setPrefHeight(35);

        this.getChildren().addAll(map, simSpeedMinus, simSpeedPlus);

        AnchorPane.setRightAnchor(simSpeedMinus, 10d);
        AnchorPane.setRightAnchor(simSpeedPlus, 55d);
        AnchorPane.setBottomAnchor(simSpeedMinus, 10d);
        AnchorPane.setBottomAnchor(simSpeedPlus, 10d);

        setListener();
    }

    void setListener(){
        simSpeedPlus.setOnMouseClicked(e->{
            map.getSimulation().setSimulationSpeed(map.getSimulation().getSimulationSpeed() * 1.2);
        });

        simSpeedMinus.setOnMouseClicked(e->{
            map.getSimulation().setSimulationSpeed(map.getSimulation().getSimulationSpeed() / 1.2);
        });
    }
}
