package search_engine.search_engine;

import javafx.scene.Scene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class Config {
    public int skin_var;
    public String Path_var;

    public Config(File conf, Scene scene) throws IOException {
        skin_var = getSkin(conf);
        if (skin_var == 1) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/search_engine/search_engine/style_dark_mode.css")).toExternalForm());
            MainController.darkmode = true;
        }
        else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/search_engine/search_engine/style_light_mode.css")).toExternalForm());
            MainController.darkmode = false;
        }
        Path_var = getPath(conf);
        MainController.RootPath = Path_var;
    }

    public int getSkin(File file) throws IOException {
        if (!file.exists())
            return 0;
        FileReader fileReader = new FileReader(file);
        StringBuilder stringBuilder = new StringBuilder();
        int ch;
        while ((ch = fileReader.read()) != -1){
            stringBuilder.append((char) ch);
        }
        fileReader.close();
        String config = stringBuilder.toString();
        if (config.split(";")[0].split("=").length > 1)
            return Integer.parseInt(config.split(";")[0].split("=")[1]);
        return 0;
    }

    public String getPath(File file) throws IOException {
        if (!file.exists())
            return "";
        FileReader fileReader = new FileReader(file);
        StringBuilder stringBuilder = new StringBuilder();
        int ch;
        while ((ch = fileReader.read()) != -1){
            stringBuilder.append((char) ch);
        }
        fileReader.close();
        String config = stringBuilder.toString();
        if ((config.split(";")[1].split("=").length > 1))
            return config.split(";")[1].split("=")[1];
        return "";
    }
}
