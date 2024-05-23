module com.example.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.proyecto to javafx.fxml;
    exports com.example.proyecto;

    exports com.example.proyecto.model;
    opens com.example.proyecto.model to com.google.gson, javafx.fxml;

    opens com.example.proyecto.response to com.google.gson;
}
