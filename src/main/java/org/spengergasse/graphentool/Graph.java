package org.spengergasse.graphentool;

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
        // initalisierung
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

            if (isEqual(distanceMatrixAsList, prevDistMatrix) || noInfinity(distanceMatrixAsList)) {
                break;
            }
        }

        return distanceMatrix;
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

    private boolean noInfinity(List<List<Integer>> matrix) {
        for (List<Integer> row : matrix) {
            for (Integer value : row) {
                if (value.equals(Integer.MAX_VALUE)) {
                    return false;
                }
            }
        }
        return true;
    }
}
