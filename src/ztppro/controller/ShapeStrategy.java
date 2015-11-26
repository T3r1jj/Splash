package ztppro.controller;

import java.awt.event.MouseEvent;

/**
 *
 * @author Damian Terlecki
 */
public abstract class ShapeStrategy extends DefaultDrawingStrategy {
    protected MouseEvent currentEvent;
    protected MouseEvent lastEvent;

    public ShapeStrategy(CanvasController controller) {
        super(controller);
    }

}
