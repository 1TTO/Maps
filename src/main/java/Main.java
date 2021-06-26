import backend.movables.Car;
import frontend.ui.MapsUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import backend.simulation.Simulation;

public class Main extends Application {
    private static final Simulation simulation = new Simulation();

    @Override
    public void start(Stage primaryStage) throws Exception{
        MapsUI root = new MapsUI(simulation.getMap());
        Car car = new Car(simulation.getMap().getStreets().get(0).getNodes().get(0), simulation.getMap().getStreets().get(5).getNodes().get(2), simulation.getMap().getStreets(), simulation.getMap(), 50, 30);

        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();

        simulation.addMovable(car);
        simulation.startMovable(car);
    }

    @Override
    public void stop(){
        simulation.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
