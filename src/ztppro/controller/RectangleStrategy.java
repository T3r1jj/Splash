package ztppro.controller;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

/**
 *
 * @author Damian Terlecki
 */
class RectangleStrategy extends ShapeStrategy {

    public RectangleStrategy(CanvasController controller) {
        super(controller);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        controller.getModel().restoreState(controller.getModel().getCurrentState());
        currentEvent = e;
        Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
        g2d.setColor(firstColor);
        g2d.drawRect((Math.min(e.getX(), lastEvent.getX()) - controller.getModel().getXOffset()) / controller.getModel().getZoom(),
                (Math.min(e.getY(), lastEvent.getY()) - controller.getModel().getYOffset()) / controller.getModel().getZoom(),
                Math.abs(lastEvent.getX() - e.getX()) / controller.getModel().getZoom(), Math.abs(lastEvent.getY() - e.getY()) / controller.getModel().getZoom());
        controller.repaintAllLayers();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastEvent = e;
        controller.getModel().setCurrentState(controller.getModel().createMemento());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastEvent = null;
        currentEvent = null;
        controller.undoHistory.add(controller.getModel().createMemento());
        controller.redoHistory.clear();
    }
}
