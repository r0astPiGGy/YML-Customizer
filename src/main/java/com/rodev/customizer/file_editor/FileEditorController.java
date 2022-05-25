package com.rodev.customizer.file_editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FileEditorController {

    private static File fileToEdit;

    @FXML protected TextArea inputText;
    @FXML protected TextArea originalText;

    @FXML protected TreeView<String> treeView;

    @FXML protected Button prevButton;
    @FXML protected Button nextButton;
    @FXML protected Button saveButton;

    private final FileConfiguration fileConfiguration = new YamlConfiguration();

    private FileEditor fileEditor;

    public void start(Stage window, File fileToEdit) throws IOException {
        FileEditorController.fileToEdit = fileToEdit;
        Parent root = new FXMLLoader(FileEditorController.class.getResource("file_editor.fxml")).load();

        var prevScene = window.getScene();
        var scene = new Scene(root, prevScene.getWidth(), prevScene.getHeight());

        window.setScene(scene);
        window.show();
    }

    public void initialize(){
        prepare();
    }

    private void prepare(){
        try {
            fileConfiguration.load(fileToEdit);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        fileEditor = new FileEditor(fileToEdit);
        String fileName = fileToEdit.getName();

        var root = iterateThroughConfig(fileConfiguration);
        root.setValue(fileName);

        treeView.setRoot(root);

        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClicked);
    }

    private TreeItem<String> iterateThroughConfig(ConfigurationSection section){
        var stringTreeItem = new TreeItem<>(section.getName());
        var currentPath = section.getCurrentPath() + ".";
        section.getKeys(false).forEach(key -> {
            Object obj = section.get(key);
            if(obj == null) return;

            TreeItem<String> child;

            // Checking whether key is branch or leaf
            if(obj instanceof ConfigurationSection){
                child = iterateThroughConfig((ConfigurationSection) obj);
            } else {
                var objectConfigKey = currentPath + key;
                FileEditor.Node node = new FileEditor.Node(obj.toString(), objectConfigKey);
                fileEditor.addNode(node);
                child = new FileEditorItem(key, node);
            }
            stringTreeItem.getChildren().add(child);
        });
        return stringTreeItem;
    }

    private FileEditorItem currentEditingItem;

    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        if (!(node instanceof TreeCell) && !(node instanceof Text)) return;

        var item = treeView.getSelectionModel().getSelectedItem();

        if(!item.isLeaf()){
//            TreeItem<String> parent = item.getParent();
//            if(parent != null){
//                if(parent.getParent() != null) return;
//
//                System.out.println("FILE");
//
//                return;
//            }
//
//            System.out.println("ROOT");
            return;
        }

        selectEditItem((FileEditorItem) item);
    }

    @FXML
    protected void onNextButtonPressed(){
        System.out.println("Button NEXT pressed");
    }

    @FXML
    protected void onPrevButtonPressed(){
        System.out.println("Button PREV pressed");
    }

    private void saveCurrentEditingItem(){
        if(currentEditingItem == null) return;

        saveEditItem(currentEditingItem);
    }

    private void saveEditItem(FileEditorItem item){
        item.onSwitched(inputText.getText());
    }

    private void selectEditItem(FileEditorItem item){
        if(currentEditingItem == item) return;

        if(currentEditingItem != null){
            saveCurrentEditingItem();
        }

        currentEditingItem = item;

        prepareTextEditor();
    }

    private void prepareTextEditor(){
        currentEditingItem.onClicked((original, input)-> {
            originalText.setText(original);
            inputText.setText(input);
        });
    }

    boolean saveEnabled = true;

    @FXML
    protected void onSaveButtonPressed() throws IOException {
        if(!saveEnabled) return;
        saveEnabled = false;

        Stage stage = (Stage)saveButton.getScene().getWindow();

        String title = stage.getTitle();

        stage.setTitle("Saving...");

        saveCurrentEditingItem();

        fileEditor.onSaveAll(fileConfiguration::set);

        File fileToSave = getFileToSave(stage);

        if(fileToSave == null) return;

        fileConfiguration.save(fileToSave);
        stage.setTitle("Saved!");
        saveButton.setDisable(true);

        CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS).execute(()->{
            Platform.runLater(()-> {
                stage.setTitle(title);
                saveButton.setDisable(false);
                saveEnabled = true;
                System.out.println("Done");
            });
        });
    }

    protected File getFileToSave(Window window){
        var fileChooser = new FileChooser();

        fileChooser.setTitle("Where i should save this?");
        fileChooser.setInitialDirectory(fileToEdit.getParentFile());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("YML", "*.yml")
        );
        File newFile = fileChooser.showOpenDialog(window);

        if(newFile.equals(fileToEdit)){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Overwrite file?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                return newFile;
            }
            if (alert.getResult() == ButtonType.NO){
                return getFileToSave(window);
            }
            if (alert.getResult() == ButtonType.CANCEL){
                return null;
            }
        }

        return newFile;
    }
}
