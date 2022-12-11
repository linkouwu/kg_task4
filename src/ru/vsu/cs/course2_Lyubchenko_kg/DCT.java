package ru.vsu.cs.course2_Lyubchenko_kg;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DCT {
    private static final int MATRIX_SIZE = 8;

    private static final int[][] QUANTIZATION_MATRIX = {{16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};

    public static BufferedImage transform(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        //YCrCb color space
        int[][] yColor = new int[w][h];
        int[][] cbColor = new int[w][h];
        int[][] crColor = new int[w][h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = img.getRGB(x, y);
                Color color = new Color(pixel, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                yColor[x][y] = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                cbColor[x][y] = (int) (128 - 0.169 * r - 0.331 * g + 0.500 * b);
                crColor[x][y] = (int) (128 + 0.500 * r - 0.419 * g - 0.081 * b);
            }
        }

        yColor = compressMatrix(yColor);
        cbColor = compressMatrix(cbColor);
        crColor = compressMatrix(crColor);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //RGB color space
                int Y = yColor[x][y];
                int Cb = cbColor[x][y];
                int Cr = crColor[x][y];

                int r = (int) (Y + 1.40200 * (Cr - 0x80));
                int g = (int) (Y - 0.34414 * (Cb - 0x80) - 0.71414 * (Cr - 0x80));
                int b = (int) (Y + 1.77200 * (Cb - 0x80));

                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                Color color = new Color(r, g, b);
                img.setRGB(x, y, color.getRGB());
            }
        }

        return img;
    }

    public static int[][] compressMatrix(int[][] matrix) {
        int[][] compressMatrix = new int[matrix.length][matrix[0].length];

        int[][] tempMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];
        for (int m = 0; m < matrix.length; m += 8) {
            for (int n = 0; n < matrix[0].length; n += 8) {
                for (int i = m; i < m + MATRIX_SIZE; i++) {
                    for (int j = n; j < n + MATRIX_SIZE; j++) {
                        if (i >= matrix.length || j >= matrix[0].length) {
                            break;
                        }

                        tempMatrix[i % MATRIX_SIZE][j % MATRIX_SIZE] = matrix[i][j];

                        if (((i + 1) % MATRIX_SIZE) == 0 & ((j + 1) % MATRIX_SIZE) == 0) {
                            tempMatrix = dctTransformInverse(dctTransform(tempMatrix));

                            for (int k = 0; k < MATRIX_SIZE; k++) {
                                for (int l = 0; l < MATRIX_SIZE; l++) {
                                    if (i - MATRIX_SIZE + 1 + k < matrix.length & j - MATRIX_SIZE + 1 + l < matrix[0].length) {
                                        compressMatrix[i - MATRIX_SIZE + k + 1][j - MATRIX_SIZE + l + 1] = tempMatrix[k][l];
                                    }
                                }
                            }

                            tempMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];
                        }
                    }
                }
            }
        }
        return compressMatrix;
    }

    public static int[][] dctTransform(int[][] matrix) {
        int[][] dct = new int[MATRIX_SIZE][MATRIX_SIZE];
        double k1 = 1 / Math.sqrt(2);
        double k2 = 1;

        for (int i = 0; i < MATRIX_SIZE; i++) {
            double ci;
            if (i == 0) {
                ci = k1;
            } else {
                ci = k2;
            }

            for (int j = 0; j < MATRIX_SIZE; j++) {
                double cj;
                if (j == 0) {
                    cj = k1;
                } else {
                    cj = k2;
                }

                double sum = 0;
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    for (int l = 0; l < MATRIX_SIZE; l++) {
                        sum += matrix[k][l] * ci * cj *
                                Math.cos((2 * k + 1) * i * Math.PI / (2 * MATRIX_SIZE)) *
                                Math.cos((2 * l + 1) * j * Math.PI / (2 * MATRIX_SIZE));
                    }
                }
                dct[i][j] = (int) ((1 / 4.0) * sum);
            }
        }

        return dct;
    }

    public static int[][] dctTransformInverse(int[][] matrix) {
        int[][] dct = new int[MATRIX_SIZE][MATRIX_SIZE];
        double k1 = 1 / Math.sqrt(2);
        double k2 = 1;

        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                double sum = 0;

                for (int k = 0; k < MATRIX_SIZE; k++) {
                    double ck;
                    if (k == 0) {
                        ck = k1;
                    } else {
                        ck = k2;
                    }

                    for (int l = 0; l < MATRIX_SIZE; l++) {
                        double cl;
                        if (l == 0) {
                            cl = k1;
                        } else {
                            cl = k2;
                        }

                        sum += ck * cl * matrix[k][l] *
                                Math.cos((2 * i + 1) * k * Math.PI / (2 * MATRIX_SIZE)) *
                                Math.cos((2 * j + 1) * l * Math.PI / (2 * MATRIX_SIZE));
                    }
                }
                dct[i][j] = (int) ((1 / 4.0) * sum);
            }
        }

        return dct;
    }

//    static void zigZagMatrix(int arr[][], int n, int m) {
//        int row = 0, col = 0;
//
//        boolean row_inc = false;
//
//        int mn = Math.min(m, n);
//        for (int len = 1; len <= mn; ++len) {
//            for (int i = 0; i < len; ++i) {
//                System.out.print(arr[row][col] + " ");
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
//            if (len == mn)
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
//            if (col == m - 1)
//                ++row;
//            else
//                ++col;
//            row_inc = true;
//        } else {
//            if (row == n - 1)
//                ++col;
//            else
//                ++row;
//            row_inc = false;
//        }
//
//        int MAX = Math.max(m, n) - 1;
//        for (int len, diag = MAX; diag > 0; --diag) {
//
//            if (diag > mn)
//                len = mn;
//            else
//                len = diag;
//
//            for (int i = 0; i < len; ++i) {
//                System.out.print(arr[row][col] + " ");
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
//            if (row == 0 || col == m - 1) {
//                if (col == m - 1)
//                    ++row;
//                else
//                    ++col;
//
//                row_inc = true;
//            } else if (col == 0 || row == n - 1) {
//                if (row == n - 1)
//                    ++col;
//                else
//                    ++row;
//
//                row_inc = false;
//            }
//        }
//    }
//
//    public static StringBuilder encoding(StringBuilder str) {
//        StringBuilder stringBuilder = new StringBuilder();
//        String[] list = str.toString().split(" ");
//        int n = list.length;
//        for (int i = 0; i < n; i++) {
//            int count = 1;
//            while (i < n - 1 && list[i].equals(list[i + 1])) {
//                count++;
//                i++;
//            }
//
//            stringBuilder.append(list[i] + " " + count);
//        }
//        return stringBuilder.append("/n");
//    }
//
//    public static StringBuilder print2D(int mat[][]) {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int[] row : mat) {
//            for (int i : row) {
//                stringBuilder.append(i + " ");
//            }
//            stringBuilder.append("/n");
//        }
//        return stringBuilder.append(" /n");
//    }


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
