module com.prichardsondev.ann {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens com.prichardsondev.ann to javafx.fxml;
    exports com.prichardsondev.ann;
    exports com.prichardsondev.ann.controller;
    opens com.prichardsondev.ann.controller to javafx.fxml;
    exports com.prichardsondev.ann.controller.util;
    opens com.prichardsondev.ann.controller.util to javafx.fxml;
}