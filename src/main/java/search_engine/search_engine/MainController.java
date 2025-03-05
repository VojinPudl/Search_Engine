package search_engine.search_engine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainController {
    public static String RootPath;
    public MenuItem setRootPathItem;
    public Button searchButton;
    public TextField searchTextField;
    public ListView<Button> listView;
    public javafx.scene.control.TextArea TextArea;
    public MenuItem refreshItem;
    public MenuItem darkmodeItem;
    public MenuItem lightmodeItem;
    public MenuItem buttonShowFullFile;

    ArrayList<MyFile> fileList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();

    private Scene scene;

    public MainController() {
        listView = new ListView<>();
    }

    public boolean darkmode=false;


    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void InsertIntoListView() {
        listView.setItems(null);
        ObservableList<Button> items = FXCollections.observableArrayList();
        for (String s : stringArrayList) {
            System.out.println("Přidávám do seznamu: " + s);
            Button button = new Button(s);
            button.setText(s);
            if (!darkmode) {
                button.setStyle("-fx-background-color: transparent; "
                        + "-fx-text-fill: black; "
                        + "-fx-padding: 0 32; "
                        + "-fx-font-size: 14px; "
                        + "-fx-background-radius: 3; "
                        + "-fx-border-width: 0;");
            } else {
                button.setStyle("-fx-background-color: transparent; "
                        + "-fx-text-fill: white; "
                        + "-fx-padding: 0 32; "
                        + "-fx-font-size: 14px; "
                        + "-fx-background-radius: 3; "
                        + "-fx-border-width: 0;");
            }
            button.setOnAction(event -> OpenFile(s));
            items.add(button);
        }
        listView.setItems(items);
    }

    public void SetRootPath() {
        fileList = new ArrayList<>();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            System.out.println("Selected directory: " + selectedDirectory.getAbsolutePath());
        }
        assert selectedDirectory != null;
        RootPath = selectedDirectory.getAbsolutePath();
        InsertFilesIntoList(RootPath, fileList);
        InsertIntoListView();
    }

    public void Search() {
        TextArea.setText("");

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
        System.out.println();
        MyFile file = fileList.get(index);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getFile()))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = fileReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
                System.out.println(line);
            }
            System.out.println();
            TextArea.appendText("---------------------------------------------" + "\n");
            TextArea.appendText(file.file.getName() + "\n");
            TextArea.appendText("---------------------------------------------" + "\n");
            TextArea.appendText(String.valueOf(stringBuilder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetDarkMode() {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/search_engine/search_engine/style_dark_mode.css")).toExternalForm());
            darkmode=true;
            if (!fileList.isEmpty() && !stringArrayList.isEmpty())
                RefreshFiles();
        } else {
            System.out.println("Scene is not set.");
        }
    }

    public void SetLightMode() {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/search_engine/search_engine/style_light_mode.css")).toExternalForm());
            darkmode=false;
            if (!fileList.isEmpty() && !stringArrayList.isEmpty())
                RefreshFiles();
        } else {
            System.out.println("Scene is not set.");
        }
    }

    public void SearchPressedEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            Search();
        }
    }

    public void Close() {
        System.exit(0);
    }

    public void ShowFullFile() {
    }
}