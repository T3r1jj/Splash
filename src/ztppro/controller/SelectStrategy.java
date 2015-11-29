package ztppro.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author Damian Terlecki
 */
public class SelectStrategy extends AbstractDrawingStrategy {

    private Rectangle2D rectangle;
    private Rectangle2D handleRectangle;
    private Point deltaSelection;
    private BufferedImage selection;
    private MouseEvent lastEvent;
    private MouseEvent currentEvent;

    public SelectStrategy(CanvasController controller) {
        super(controller);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastEvent != null) {
            Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
            controller.getModel().restoreState(controller.getModel().getCurrentState());
            if (currentEvent == null) {
                g2d.setColor(firstColor);
                rectangle = new Rectangle((Math.min(e.getX(), lastEvent.getX()) - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                        (Math.min(e.getY(), lastEvent.getY()) - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(),
                        Math.abs(lastEvent.getX() - e.getX()) / controller.getModel().getZoom(), Math.abs(lastEvent.getY() - e.getY()) / controller.getModel().getZoom());
                g2d.draw(rectangle);
                drawHighlightSquares((Graphics2D) controller.getModel().getImage().getGraphics(), rectangle);
            } else {
                g2d.setColor(secondColor);
                g2d.fill(rectangle);
                g2d.drawImage(selection, (e.getX() - deltaSelection.x - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                        (e.getY() - deltaSelection.y - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), null);
                handleRectangle.setRect((e.getX() - deltaSelection.x - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                        (e.getY() - deltaSelection.y - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), handleRectangle.getWidth(), handleRectangle.getHeight());
                drawHighlightSquares((Graphics2D) controller.getModel().getImage().getGraphics(), handleRectangle);
            }
            g2d.dispose();
            controller.repaintAllLayers();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            controller.getModel().restoreState(controller.getModel().getCurrentState());
            currentEvent = null;
            lastEvent = null;
            controller.repaintAllLayers();
        } else if (lastEvent == null) {
            lastEvent = e;
            controller.getModel().setCurrentState(controller.getModel().createMemento());
        } else if (rectangle.contains((e.getPoint().getX() - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                (e.getPoint().getY() - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom()) && rectangle != null) {
            controller.getModel().restoreState(controller.getModel().getCurrentState());
            selection = deepCopy(controller.getModel().getImage()).getSubimage((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
            handleRectangle = rectangle.getBounds2D();
            drawHighlightSquares((Graphics2D) controller.getModel().getImage().getGraphics(), handleRectangle);
            deltaSelection = new Point((Math.abs((int) rectangle.getX() - e.getX()) - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                    (Math.abs((int) rectangle.getY() - e.getY()) - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom());
        } else {
            lastEvent = e;
            currentEvent = null;
            controller.getModel().restoreState(controller.getModel().getCurrentState());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) {
            if (currentEvent != null) {
                Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
                controller.getModel().restoreState(controller.getModel().getCurrentState());
                g2d.setColor(secondColor);
                g2d.fill(rectangle);
                g2d.drawImage(selection, (e.getX() - deltaSelection.x - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                        (e.getY() - deltaSelection.y - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), null);
                controller.getModel().setCurrentState(controller.getModel().createMemento());
                controller.undoHistory.add(controller.getModel().createMemento());
                controller.redoHistory.clear();
                rectangle.setRect((e.getX() - deltaSelection.x - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                        (e.getY() - deltaSelection.y - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), handleRectangle.getWidth(), handleRectangle.getHeight());
                handleRectangle.setRect((e.getX() - deltaSelection.x - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                        (e.getY() - deltaSelection.y - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom(), handleRectangle.getWidth(), handleRectangle.getHeight());
                drawHighlightSquares((Graphics2D) controller.getModel().getImage().getGraphics(), handleRectangle);
                g2d.dispose();
            }
            currentEvent = e;
        }
    }

    public void drawHighlightSquares(Graphics2D g2D, Rectangle2D r) {
        double x = r.getX();
        double y = r.getY();
        double w = r.getWidth();
        double h = r.getHeight();
        g2D.setColor(Color.black);

        g2D.fill(new Rectangle.Double(x - 3.0, y - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x + w - 3.0, y - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x - 3.0, y + h * 0.5 - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x + w - 3.0, y + h * 0.5 - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x - 3.0, y + h - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y + h - 3.0, 6.0, 6.0));
        g2D.fill(new Rectangle.Double(x + w - 3.0, y + h - 3.0, 6.0, 6.0));
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    @Override
    public void paste() {
        controller.getModel().restoreState(controller.getModel().getCurrentState());
        Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
        Image clipboardImage = getClipboardImage();
        g2d.drawImage(getClipboardImage(), 0, 0, null);
        rectangle.setRect(0, 0, clipboardImage.getWidth(null), clipboardImage.getHeight(null));
        controller.getModel().setCurrentState(controller.getModel().createMemento());
        controller.undoHistory.add(controller.getModel().createMemento());
        controller.redoHistory.clear();
        handleRectangle = new Rectangle(0, 0, clipboardImage.getWidth(null), clipboardImage.getHeight(null));
        drawHighlightSquares((Graphics2D) controller.getModel().getImage().getGraphics(), handleRectangle);
        g2d.dispose();
        controller.repaintAllLayers();
    }

    @Override
    public void copy() {
        if (selection == null && rectangle != null) {
            controller.getModel().restoreState(controller.getModel().getCurrentState());
            selection = deepCopy(controller.getModel().getImage()).getSubimage((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
        }
        setClipboard(selection);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Get an image off the system clipboard.
     *
     * @return Returns an Image if successful; otherwise returns null.
     */
    protected Image getClipboardImage() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                return (Image) transferable.getTransferData(DataFlavor.imageFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                Logger.getLogger(SelectStrategy.class.getName()).fine(e.toString());
            }
        } else {
            Logger.getLogger(SelectStrategy.class.getName()).fine("Clipboard: not an image!");
        }
        return null;
    }

    // code below from exampledepot.com
    protected static void setClipboard(Image image) {
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    protected static class ImageSelection implements Transferable {

        private Image image;

        public ImageSelection() {
        }

        public ImageSelection(Image image) {
            this.image = image;
        }

        public Image getImage() {
            return image;
        }

        // Returns supported flavors
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }

}
