package main;

import java.util.List;

import entities.Entity;

/**
 * Class representing a set of starting conditions for a Simulation to use.
 * 
 * @author Eddie Summers
 */
public class Scenario {
    
    private String name;
    private List<Entity> entities;
    private double timeAcceleration;
    private double overlayZoomFactor;
    private double initialScaleFactor;
    
    public Scenario(
            String name,
            List<Entity> entities,
            double timeAcceleration,
            double overlayZoomFactor,
            double initialScaleFactor) {

        this.name = name;
        this.entities = entities;
        this.timeAcceleration = timeAcceleration;
        this.overlayZoomFactor = overlayZoomFactor;
        this.initialScaleFactor = initialScaleFactor;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public double getTimeAcceleration() {
        return timeAcceleration;
    }

    public void setTimeAcceleration(double timeAcceleration) {
        this.timeAcceleration = timeAcceleration;
    }

    public double getOverlayZoomFactor() {
        return overlayZoomFactor;
    }

    public void setOverlayZoomFactor(double overlayZoomFactor) {
        this.overlayZoomFactor = overlayZoomFactor;
    }

    public double getInitialScaleFactor() {
        return initialScaleFactor;
    }

    public void setInitialScaleFactor(double initialScaleFactor) {
        this.initialScaleFactor = initialScaleFactor;
    }

}
