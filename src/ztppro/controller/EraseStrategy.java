package ztppro.controller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Damian Terlecki
 */
class EraseStrategy extends BrushStrategy {

    protected EraseShape shapeType;
    protected Shape shape;
    private Cursor defaultCursor;

    public EraseStrategy(CanvasController controller, EraseShape shapeType) {
        super(controller);
        this.shapeType = shapeType;
        if (controller != null) {
            controller.getModel().setCurrentState(controller.getModel().createMemento());
            defaultCursor = controller.getView().getCursor();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentEvent = e;
        if (shapeType.equals(EraseShape.ROUND)) {
            shape = new Ellipse2D.Double((e.getX() - size - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                    (e.getY() - size - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), 2 * size, 2 * size);
        } else if (shapeType.equals(EraseShape.SQUARE)) {
            shape = new Rectangle((e.getX() - size - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                    (e.getY() - size - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), 2 * size, 2 * size);
        }
        Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
        g2d.setColor(secondColor);
        g2d.fill(shape);
        mouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        lastEvent = currentEvent;
        currentEvent = e;
        if (lastEvent != null && currentEvent != null) {
            Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
            g2d.setColor(secondColor);
            for (Point2D point : new Line2DAdapter(lastEvent.getX(), lastEvent.getY(), currentEvent.getX(), currentEvent.getY())) {
                if (shapeType.equals(EraseShape.ROUND)) {
                    shape = new Ellipse2D.Double((point.getX() - size - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                            (point.getY() - size - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), 2 * size, 2 * size);
                } else if (shapeType.equals(EraseShape.SQUARE)) {
                    shape = new Rectangle(((int) point.getX() - size - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                            ((int) point.getY() - size - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), 2 * size, 2 * size);
                }
                g2d.fill(shape);
            }
            mouseMoved(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        controller.getModel().setCurrentState(controller.getModel().createMemento());
        super.mouseMoved(e);
        if (shapeType.equals(EraseShape.ROUND)) {
            shape = new Ellipse2D.Double((e.getX() - size - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                    (e.getY() - size - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), 2 * size, 2 * size);
        } else if (shapeType.equals(EraseShape.SQUARE)) {
            shape = new Rectangle((e.getX() - size - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                    (e.getY() - size - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), 2 * size, 2 * size);
        }
        Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
        g2d.setColor(secondColor);
        g2d.fill(shape);
        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
        g2d.dispose();
        controller.repaintAllLayers();
        controller.getModel().restoreState(controller.getModel().getCurrentState());

    }

    @Override
    public void mouseExited(MouseEvent e) {
        controller.getView().setCursor(defaultCursor);
        controller.repaintAllLayers();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");    //cant use custom cursor due to windows default resize to 32x32
        controller.getView().setCursor(blankCursor);
        controller.repaintAllLayers();
    }

    enum EraseShape {

        ROUND, SQUARE
    }

}
