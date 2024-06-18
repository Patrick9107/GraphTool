package org.spengergasse.graphentool;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class GraphController {

    private static final Graph graph = new Graph(new Matrix());

    private static int vertexCounter = 0;

    private static Group selected = null;

    @FXML
    public AnchorPane graphArea;

    private Stage primaryStage;

    @FXML
    public TextArea eccentricities;
    @FXML
    public TextArea radius;

    @FXML
    public TextArea diameter;

    @FXML
    public TextArea center;

    @FXML
    public TextArea distanceMatrix;

    @FXML
    public TextArea components;

    @FXML
    public TextArea adjacencyMatrix;

    @FXML
    public TextArea articulations;

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
                circle.setFill(Color.LIGHTGRAY);
            } else {
                if (group == selected) {
                    selected = null;
                    circle.setFill(Color.WHITE);
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

//    private static class Delta {
//        double x, y;
//    }

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
                    graphArea.getChildren().addFirst(line);
                }
            }
        }
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
    public void calculate() {
        calculateEccentricitiesRadiusDiameterCenter();
        components();
        printAdjacencyMatrix();
        articulations();
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

    private void calculateEccentricitiesRadiusDiameterCenter() {
        Matrix distanceMatrix = graph.calculateDistanceMatrix();
        this.distanceMatrix.setText("Distanzmatrix:" + System.lineSeparator() + distanceMatrix.toString());
        if (Matrix.noInfinity(distanceMatrix)) {
            this.eccentricities.setText("Exzentrizitäten: " + Matrix.getEccentricitiesFromDistanceMatrix(distanceMatrix));
            Map<String, Integer> map = Matrix.getRadDmCenterFromDistanceMatrixWithMap(distanceMatrix);
            map.values().stream().mapToInt(Integer::intValue).min().ifPresent(value -> this.radius.setText("Radius: rad(G)=" + value));
            this.center.setText(getCenterFromMap(map));
            map.values().stream().mapToInt(Integer::intValue).max().ifPresent(value -> this.diameter.setText("Durchmesser: dm(G)=" + value));
        } else {
            this.eccentricities.setText("Exzentrizitäten: Graph nicht zusammenhängend");
            this.radius.setText("Radius: Graph nicht zusammenhängend");
            this.center.setText("Zentrum: Graph nicht zusammenhängend");
            this.diameter.setText("Durchmesser: Graph nicht zusammenhängend");
        }
    }

    private String getCenterFromMap(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("Zentrum: Z(G)={");

        int minValue = map.values()
                .stream()
                .min(Integer::compare)
                .orElseThrow();

        map.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == minValue)
                .map(Map.Entry::getKey)
                .forEach(s -> sb.append(s).append(","));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    public void articulations() {
        List<String> articulations = graph.calculateArticulations();
        StringBuilder sb = new StringBuilder("Artikulationen: {");
        if (!articulations.isEmpty()) {
            articulations.forEach(s -> sb.append(s).append(","));
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        this.articulations.setText(sb.toString());
    }

    @FXML
    public void components() {
        Map<String, List<String>> map = graph.findComponents(graph.calculatePathMatrix(graph.getMatrix()));
        List<List<String>> components = map.values().stream().toList();
        StringBuilder sb = new StringBuilder();
        for (List<String> component : components) {
            sb.append("{");
            for (String s : component) {
                sb.append(s).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}, ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        this.components.setText("Komponente: " + sb);
    }

    public void printAdjacencyMatrix() {
        this.adjacencyMatrix.setText("Adjazenzmatrix:" + System.lineSeparator() + graph.getMatrix().toString());
    }
}
