package ztppro.controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.event.InternalFrameEvent;
import ztppro.controller.CanvasController;
import ztppro.model.LayersModel;
import ztppro.model.ImageModel;
import ztppro.util.io.exception.UnsupportedExtension;
import ztppro.view.menu.Menu;
import ztppro.view.MyInternalFrame;
import ztppro.view.View;

/**
 *
 * @author Damian Terlecki
 */
public interface Controller extends MouseMotionListener, MouseListener, Observer {

    public void addToDesktop(MyInternalFrame frame);

    public void setView(View view);

    public void setModel(ImageModel model);

    public View getView();

    public ImageModel getModel();

    public void choosePencil();

    public void choosePaintbrush();

    public void chooseLine();

    public void chooseColor(Color color);

    public void chooseErase();

    public void chooseOval();

    public void chooseFilling();

    public void chooseRectangle();

    public void chooseSelect();

    public void addCanvasController(Controller canvasController);

    public boolean undo();

    public boolean redo();

    public boolean copy();

    public boolean paste();

    public void loseFocus();

    public void internalFrameActivated(InternalFrameEvent e, Menu menu, ImageModel model, JComponent caller);

    public void setLayersModel(LayersModel layersModel);

    public void addChildController(CanvasController controller);

    public void setParent(Controller controller);

    public void setChild(Controller controller);

    public Controller getChild();

    public Controller getParent();

    public void repaintLayers(Graphics g);

    public void swapChainTowardsTop();

    public void swapChainTowardsBottom();

    public void chooseMove();

    public void repaintAllLayers();

    public void chooseColorPicker();

    public void chooseText();

    public void chooseZoom();

    public void chooseTriangle();

    public void chooseSpray();

    public void chooseRoundedRectangle();

    public void saveToFile(File chosenFile, String extension) throws IOException, UnsupportedExtension;

    public LayersModel getLayersModel();

    public void openFile(File chosenFile) throws IOException, ClassNotFoundException, UnsupportedExtension;

    public void invert(boolean layer);

    public void rotate(double angle, boolean layer);

    public void changeBrightness(double percentage, boolean layer);

    public void changeContrast(double value, boolean layer);

    public void blur(boolean layer);

    public void autoWhiteBalance(boolean layer);

    public void sharpen(boolean layer);

    public void setDrawingSize(int size);

    public void chooseBrokenLine();

    public void disposeLayer(ImageModel deletion);

    public void mergeDown(ImageModel merge);

}
