module org.spengergasse.graphentool {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.spengergasse.graphentool to javafx.fxml;
    exports org.spengergasse.graphentool;
}