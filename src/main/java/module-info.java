module com.rodev.customizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bukkit;
    requires org.yaml.snakeyaml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires javafx.graphics;

    opens com.rodev.customizer to javafx.fxml;
    exports com.rodev.customizer;
    exports com.rodev.customizer.file_editor;
    opens com.rodev.customizer.file_editor to javafx.fxml;
}