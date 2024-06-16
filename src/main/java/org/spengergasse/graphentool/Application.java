package org.spengergasse.graphentool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/graph-view.fxml"));
        Parent root = loader.load();

        GraphController controller = loader.getController();
        controller.setPrimaryStage(stage);

        stage.setMaximized(true);
        stage.setTitle("Graphentool");
        stage.setScene(new Scene(root));

        stage.show();
    }


    private void newGraphEvent() {
        Matrix matrix = new Matrix(4);
        matrix.randomAdjacencyMatrix();
        matrix.print();
    }

    public static void main(String[] args) {
        launch(args);
    }
}