package ru.vsu.cs.course2_Lyubchenko_kg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Window extends JFrame {
    private JButton loadButton;
    private JButton compressButton;
    private JButton saveButton;
    private JPanel originalImageJPanel;
    private JPanel compressImageJPanel;
    private JPanel panel1;

    BufferedImage compressImage;

    public Window() {
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                int result = fileChooser.showOpenDialog(Window.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ((ImagePanel) originalImageJPanel).setFile(selectedFile);
                    originalImageJPanel.repaint();
                }
            }
        });
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage originalImage = ((ImagePanel) originalImageJPanel).getImage();
                compressImage = DCT.transform(ImageUtils.deepCopy(originalImage));
                ((ImagePanel) compressImageJPanel).setImage(compressImage);
                compressImageJPanel.repaint();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File outputFile = new File("image.jpg");
                try {
                    ImageIO.write(compressImage, "jpg", outputFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void createUIComponents() {
        originalImageJPanel = new ImagePanel();
        compressImageJPanel = new ImagePanel();
    }
}
