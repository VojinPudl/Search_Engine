package search_engine.search_engine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class MainController {
    public static String RootPath = "";
    public static boolean darkmode = false;
    public MenuItem setRootPathItem;
    public Button searchButton;
    public TextField searchTextField;
    public ListView<Button> listView;
    public javafx.scene.control.TextArea TextArea;
    public MenuItem refreshItem;
    public MenuItem darkmodeItem;
    public MenuItem lightmodeItem;
    public MenuItem buttonShowInfo;
    public MenuItem ResetPathItem;
    ArrayList<MyFile> fileList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();
    private Scene scene;
    public static final Path CONFIG_PATH = getConfigPath();

    public MainController() {
        listView = new ListView<>();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private static Path getConfigPath() {
        String userHome = System.getProperty("user.home");
        String appName = "ASW-Search-Engine";
        String os = System.getProperty("os.name").toLowerCase();
        Path configDir;

        if (os.contains("win")) {
            configDir = Paths.get(System.getenv("APPDATA"), appName);
        } else {
            configDir = Paths.get(userHome, ".config", appName.toLowerCase());
        }

        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            throw new RuntimeException("Nelze vytvořit konfigurační adresář: " + configDir, e);
        }

        return configDir.resolve("conf.d");
    }

    public static void initConfig() throws IOException {
        if (!Files.exists(CONFIG_PATH)) {
            InputStream defaultConfig = MainApplication.class.getResourceAsStream("/search_engine/search_engine/conf.d");
            if (defaultConfig != null) {
                Files.copy(defaultConfig, CONFIG_PATH);
                defaultConfig.close();
            } else {
                Files.createFile(CONFIG_PATH);
            }
        }
    }

    public void InsertIntoListView() {
        listView.setItems(null);
        ObservableList<Button> items = FXCollections.observableArrayList();
        for (String s : stringArrayList) {
            System.out.println("Přidávám do seznamu: " + s);
            Button button = getButton(s);
            items.add(button);
        }
        listView.setItems(items);
    }

    public Button getButton(String s) {
        Button button = new Button(s);
        button.setText(s);
        button.setMnemonicParsing(false);
        button.resize(500, 100);
        if (!darkmode) {
            button.setStyle("-fx-background-color: transparent; "
                    + "-fx-text-fill: black; "
                    + "-fx-padding: 0 32; "
                    + "-fx-font-size: 14px; "
                    + "-fx-border-width: 0;");
        } else {
            button.setStyle("-fx-background-color: transparent; "
                    + "-fx-text-fill: white; "
                    + "-fx-padding: 0 32; "
                    + "-fx-font-size: 14px; "
                    + "-fx-border-width: 0;");
        }
        button.setOnAction(event -> OpenFile(s));
        return button;
    }

    public void SetRootPath() throws IOException {
        fileList = new ArrayList<>();
        stringArrayList = new ArrayList<>();
        if (RootPath.isEmpty()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder");
            Stage stage = new Stage();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if (selectedDirectory != null) {
                System.out.println("Selected directory: " + selectedDirectory.getAbsolutePath());
                RootPath = selectedDirectory.getAbsolutePath();
            }
        }
        InsertFilesIntoList(RootPath, fileList);
        InsertIntoListView();
        SetConfig();
    }

    public void Search() {
        TextArea.setText("");
        if (searchTextField.getText().isEmpty()) {
            RefreshFiles();
            return;
        }
        for (MyFile file : fileList) {
            if (Objects.equals(searchTextField.getText(), "")) break;
            try (BufferedReader fileReader = new BufferedReader(new FileReader(String.valueOf(file.getFile())))) {
                boolean found = file.getFile().getName().toLowerCase().contains(searchTextField.getText());
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = fileReader.readLine()) != null) {
                    if (line.toLowerCase().contains(searchTextField.getText().toLowerCase()))
                        found = true;
                    stringBuilder.append(line).append("\n");
                }
                if (found) {
                    TextArea.appendText("---------------------------------------------" + "\n");
                    TextArea.appendText(file.getFile().getName() + "\n");
                    TextArea.appendText("---------------------------------------------" + "\n");
                    TextArea.appendText(String.valueOf(stringBuilder));
                } else {
                    System.out.println("Neobsahuje text. Prohledávám další soubor.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void InsertFilesIntoList(String path, ArrayList<MyFile> fileList) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        MyFile myFile = new MyFile(file);
                        fileList.add(myFile);
                        System.out.println("Načítám soubor: " + myFile.getFile().getName());
                        stringArrayList.add(myFile.getFile().getName());
                    }
                }
            }
        } else System.out.println("Zadaná cesta neexistuje nebo není adresář.");
    }

    public void RefreshFiles() {
        fileList.clear();
        stringArrayList.clear();
        InsertFilesIntoList(RootPath, fileList);
        InsertIntoListView();
    }

    public void OpenFile(String name) {
        searchTextField.setText(null);
        TextArea.clear();
        int index = 0;
        for (int i = 0; i < fileList.size(); i++) {
            index = i;
            if (fileList.get(i).getFile().getName().equals(name))
                break;
        }
        MyFile file = fileList.get(index);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getFile()))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = fileReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
                System.out.println(line);
            }
            TextArea.appendText("---------------------------------------------" + "\n");
            TextArea.appendText(file.getFile().getName() + "\n");
            TextArea.appendText("---------------------------------------------" + "\n");
            TextArea.appendText(String.valueOf(stringBuilder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetDarkMode() throws IOException {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/search_engine/search_engine/style_dark_mode.css")).toExternalForm());
            darkmode = true;
            if (!fileList.isEmpty() && !stringArrayList.isEmpty())
                RefreshFiles();
            SetConfig();
        } else {
            System.out.println("Scene is not set.");
        }
    }

    public void SetLightMode() throws IOException {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/search_engine/search_engine/style_light_mode.css")).toExternalForm());
            darkmode = false;
            if (!fileList.isEmpty() && !stringArrayList.isEmpty())
                RefreshFiles();
            SetConfig();
        } else {
            System.out.println("Scene is not set.");
        }
    }

    public void SearchPressedEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            Search();
        }
        while (stringArrayList.contains(searchTextField.getText())) {
            listView.setItems(null);
            ObservableList<Button> items = FXCollections.observableArrayList();
            for (String s : stringArrayList) {
                if (s.contains(searchTextField.getText())) {
                    Button button = getButton(s);
                    items.add(button);
                }
            }
            listView.setItems(items);
            InsertIntoListView();
        }
    }

    public void SetConfig() throws IOException {
        try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
            fileWriter.write("skin=" + (darkmode ? 1 : 0) + ";path=" + (RootPath == null ? "" : RootPath) + ";");
        }
    }

    public void Close() {
        System.exit(0);
    }

    public void SearchDynamically(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            SearchPressedEnter(keyEvent);
        }
        if (searchTextField.getText().isEmpty()) {
            RefreshFiles();
            return;
        }
        ObservableList<Button> items = FXCollections.observableArrayList();
        for (String s : stringArrayList) {
            if (s.toLowerCase().contains(searchTextField.getText().toLowerCase())) {
                Button button = getButton(s);
                items.add(button);
            }
        }
        listView.setItems(items);
    }

    public void ShowInfo() throws IOException {
        Stage infoStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/search_engine/search_engine/Info_Layout.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 600, 400);
        Config config = new Config(CONFIG_PATH.toFile(), scene);
        try (InputStream iconStream = MainApplication.class
                .getResourceAsStream("/search_engine/search_engine/ASW-SEARCH-ENGINE.png")) {
            if (iconStream != null) {
                infoStage.getIcons().add(new Image(iconStream));
            }
        }
        infoStage.setTitle("ASW-Search-Engine");
        infoStage.setResizable(false);
        infoStage.setScene(scene);
        infoStage.show();
    }

    public void ResetPath() throws IOException {
        RootPath = "";
        SetRootPath();
    }
}