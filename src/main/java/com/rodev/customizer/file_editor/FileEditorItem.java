package com.rodev.customizer.file_editor;

import javafx.scene.control.TreeItem;

import java.util.function.BiConsumer;

public class FileEditorItem extends TreeItem<String> {

    private final FileEditor.Node node;

    public FileEditorItem(String item, FileEditor.Node node){
        super(item);
        this.node = node;
    }

    public void onClicked(BiConsumer<String, String> callback){
        callback.accept(node.getOriginalText(), node.getInputText());
    }

    public void onSwitched(String input){
        node.setInputText(input);
    }
}
