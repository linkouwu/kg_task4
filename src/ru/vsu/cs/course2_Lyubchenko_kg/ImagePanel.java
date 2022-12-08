package ru.vsu.cs.course2_Lyubchenko_kg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImagePanel extends JPanel {
    private BufferedImage bi;
    private File file;

    public ImagePanel() {
    }

    public void setFile(File selectedFile){
        try {
            this.bi = ImageIO.read(selectedFile);
            this.file = selectedFile;
        } catch (IOException ex) {
            Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bi!=null) {
            Graphics g2 = g.create();
            int preferWidth = bi.getWidth();
            int preferHeight = bi.getHeight();
            double padding = 1.25;
            if (bi.getWidth()>getWidth()/padding || bi.getHeight()> getHeight()/padding){
                double k = Math.max(getWidth()/(double)bi.getWidth(), getHeight()/(double)bi.getHeight());
                preferWidth= (int) (preferWidth*k/padding);
                preferHeight= (int) (preferHeight*k/padding);
            }
            g2.drawImage(bi, 0, 0, preferWidth, preferHeight, null);
            g2.dispose();
        }
    }

    public BufferedImage getImage() {
        return bi;
    }

    public void setImage(BufferedImage bi) {
        this.bi = bi;
    }

    public File getFile() {
        return file;
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(bi.getWidth() / 2, bi.getHeight() / 2);
//    }
}
