package org.example;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageAnnotation {

    public void addHighlight() throws IOException {
        try {
            // Load the original image
            BufferedImage originalImage = ImageIO.read(new File("BaseImage.jpeg"));

            // Create a new image in RGB color mode with the same size as the original
            BufferedImage rgbImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Draw the original image onto the new RGB image
            Graphics2D g2dRgb = rgbImage.createGraphics();
            g2dRgb.drawImage(originalImage, 0, 0, null);

            // Apply a highlight effect to a rectangular region on the RGB image
            Graphics2D g2dHighlight = rgbImage.createGraphics();
            g2dHighlight.setColor(new Color(88, 169, 79, 128)); // green highlight color with 50% opacity
            g2dHighlight.fillRect(350, 300, 100, 100);

            // Save the new RGB image with the highlight effect applied
            ImageIO.write(rgbImage, "jpg", new File("highlighted_rgb.jpg"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
