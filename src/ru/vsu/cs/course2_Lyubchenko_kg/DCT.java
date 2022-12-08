package ru.vsu.cs.course2_Lyubchenko_kg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DCT {
    private static final int MATRIX_SIZE = 8;

    private static final int[][] QUANTIZATION_MATRIX= {{16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};

    public static BufferedImage transform(BufferedImage img){
        int w = img.getWidth();
        int h = img.getHeight();

        int[][] red = new int[w][h];
        int[][] green = new int[w][h];
        int[][] blue = new int[w][h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = img.getRGB(x,y);
                Color color = new Color(pixel, true);
                red[x][y] = color.getRed();
                green[x][y] = color.getGreen();
                blue[x][y] = color.getBlue();
            }
        }

        red = compressMatrix(red);
        green = compressMatrix(green);
        blue = compressMatrix(blue);

//        for (int y = 0; y < h; y++) {
//            for (int x = 0; x < w; x++) {
//                Color color = new Color(red[x][y], green[x][y], blue[x][y]);
//                img.setRGB(x, y, color.getRGB());
//            }
//        }

        return img;
    }

    public static int[][] compressMatrix(int[][] matrix){
        int[][] compressMatrix = new int[matrix.length][ matrix[0].length];

        int[][] tempMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                tempMatrix[i%8][j%8] = matrix[i][j];
                if (((i + 1) % MATRIX_SIZE) == 0 & ((j + 1) % MATRIX_SIZE) == 0) {
                    tempMatrix = dctTransform(tempMatrix);
                    for (int k = 0; k < MATRIX_SIZE; k++) {
                        for (int l = 0; l < MATRIX_SIZE; l++) {
                            if (i-MATRIX_SIZE+1+k<matrix.length & j-MATRIX_SIZE+1+l<matrix[0].length){
                                compressMatrix[i-MATRIX_SIZE+k+1][j-MATRIX_SIZE+l+1] = tempMatrix[k][l];
                            }
                        }
                    }
                    tempMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];;
                } else if (((j + 1) % MATRIX_SIZE) == 0 ){
                    break;
                }
            }
        }
        return compressMatrix;
    }

    public static int[][] dctTransform(int[][] matrix){
        int[][] dct = new int[MATRIX_SIZE][MATRIX_SIZE];

        for (int i = 0; i < MATRIX_SIZE; i++) {
            double ci;
            if (i == 0) {
                ci = 1 / Math.sqrt(MATRIX_SIZE);
            } else {
                ci = Math.sqrt(2) / Math.sqrt(MATRIX_SIZE);
            }

            for (int j = 0; j < MATRIX_SIZE; j++) {
                double cj;
                if (j == 0) {
                    cj = 1 / Math.sqrt(MATRIX_SIZE);
                }else {
                    cj = Math.sqrt(2) / Math.sqrt(MATRIX_SIZE);
                }

                double sum = 0;
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    for (int l = 0; l < MATRIX_SIZE; l++) {
                        sum+= matrix[k][l] *
                                Math.cos((2 * k + 1) * i * Math.PI / (2 * MATRIX_SIZE)) *
                                Math.cos((2 * l + 1) * j * Math.PI / (2 * MATRIX_SIZE));
                    }
                }
                dct[i][j] = (int) (ci * cj * sum);
                dct[i][j] = dct[i][j]/QUANTIZATION_MATRIX[i][j];
            }
        }

        return dct;
    }

    static void zigZagMatrix(int arr[][], int n, int m) {
        int row = 0, col = 0;

        boolean row_inc = false;

        int mn = Math.min(m, n);
        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {
                System.out.print(arr[row][col] + " ");

                if (i + 1 == len)
                    break;
                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    --row;
                    ++col;
                }
            }

            if (len == mn)
                break;

            if (row_inc) {
                ++row;
                row_inc = false;
            } else {
                ++col;
                row_inc = true;
            }
        }

        if (row == 0) {
            if (col == m - 1)
                ++row;
            else
                ++col;
            row_inc = true;
        } else {
            if (row == n - 1)
                ++col;
            else
                ++row;
            row_inc = false;
        }

        int MAX = Math.max(m, n) - 1;
        for (int len, diag = MAX; diag > 0; --diag) {

            if (diag > mn)
                len = mn;
            else
                len = diag;

            for (int i = 0; i < len; ++i) {
                System.out.print(arr[row][col] + " ");

                if (i + 1 == len)
                    break;

                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    ++col;
                    --row;
                }
            }

            if (row == 0 || col == m - 1) {
                if (col == m - 1)
                    ++row;
                else
                    ++col;

                row_inc = true;
            }

            else if (col == 0 || row == n - 1) {
                if (row == n - 1)
                    ++col;
                else
                    ++row;

                row_inc = false;
            }
        }
    }

    public static StringBuilder encoding(StringBuilder str) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] list = str.toString().split(" ");
        int n = list.length;
        for (int i = 0; i < n; i++) {
            int count = 1;
            while (i < n - 1 && list[i].equals(list[i + 1])) {
                count++;
                i++;
            }

            stringBuilder.append(list[i] + " " + count);
        }
        return stringBuilder.append("/n");
    }

    public static StringBuilder print2D(int mat[][]) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : mat) {
            for (int i : row) {
                stringBuilder.append(i + " ");
            }
            stringBuilder.append("/n");
        }
        return stringBuilder.append(" /n");
    }


//    static final int C = 3;
//
//    public static StringBuilder zigZagMatrix(int arr[][]) {
//        StringBuilder string = new StringBuilder();
//        int row = 0, col = 0;
//        boolean row_inc = false;
//
//        for (int len = 1; len <= MATRIX_SIZE; ++len) {
//            for (int i = 0; i < len; ++i) {
//                string.append(arr[row][col]).append(" ");
//
//                if (i + 1 == len)
//                    break;
//                if (row_inc) {
//                    ++row;
//                    --col;
//                } else {
//                    --row;
//                    ++col;
//                }
//            }
//
//            if (len == MATRIX_SIZE)
//                break;
//
//            if (row_inc) {
//                ++row;
//                row_inc = false;
//            } else {
//                ++col;
//                row_inc = true;
//            }
//        }
//
//        if (row == 0) {
//            if (col == MATRIX_SIZE - 1)
//                ++row;
//            else
//                ++col;
//            row_inc = true;
//        } else {
//            if (row == MATRIX_SIZE - 1)
//                ++col;
//            else
//                ++row;
//            row_inc = false;
//        }
//
//        int MAX = MATRIX_SIZE - 1;
//        for (int len, diag = MAX; diag > 0; --diag) {
//
//            if (diag > MATRIX_SIZE)
//                len = MATRIX_SIZE;
//            else
//                len = diag;
//
//            for (int i = 0; i < len; ++i) {
//                string.append(arr[row][col]).append(" ");
//
//                if (i + 1 == len)
//                    break;
//
//                if (row_inc) {
//                    ++row;
//                    --col;
//                } else {
//                    ++col;
//                    --row;
//                }
//            }
//
//            if (row == 0 || col == MATRIX_SIZE - 1) {
//                if (col == MATRIX_SIZE - 1)
//                    ++row;
//                else
//                    ++col;
//
//                row_inc = true;
//            }
//
//            else if (col == 0 || row == MATRIX_SIZE - 1) {
//                if (row == MATRIX_SIZE - 1)
//                    ++col;
//                else
//                    ++row;
//
//                row_inc = false;
//            }
//        }
//        return string;
//    }
}
