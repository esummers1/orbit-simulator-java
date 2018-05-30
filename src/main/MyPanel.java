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

import entities.Entity;
import physics.Position;

/**
 * Class responsible for rendering the simulation.
 * 
 * @author Eddie Summers
 */
public class MyPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;

    private List<Entity> entities;
    
    private BufferedImage magnifiedImage = new BufferedImage(200, 200,
            BufferedImage.TYPE_INT_ARGB);

    private Camera camera;
    private Camera magnifyCamera;

    public MyPanel(int width, int height, List<Entity> entities, Camera camera) {
        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.entities = entities;
        this.camera = camera;
        this.magnifyCamera = new Camera(new Position(0, 0), 200);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        // Draw the simulation onto the panel
        Graphics2D g2d = (Graphics2D) g;
        double scale = Simulation.getSizedScaleFactor();
        drawSimulation(g2d, scale, camera);
        
        // Move our magnifier camera to the mouse
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePos, this);
        
        // When the mouse is in the middle of the screen, it should be focused
        // on our barycentre
        // Subtract targetSize / 2 because we want the mouse position to be
        // relative to the centre of the screen, NOT the top-left
        magnifyCamera.setFocus(new Position(
                (mousePos.x - camera.getTargetSize() / 2) * scale + camera.getFocus().getX(),
                (mousePos.y - camera.getTargetSize() / 2) * scale + camera.getFocus().getY()));
        
        // Draw the simulation at double scale onto an image
        Graphics2D imgGfx = magnifiedImage.createGraphics();
        imgGfx.clearRect(0, 0, 200, 200);
        drawSimulation(imgGfx, scale / 2, magnifyCamera);
        
        // Draw the image at the cursor
        g2d.drawImage(magnifiedImage, mousePos.x - 100, mousePos.y - 100, null);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(mousePos.x - 100, mousePos.y - 100, 200, 200);
    }
    
    private void drawSimulation(Graphics2D g2d, double scale, Camera camera) {
        for (Entity entity : entities) {
            entity.draw(g2d, scale, camera);
        }
    }

    /**
     * Replace list of entities for rendering.
     * @param entities
     */
    public void updateEntityList(List<Entity> entities) {
        this.entities = entities;
    }
}