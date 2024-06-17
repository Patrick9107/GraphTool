package org.spengergasse.graphentool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Graph {

    private Matrix matrix;

    public Graph() {
    }

    public Graph(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Matrix calculateDistanceMatrix() {
        List<List<Integer>> adjacencyMatrix = matrix.getMatrix();
        int size = adjacencyMatrix.size();
        Matrix distanceMatrix = new Matrix(size);
        List<List<Integer>> distanceMatrixAsList = distanceMatrix.getMatrix();

        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            for (int j = 0; j < adjacencyMatrix.get(i).size(); j++) {
                Integer value = adjacencyMatrix.get(i).get(j);
                if (i == j) {
                    distanceMatrixAsList.get(i).set(j, 0);
                } else if (value.equals(1)) {
                    distanceMatrixAsList.get(i).set(j, 1);
                } else if (value.equals(0)) {
                    distanceMatrixAsList.get(i).set(j, Integer.MAX_VALUE);
                }
            }
        }

        int k = 2;

        List<List<Integer>> adjacencyMatrixKPower = adjacencyMatrix;
        while (true) {
            List<List<Integer>> prevDistMatrix = Matrix.createCopy(distanceMatrixAsList);
            adjacencyMatrixKPower = Matrix.matrixMultiplication(adjacencyMatrixKPower, adjacencyMatrix);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {

                    if (distanceMatrixAsList.get(i).get(j).equals(Integer.MAX_VALUE) && !adjacencyMatrixKPower.get(i).get(j).equals(0)) {
                        distanceMatrixAsList.get(i).set(j, k);
                    }
                }
            }

            k++;

            if (isEqual(distanceMatrixAsList, prevDistMatrix) || Matrix.noInfinity(distanceMatrixAsList)) {
                break;
            }
        }

        return distanceMatrix;
    }

    public Matrix calculatePathMatrix() {
        List<List<Integer>> adjacencyMatrix = matrix.getMatrix();
        int size = adjacencyMatrix.size();
        Matrix pathMatrix = new Matrix(Matrix.createCopy(adjacencyMatrix));
        List<List<Integer>> pathMatrixAsList = pathMatrix.getMatrix();

        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            for (int j = 0; j < adjacencyMatrix.get(i).size(); j++) {
                if (i == j) {
                    pathMatrixAsList.get(i).set(j, 1);
                }
            }
        }

        int k = 2;

        List<List<Integer>> adjacencyMatrixKPower = adjacencyMatrix;
        while (true) {
            List<List<Integer>> prevPathMatrix = Matrix.createCopy(pathMatrixAsList);
            adjacencyMatrixKPower = Matrix.matrixMultiplication(adjacencyMatrixKPower, adjacencyMatrix);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {

                    if (i != j && !adjacencyMatrixKPower.get(i).get(j).equals(0)) {
                        pathMatrixAsList.get(i).set(j, 1);
                    }
                }
            }

            k++;

            if (isEqual(pathMatrixAsList, prevPathMatrix) || Matrix.noZeros(pathMatrixAsList)) {
                break;
            }
        }

        return pathMatrix;
    }

    public List<List<String>> componentSearch(Matrix matrix) {
        return componentSearch(matrix.getMatrix());
    }

    public List<List<String>> componentSearch(List<List<Integer>> pathMatrix) {
        List<List<String>> components = new ArrayList<>();
        pathMatrix = Matrix.createCopy(pathMatrix);
        int size = pathMatrix.size();

        for (int i = 0; i < size; i++) {
            ArrayList<String> newComponent = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (pathMatrix.get(i).get(j).equals(1)) {
                    newComponent.add(String.valueOf((char) ('A' + j)));
                }
            }
            if (!newComponent.isEmpty()) {
                boolean visited = true;
                Iterator<List<String>> iterator = components.iterator();
                while (iterator.hasNext()) {
                    List<String> component = iterator.next();
                    if (new HashSet<>(component).containsAll(newComponent)) {
                        visited = false;
                        break;
                    }
                    if (new HashSet<>(newComponent).containsAll(component)) {
                        iterator.remove();
                    }
                }
                if (visited) {
                    components.add(newComponent);
                }
            }
        }
        return components;
    }

    private static boolean isEqual(List<List<Integer>> a, List<List<Integer>> b) {
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < a.get(i).size(); j++) {
                if (!a.get(i).get(j).equals(b.get(i).get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
