package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import entities.Body;
import entities.Entity;
import physics.Position;

/**
 * Class responsible for rendering the simulation.
 * 
 * @author Eddie Summers
 */
public class MyPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    /*
     * The reduction in scale factor applied to the contents of the image
     * rendered using the magnifier camera (following the mouse cursor).
     */
    private static double overlayZoomFactor;
    
    // The size in pixels of the magnifier overlay square
    private static final int MAGNIFIER_OVERLAY_SIZE = 250;

    private List<Entity> entities;
    private BufferedImage magnifiedImage;
    private Camera camera;
    private Camera magnifyCamera;

    public MyPanel(
            int width,
            int height,
            List<Entity> entities,
            Camera camera,
            double overlayZoomFactor) {
        
        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.entities = entities;
        this.camera = camera;
        this.overlayZoomFactor = overlayZoomFactor;

        this.magnifyCamera = 
                new Camera(new Position(0, 0), MAGNIFIER_OVERLAY_SIZE);
        
        this.magnifiedImage = new BufferedImage(
                MAGNIFIER_OVERLAY_SIZE, 
                MAGNIFIER_OVERLAY_SIZE, 
                BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Combine the colours of two Bodies in a weighted manner, using their
     * respective masses.
     * @param thisBody
     * @param otherBody
     * @return Color
     */
    public static Color mergeBodyColours(Body thisBody, Body otherBody) {

        float newR = (float)calculateWeightedColourElement(
                thisBody.getMass(),
                thisBody.getColour().getRed(),
                otherBody.getMass(),
                otherBody.getColour().getRed());
        float newG = (float) calculateWeightedColourElement(
                thisBody.getMass(),
                thisBody.getColour().getGreen(),
                otherBody.getMass(),
                otherBody.getColour().getGreen());
        float newB = (float) calculateWeightedColourElement(
                thisBody.getMass(),
                thisBody.getColour().getBlue(),
                otherBody.getMass(),
                otherBody.getColour().getBlue());

        return new Color((newR / 255), (newG / 255), (newB / 255));
    }

    /**
     * For a single element of the RGB Colors of each of two Bodies, calculate
     * the average when weighted according to their masses.
     * @param thisMass
     * @param thisElement
     * @param otherMass
     * @param otherElement
     * @return double
     */
    private static double calculateWeightedColourElement(
            double thisMass,
            double thisElement,
            double otherMass,
            double otherElement) {

        double totalMass = thisMass + otherMass;

        return (thisMass * thisElement + otherMass * otherElement) / totalMass;
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        // Draw the main simulation render onto the panel
        Graphics2D g2d = (Graphics2D) g;
        double scale = Simulation.getSizedScaleFactor();
        drawSimulation(g2d, scale, camera);
        
        /*
         * When the mouse is in the middle of the screen, the magnifier camera
         * should be focused on the same point as the main camera. We subtract
         * targetSize / 2 because we want the mouse position to be relative to 
         * the centre of the screen, NOT the top-left.
         */
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePos, this);

        // Move our magnifier camera to the mouse
        magnifyCamera.setFocus(new Position(
                (mousePos.x - camera.getTargetSize() / 2) * scale + 
                camera.getFocus().getX(),
                (mousePos.y - camera.getTargetSize() / 2) * scale + 
                camera.getFocus().getY()));
        
        if (Simulation.getIsDrawingOverlay()) {
            drawOverlay(
                    g2d, 
                    Simulation.getSizedScaleFactor(), 
                    magnifyCamera, 
                    mousePos);
        }
                
    }
    
    /**
     * Draw the magnified overlay image, centred at the mouse cursor.
     * @param g2d
     * @param scale
     * @param magnifyCamera
     * @param centre
     */
    private void drawOverlay(
            Graphics2D g2d, 
            double scale, 
            Camera magnifyCamera,
            Point centre) {
        
        // Draw the simulation using the scale reduction onto an overlay image
        Graphics2D imageG2D = magnifiedImage.createGraphics();
        imageG2D.clearRect(
                0, 0, MAGNIFIER_OVERLAY_SIZE, MAGNIFIER_OVERLAY_SIZE);
        drawSimulation(
                imageG2D, scale / overlayZoomFactor, magnifyCamera);
        
        // Draw the overlay image at the cursor
        g2d.drawImage(
                magnifiedImage,
                centre.x - MAGNIFIER_OVERLAY_SIZE / 2,
                centre.y - MAGNIFIER_OVERLAY_SIZE / 2,
                null);
        
        // Draw the border of the overlay box
        g2d.setColor(Color.WHITE);
        g2d.drawRect(
                centre.x - MAGNIFIER_OVERLAY_SIZE / 2, 
                centre.y - MAGNIFIER_OVERLAY_SIZE / 2, 
                MAGNIFIER_OVERLAY_SIZE, 
                MAGNIFIER_OVERLAY_SIZE);
    }
    
    /**
     * Draw all Entities in the simulation at some scale and with some Camera.
     * @param g2d
     * @param scale
     * @param camera
     */
    private void drawSimulation(Graphics2D g2d, double scale, Camera camera) {
        for (Entity entity : entities) {
            entity.draw(
                    g2d,
                    scale,
                    camera,
                    Simulation.getIsDrawingNameLabels());
        }
    }

    /**
     * Replace list of entities for rendering.
     * @param entities
     */
    public void updateEntityList(List<Entity> entities) {
        this.entities = entities;
    }

    public static void setOverlayZoomFactor(double overlayZoomFactor) {
        MyPanel.overlayZoomFactor = overlayZoomFactor;
    }
    
}
