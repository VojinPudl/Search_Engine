package search_engine.search_engine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/search_engine/search_engine/Main_Layout.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = new Scene(root, 1200, 800);
        File conf = new File("src/main/resources/search_engine/search_engine/conf.d");
        Config config = new Config(conf,scene);
        controller.setScene(scene);
        stage.setTitle("ASW-Search-Engine");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}