package ztppro.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.util.Observable;
import ztppro.view.Memento;

/**
 *
 * @author Damian Terlecki
 */
public class ModelImage extends Observable implements Model {

    BufferedImage image;
    Memento currentState;

    public ModelImage(int width, int height, int imageType) {
        image = new BufferedImage(width, height, imageType);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    public void restoreState(Memento memento) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(((CanvasMemento) currentState).getState(), 0, pixels, 0, pixels.length);
    }

    public Memento createMemento() {
        return new CanvasMemento().setState(((DataBufferInt) image.getRaster().getDataBuffer()).getData().clone());
    }

    private class CanvasMemento implements Memento {

        private int[] pixels;

        public CanvasMemento() {
        }

        public Memento setState(int[] pixels) {
            this.pixels = pixels;
            return this;
        }

        public int[] getState() {
            return pixels;
        }

    }

    public Memento getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Memento currentState) {
        this.currentState = currentState;
    }

}