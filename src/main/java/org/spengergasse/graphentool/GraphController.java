package org.spengergasse.graphentool;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GraphController {

    private static final Graph graph = new Graph(new Matrix());

    private static int vertexCounter = 0;

    private static Group selected = null;

    @FXML
    public AnchorPane graphArea;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void newVertex(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget() == graphArea) {
            Group group = createVertex(mouseEvent.getX(), mouseEvent.getY());
            graph.getMatrix().addVertex();
            graphArea.getChildren().add(group);
        }
    }

    private Group createVertex(double x, double y) {
        Group group = new Group();
        Circle circle = new Circle();
        circle.setId(String.valueOf(vertexCounter));
        circle.setFill(Color.WHITE);
        circle.setLayoutX(x);
        circle.setLayoutY(y);
        group.setOnMouseClicked(event -> {
            event.consume();
            if (selected == null) {
                selected = group;
                circle.setFill(Color.BLACK);
                circle.setOpacity(0.5);
            } else {
                if (group == selected) {
                    selected = null;
                    circle.setFill(Color.WHITE);
                    circle.setOpacity(1);
                } else {
                    newEdge(event);
                }
            }
        });
        circle.setRadius(40);
        circle.setStroke(Color.BLACK);
        circle.setStrokeType(StrokeType.INSIDE);

        Text text = new Text();
        text.setText(String.valueOf((char) ('A' + vertexCounter++)));
        text.setLayoutX(x - 10);
        text.setLayoutY(y + 10);
        text.setStrokeType(StrokeType.OUTSIDE);
        text.setStrokeWidth(0);
        text.setWrappingWidth(28);
        text.setFont(Font.font(28));
        text.setDisable(true);

        group.getChildren().addAll(circle, text);
        return group;
    }

    public void newEdge(MouseEvent mouseEvent) {
        Circle circle1 = getNodeFromGroup(selected, Circle.class);
        Circle circle2 = (Circle) mouseEvent.getTarget();
        if (graph.getMatrix().addEdge(Integer.parseInt(circle1.getId()), Integer.parseInt(circle2.getId()))) {
            Line line = new Line();

            line.setStartX(circle1.getLayoutX());
            line.setStartY(circle1.getLayoutY());
            line.setEndX(circle2.getLayoutX());
            line.setEndY(circle2.getLayoutY());

            selected = null;
            circle1.setFill(Color.WHITE);
            circle1.setOpacity(1);

            graphArea.getChildren().add(0, line);
        }
    }

//    private void addDragListeners(Group group) {
//        final Delta delta = new Delta();
//
//        Circle circle = getNodeFromGroup(group, Circle.class);
//
//        group.setOnMousePressed(event -> {
//            delta.x = group.getLayoutX() - event.getSceneX();
//            delta.y = group.getLayoutY() - event.getSceneY();
//            circle.setFill(Color.GRAY);
//            circle.setOpacity(0.5);
//        });
//
//        group.setOnMouseDragged(event -> {
//            // Update the circle's position based on the mouse drag
//            group.getChildren().forEach(node -> node.setLayoutX(event.getSceneX() + delta.x));
//            group.getChildren().forEach(node -> node.setLayoutY(event.getSceneY() + delta.y));
//        });
//
//        group.setOnMouseReleased(event -> {
//            // Change color back to original color
//            circle.setFill(Color.WHITE);
//            group.setOpacity(1);
//            selected = null;
//        });
//    }

    private static class Delta {
        double x, y;
    }

    @FXML
    public void printGraph() {
        graph.getMatrix().printWithLabel();
    }

    @FXML
    public void newRandomGraph() {
        TextInputDialog input = new TextInputDialog();
        input.setTitle("New Random Graph");
        input.setHeaderText("Enter the size of your new Graph");
        input.setContentText("Please enter the size of your Graph:");

        Optional<String> result = input.showAndWait();

        result.ifPresent(s -> {
            try {
                clear();
                graph.setMatrix(new Matrix(Integer.parseInt(s)));
                graph.getMatrix().randomAdjacencyMatrix();
                drawGraph();
            } catch (NumberFormatException e) {
                newRandomGraph();
            }
        });
    }

    private List<Group> getVertexGroups() {
        return graphArea.getChildren().stream().filter(node -> node instanceof Group).map(node -> (Group) node).toList();
    }

    private void drawEdges(Matrix matrix) {
        List<List<Integer>> adjacencyMatrix = matrix.getMatrix();
        List<Group> vertexList = getVertexGroups();
        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            for (int j = i + 1; j < adjacencyMatrix.size(); j++) {
                if (adjacencyMatrix.get(i).get(j) == 1) {

                    Group group1 = vertexList.get(i);
                    Group group2 = vertexList.get(j);
                    Circle circle1 = getNodeFromGroup(group1, Circle.class);
                    Circle circle2 = getNodeFromGroup(group2, Circle.class);
                    Line line = new Line();
                    line.setStartX(circle1.getLayoutX());
                    line.setStartY(circle1.getLayoutY());
                    line.setEndX(circle2.getLayoutX());
                    line.setEndY(circle2.getLayoutY());
                    graphArea.getChildren().add(0, line);
                }
            }
        }
    }

    @FXML
    public void printDistanceMatrix() {
        graph.calculateDistanceMatrix().printWithLabel();
    }

    @FXML
    public void readGraph() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if (file == null)
            throw new IllegalStateException("There was something wrong with opening the file");
        graph.setMatrix(Matrix.readFromCsv(file.getAbsolutePath()));
        drawGraph();
    }

    private void drawGraph() {
        clearWithoutGraphClear();
        Random random = new Random();
        Matrix matrix = graph.getMatrix();
        for (int i = 0; i < matrix.getMatrix().size(); i++) {
            double x = random.nextDouble() * (graphArea.getWidth() - 80) + 40;
            double y = random.nextDouble() * (graphArea.getHeight() - 80) + 40;
            Group vertex = createVertex(x, y);
            graphArea.getChildren().add(vertex);
        }
        drawEdges(matrix);
    }

    @FXML
    public void info() {
        Stage newWindow = new Stage();

        Button backButton = new Button("Close");
        backButton.setPrefSize( 330, 114);
        backButton.setOnAction(event -> newWindow.close());

//        prefHeight="638.0"
//        prefWidth="1749.0">
        newWindow.setHeight(640);
        newWindow.setWidth(1780);
        StackPane secondaryLayout = new StackPane(backButton);
        secondaryLayout.setAlignment(Pos.BOTTOM_CENTER);
        secondaryLayout.setPadding(new Insets(0, 0, 30, 0));
        Scene secondScene = new Scene(secondaryLayout, 300, 200);

        newWindow.setTitle("Graph Info");
        newWindow.setScene(secondScene);

        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();
    }

    @FXML
    public void clear() {
        clearWithoutGraphClear();
        graph.setMatrix(new Matrix());
    }

    private void clearWithoutGraphClear() {
        graphArea.getChildren().clear();
        selected = null;
        vertexCounter = 0;
    }

    private static <T extends Node> T getNodeFromGroup(Group group, Class<T> nodeClass) {
        Optional<Node> nodeOptional = group.getChildren().stream().filter(nodeClass::isInstance).findFirst();
        return nodeOptional.map(nodeClass::cast).orElse(null);
    }

    @FXML
    public void autoLayout() {
        drawGraph();
    }
}
