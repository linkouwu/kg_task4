package ru.vsu.cs.course2_Lyubchenko_kg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

public class ImageUtils {
    public static byte[] toByteArray(BufferedImage bi, String format) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ImageIO.write(bi, format, byteArray);
        return byteArray.toByteArray();
    }

    public static BufferedImage toBufferedImage(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileFormat(File file){
        String format = "";
        String path = file.getPath();

        int i = path.lastIndexOf('.');
        if (i > 0) {
            format = path.substring(i+1);
        }
        return format;
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
