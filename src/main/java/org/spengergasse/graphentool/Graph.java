package org.spengergasse.graphentool;

import java.util.*;

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

    public Matrix calculatePathMatrix(Matrix adjacencyMatrix) {
        return calculatePathMatrix(adjacencyMatrix.getMatrix());
    }

    public Matrix calculatePathMatrix(List<List<Integer>> adjacencyMatrix) {
        int size = adjacencyMatrix.size();
        Matrix pathMatrix = new Matrix(Matrix.createCopy(adjacencyMatrix));
        List<List<Integer>> pathMatrixAsList = pathMatrix.getMatrix();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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

    public Map<String, List<String>> findComponents(Matrix pathMatrix) {
        return findComponents(pathMatrix.getMatrix());
    }

    public Map<String, List<String>> findComponents(List<List<Integer>> pathMatrix) {
        int n = pathMatrix.size();
        Map<String, List<String>> componentMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(pathMatrix.get(i).get(j));
            }
            String row = sb.toString();
            componentMap.putIfAbsent(row, new ArrayList<>());
            componentMap.get(row).add(String.valueOf((char) ('A' + i)));
        }

        return componentMap;
    }

    public List<String> calculateArticulations() {
        int componentCount = findComponents(calculatePathMatrix(matrix)).size();
        List<String> articulationPoints = new ArrayList<>();
        int size = matrix.getMatrix().size();
        List<List<Integer>> adjacencyMatrixList = matrix.getMatrix();

        for (int i = 0; i < size; i++) {
            List<List<Integer>> adjacencyMatrixCopy = Matrix.createCopy(adjacencyMatrixList);

            for (int j = 0; j < size; j++) {
                adjacencyMatrixCopy.get(i).set(j, 0);
                adjacencyMatrixCopy.get(j).set(i, 0);
            }
            int componentCountWithVertexRemoved = findComponents(calculatePathMatrix(adjacencyMatrixCopy)).size();

            if (componentCountWithVertexRemoved - 1 > componentCount) {
                articulationPoints.add(String.valueOf((char) ('A' + i)));
            }

        }

        return articulationPoints;
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
