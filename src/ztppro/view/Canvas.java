package ztppro.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Observable;
import javax.swing.JPanel;
import ztppro.controller.CanvasController;
import ztppro.controller.Controller;
import ztppro.controller.DrawingStrategyCache;
import ztppro.model.ImageModel;

/**
 *
 * @author Damian Terlecki
 */
public class Canvas extends JPanel implements View {

    private int width;
    private int height;
    private ImageModel model;
    private CanvasController canvasController;
    private Controller mainController;

    public Canvas(Controller controller, int width, int height, boolean layer, DrawingStrategyCache cache) {
        this.mainController = controller;
        this.setBackground(Color.white);
        this.model = new ImageModel(width, height, BufferedImage.TYPE_INT_ARGB, layer);
        canvasController = new CanvasController(this, this.model, cache);
        controller.setModel(this.model);
        this.model.addObserver(this);
        if (!layer) {
            controller.addCanvasController(canvasController);
        } else {
            this.setOpaque(false);
            controller.addChildController(canvasController);
        }
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.addMouseMotionListener(canvasController);
        this.addMouseListener(canvasController);
        this.setFocusable(true);
        repaint();
    }

    public ImageModel getModel() {
        return model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting level: " + model.getLayerNumber());
        g.drawImage(model.getImage(), 0, 0, null);
        canvasController.repaintLayers(g, model.getLayerNumber());
        if (model.hasFocus()) {
            drawDashedLine(g, 0, 0, this.width, this.height);
        }
    }

    @Override
    public Graphics paintLayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        System.out.println("Painting level: " + model.getLayerNumber());

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2d.drawImage(model.getImage(), 0, 0, null);
        canvasController.repaintLayers(g, model.getLayerNumber());
        if (model.hasFocus()) {
            drawDashedLine(g, 0, 0, this.width, this.height);
        }
        return g;
    }

    private void drawDashedLine(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.gray);
        float dash1[] = {10.0f};
        BasicStroke dashed = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
        g2.setStroke(dashed);
        g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, 1, 1));
    }

    @Override
    public String toString() {
        return "Canvas{width=" + width
                + ", height=" + height + '}';
    }

    @Override
    public void addToDesktop(MyInternalFrame frame) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null) {
            paintImmediately(0, 0, width, height);
        }
    }

    @Override
    public boolean hasFocus() {
        return super.getParent().getParent().getParent().getParent().hasFocus();
    }

    public Controller getController() {
        return canvasController;
    }
}
