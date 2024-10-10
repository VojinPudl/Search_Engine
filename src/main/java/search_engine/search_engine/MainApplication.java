package search_engine.search_engine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/search_engine/search_engine/Main_Layout.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/search_engine/search_engine/style_light_mode.css")).toExternalForm());
        stage.setTitle("Search Engine");
        stage.setResizable(false);
        stage.setScene(scene);
        controller.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}