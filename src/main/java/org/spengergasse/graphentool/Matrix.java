package org.spengergasse.graphentool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Matrix {

    private List<List<Integer>> matrix;

    public Matrix() {
        this.matrix = new ArrayList<>();
    }

    public Matrix(int size) {
        setMatrixWithZeros(size);
    }

    public Matrix(List<List<Integer>> matrix) {
        this.matrix = matrix;
    }

    public static Matrix readFromCsv(String path) {

        Matrix matrix = new Matrix();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = br.readLine()) != null) {
                List<Integer> row = new ArrayList<>();
                matrix.getMatrix().add(row);
                String[] fields = line.split(";");

                for (String field : fields) {
                    row.add(Integer.parseInt(field));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return matrix;
    }

    public void setMatrix(List<List<Integer>> matrix) {
        this.matrix = matrix;
    }

    public void setMatrixWithZeros(int size) {
        matrix = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                row.add(0);
            }
            matrix.add(row);
        }
    }

    public static List<List<Integer>> createCopy(List<List<Integer>> original) {
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> row : original) {
            List<Integer> rowCopy = new ArrayList<>(row);
            copy.add(rowCopy);
        }
        return copy;
    }


    public List<List<Integer>> getMatrix() {
        return matrix;
    }

    public void addVertex() {
        matrix.forEach(integers -> integers.add(0));

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < matrix.size() + 1; i++) {
            list.add(0);
        }
        matrix.add(list);
    }

    public boolean addEdge(int v, int v1) {
        if (v < matrix.size() && v1 < matrix.size() && v != v1) {
            matrix.get(v).set(v1, 1);
            matrix.get(v1).set(v, 1);
            return true;
        }
        return false;
    }

    public void randomAdjacencyMatrix() {
        Random random = new Random();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.size(); j++) {
                if (j != i) {
                    int value = random.nextInt(2);
                    matrix.get(i).set(j, value);
                    matrix.get(j).set(i, value);
                }
            }
        }
    }

    public void print() {
        for (List<Integer> row : matrix) {

            for (Integer value : row) {
                if (value.equals(Integer.MAX_VALUE)) {
                    System.out.print("∞" + "\t");
                } else {
                    System.out.print(value + "\t");
                }
            }
            System.out.println();
        }
    }

    public void printWithLabel() {
        int size = matrix.size();

        System.out.print("\t");
        for (int i = 0; i < size; i++) {
            System.out.print((char) ('A' + i) + "\t");
        }
        System.out.println();


        for (int i = 0; i < size; i++) {
            System.out.print((char) ('A' + i) + "\t");
            for (int j = 0; j < size; j++) {
                Integer value = matrix.get(i).get(j);
                if (value.equals(Integer.MAX_VALUE)) {
                    System.out.print("∞" + "\t");
                } else {
                    System.out.print(matrix.get(i).get(j) + "\t");
                }
            }
            System.out.println();
        }
    }

    public static List<List<Integer>> matrixMultiplication(Matrix a, List<List<Integer>> b) {
        return matrixMultiplication(a.getMatrix(), b);
    }

    public static List<List<Integer>> matrixMultiplication(List<List<Integer>> a, Matrix b) {
        return matrixMultiplication(a, b.getMatrix());
    }

    public static List<List<Integer>> matrixMultiplication(Matrix a, Matrix b) {
        return matrixMultiplication(a.getMatrix(), b.getMatrix());
    }

    public static List<List<Integer>> matrixMultiplication(List<List<Integer>> a, List<List<Integer>> b) {
        int size = a.size();

        List<List<Integer>> c = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                row.add(0);
            }
            c.add(row);
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    c.get(i).set(j, c.get(i).get(j) + a.get(i).get(k) * b.get(k).get(j));
                }
            }
        }

        return c;
    }

    public static boolean noInfinity(Matrix matrix) {
        return noInfinity(matrix.getMatrix());
    }

    public static boolean noInfinity(List<List<Integer>> matrix) {
        for (List<Integer> row : matrix) {
            for (Integer value : row) {
                if (value.equals(Integer.MAX_VALUE)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean noZeros(Matrix matrix) {
        return noZeros(matrix.getMatrix());
    }

    public static boolean noZeros(List<List<Integer>> matrix) {
        for (List<Integer> row : matrix) {
            for (Integer value : row) {
                if (value.equals(0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getEccentricitiesFromDistanceMatrix(Matrix distanceMatrix) {
        return getEccentricitiesFromDistanceMatrix(distanceMatrix.getMatrix());
    }

    public static String getEccentricitiesFromDistanceMatrix(List<List<Integer>> distanceMatrix) {
        StringBuilder sb = new StringBuilder();
        int size = distanceMatrix.size();
        Integer max;
        for (int i = 0; i < size; i++) {
            max = distanceMatrix.get(i).get(0);
            for (int j = 0; j < size; j++) {
                Integer value = distanceMatrix.get(i).get(j);
                if (i != j && value > max) {
                    max = value;
                }
            }
            sb.append((char) ('A' + i)).append(":").append(max).append("; ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public static Map<String, Integer> getRadDmCenterFromDistanceMatrixWithMap(Matrix matrix) {
        return getRadDmCenterFromDistanceMatrixWithMap(matrix.getMatrix());
    }

    public static Map<String, Integer> getRadDmCenterFromDistanceMatrixWithMap(List<List<Integer>> distanceMatrix) {
        Map<String, Integer> map = new HashMap<>();
        int size = distanceMatrix.size();
        Integer maxPerRow;
        for (int i = 0; i < size; i++) {
            maxPerRow = distanceMatrix.get(i).get(0);
            for (int j = 0; j < size; j++) {
                Integer value = distanceMatrix.get(i).get(j);
                if (i != j && value > maxPerRow) {
                        maxPerRow = value;
                }
            }
            map.put(String.valueOf((char) ('A' + i)), maxPerRow);
        }
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<Integer> row : matrix) {
            for (Integer value : row) {
                if (value.equals(Integer.MAX_VALUE)) {
                    sb.append("∞").append("\t");
                } else {
                    sb.append(value).append("\t");
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
