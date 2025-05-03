package search_engine.search_engine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController.initConfig();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/search_engine/search_engine/Main_Layout.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = new Scene(root, 1200, 800);
        Config config = new Config(MainController.CONFIG_PATH.toFile(), scene);
        controller.setScene(scene);
        try (InputStream input = MainApplication.class.getResourceAsStream("/search_engine/search_engine/ASW-SEARCH-ENGINE.png")) {
            if (input != null) {
                stage.getIcons().add(new Image(input));
            }
        }
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("ASW-Search-Engine");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}