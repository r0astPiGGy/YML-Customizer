package com.rodev.customizer;

import com.rodev.customizer.file_editor.FileEditorController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    private Label selectFileToEdit;

    private FileEditorController fileEditorController;

    public void initialize(){
        fileEditorController = new FileEditorController();
    }

    @FXML
    protected void onHelloButtonClick() throws IOException {
        Stage stage = (Stage) selectFileToEdit.getScene().getWindow();
        var chooser = new FileChooser();
        chooser.setTitle("Choose file to edit");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("YML", "*.yml")
        );
        File file = chooser.showOpenDialog(stage);
        if(file == null) return;

        if(file.getName().endsWith(".yml")){
            fileEditorController.start(stage, file);
        }
    }
}