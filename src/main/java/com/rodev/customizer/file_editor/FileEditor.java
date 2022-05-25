package com.rodev.customizer.file_editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FileEditor {

    private final List<Node> nodes = new ArrayList<>();

    public FileEditor(File fileToEdit){

    }

    public void addNode(Node node){
        nodes.add(node);
    }

    public void onSaveAll(BiConsumer<String, String> stringByKey){
        nodes.forEach(node -> {
            stringByKey.accept(node.configKey, node.inputText);
        });
    }

    public static class Node {
        public final String configKey;

        private String inputText;
        private String originalText;

        public Node(String originalText, String configKey){
            this.originalText = originalText;
            this.inputText = originalText;
            this.configKey = configKey;
        }

        public String getOriginalText(){
            return originalText;
        }

        public void setOriginalText(String originalText) {
            this.originalText = originalText;
        }

        public String getInputText() {
            return inputText;
        }

        public void setInputText(String inputText) {
            this.inputText = inputText;
        }
    }
}
