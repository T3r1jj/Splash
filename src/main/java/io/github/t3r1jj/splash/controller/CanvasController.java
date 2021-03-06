/* 
 * Copyright 2016 Damian Terlecki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.t3r1jj.splash.controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;

import io.github.t3r1jj.splash.controller.drawing.*;
import io.github.t3r1jj.splash.controller.drawing.shape.*;
import io.github.t3r1jj.splash.model.*;
import io.github.t3r1jj.splash.model.imagefilter.*;
import io.github.t3r1jj.splash.util.ImageUtil;
import io.github.t3r1jj.splash.util.io.*;
import io.github.t3r1jj.splash.util.io.exception.UnsupportedExtension;
import io.github.t3r1jj.splash.view.*;
import io.github.t3r1jj.splash.view.menu.Menu;

public class CanvasController implements Controller {

    private View view;
    private ImageModel model;
    private Controller parent;
    private DrawingStrategy drawingStrategy;
    private Controller childCanvasController;
    private final DrawingStrategyCache cache;
    private final LinkedList<Memento> undoHistory = new LinkedList<>();
    private final LinkedList<Memento> redoHistory = new LinkedList<>();

    @Override
    public void setParent(Controller parent) {
        this.parent = parent;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public ImageModel getModel() {
        return model;
    }

    public CanvasController(View canvas, ImageModel model, DrawingStrategyCache cache) {
        this.view = canvas;
        this.model = model;
        this.cache = cache;
        this.model.addObserver(this);
        undoHistory.add(model.createMemento());
        drawingStrategy = cache.getDrawingStrategy();
        drawingStrategy.setController(this);
    }

    @Override
    public void setModel(ImageModel model) {
        this.model = model;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (model.hasFocus()) {
            drawingStrategy.mouseDragged(e);
        } else if (childCanvasController != null) {
            childCanvasController.mouseDragged(e);
        }
        if (parent instanceof MainController) {
            model.setCurrentMousePoint(e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (model.hasFocus()) {
            drawingStrategy.mouseMoved(e);
        } else if (childCanvasController != null) {
            childCanvasController.mouseMoved(e);
        }
        if (parent instanceof MainController) {
            model.setCurrentMousePoint(e.getPoint());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (model.hasFocus()) {
            drawingStrategy.mousePressed(e);
        } else if (childCanvasController != null) {
            childCanvasController.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (model.hasFocus()) {
            drawingStrategy.mouseReleased(e);
        } else if (childCanvasController != null) {
            childCanvasController.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (model.hasFocus()) {
            drawingStrategy.mouseEntered(e);
        } else if (childCanvasController != null) {
            childCanvasController.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (model.hasFocus()) {
            drawingStrategy.mouseExited(e);
        } else if (childCanvasController != null) {
            childCanvasController.mouseExited(e);
        }
        if (parent instanceof MainController) {
            model.setCurrentMousePoint(new Point(-1, -1));
        }
    }

    @Override
    public void choosePencil() {
        drawingStrategy = new PencilStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.choosePencil();
        }
    }

    @Override
    public void choosePaintbrush() {
        drawingStrategy = new BrushStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.choosePaintbrush();
        }

    }

    @Override
    public void chooseSpray() {
        drawingStrategy = new SprayStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseSpray();
        }

    }

    @Override
    public void chooseLine() {
        drawingStrategy = new LineStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseLine();
        }
    }

    @Override
    public void chooseBrokenLine() {
        drawingStrategy = new BrokenLineStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseBrokenLine();
        }
    }

    @Override
    public void chooseForegroundColor(Color color) {
        drawingStrategy.setFirstColor(color);
    }

    @Override
    public void chooseBackgroundColor(Color color) {
        drawingStrategy.setSecondColor(color);
    }

    @Override
    public void chooseOval() {
        drawingStrategy = new OvalStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseOval();
        }
    }

    @Override
    public void chooseFilling() {
        drawingStrategy = new ColorFillStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseFilling();
        }
    }

    @Override
    public void chooseRectangle() {
        drawingStrategy = new RectangleStrategy(this, RectangleStrategy.RectangleShape.NORMAL);
        if (childCanvasController != null) {
            childCanvasController.chooseRectangle();
        }
    }

    @Override
    public void chooseRoundedRectangle() {
        drawingStrategy = new RectangleStrategy(this, RectangleStrategy.RectangleShape.ROUNDED);
        if (childCanvasController != null) {
            childCanvasController.chooseRoundedRectangle();
        }
    }

    @Override
    public void chooseTriangle() {
        drawingStrategy = new TriangleStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseTriangle();
        }
    }

    @Override
    public void chooseSelect(boolean transparent) {
        drawingStrategy = new SelectStrategy(this, transparent);
        if (childCanvasController != null) {
            childCanvasController.chooseSelect(transparent);
        }
    }

    @Override
    public void chooseErase() {
        drawingStrategy = new EraseStrategy(this, EraseStrategy.EraseShape.SQUARE);
        if (childCanvasController != null) {
            childCanvasController.chooseErase();
        }
    }

    @Override
    public void chooseMove() {
        drawingStrategy = new MoveStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseMove();
        }
    }

    @Override
    public void chooseColorPicker() {
        drawingStrategy = new ColorPickerStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseColorPicker();
        }
    }

    @Override
    public void chooseText() {
        drawingStrategy = new TextStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseText();
        }
    }

    @Override
    public void chooseZoom() {
        drawingStrategy = new ZoomStrategy(this);
        if (childCanvasController != null) {
            childCanvasController.chooseZoom();
        }
    }

    @Override
    public void addCanvasController(Controller canvasController) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean undo() {
        if (view.hasFocus()) {
            if (model.hasFocus()) {
                if (undoHistory.size() > 1) {
                    redoHistory.add(undoHistory.removeLast());
                    model.restoreState(undoHistory.getLast());
                    repaintAllLayers();
                }
                return true;
            } else if (childCanvasController != null) {
                return childCanvasController.undo();
            }
        }
        return false;
    }

    @Override
    public boolean redo() {
        if (view.hasFocus()) {
            if (model.hasFocus()) {
                if (!redoHistory.isEmpty()) {
                    model.restoreState(redoHistory.getLast());
                    undoHistory.add(redoHistory.removeLast());
                    repaintAllLayers();
                }
                return true;
            }
        } else if (childCanvasController != null) {
            return childCanvasController.redo();
        }
        return false;
    }

    @Override
    public boolean copy() {
        if (view.hasFocus()) {
            if (model.hasFocus()) {
                drawingStrategy.copy();
                return true;
            }
        } else if (childCanvasController != null) {
            return childCanvasController.copy();
        }
        return false;
    }

    @Override
    public boolean paste() {
        if (view.hasFocus()) {
            if (model.hasFocus()) {
                drawingStrategy.paste();
                return true;
            }
        } else if (childCanvasController != null) {
            return childCanvasController.paste();
        }
        return false;
    }

    @Override
    public void loseFocus() {
        model.setFocus(false);
    }

    @Override
    public void frameActivated(JFrame frame, Menu menu, ImageModel topModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLayersModel(LayersModel layersModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addChildController(CanvasController controller) {
        if (view.hasFocus()) {
            model.setFocus(false);
            if (childCanvasController == null) {
                controller.setParent(this);
                childCanvasController = controller;
                controller.getModel().setFocus(true);
                view.add((Component) controller.getView());
            } else {
                childCanvasController.addChildController(controller);
            }
        }
    }

    @Override
    public void repaintLayers(Graphics g) {
        if (childCanvasController != null) {
            childCanvasController.getView().paintLayer(g);
            childCanvasController.repaintLayers(g);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Integer) {
            int currentModelLayer = (int) arg;
            if (childCanvasController != null) {
                while (childCanvasController.getModel().getLayerNumber() <= currentModelLayer) {
                    swapChainTowardsBottom();
                    if (childCanvasController == null) {
                        break;
                    }
                }
            }
            if (parent != null && !(parent instanceof MainController)) {
                while (parent.getModel().getLayerNumber() >= currentModelLayer) {
                    swapChainTowardsTop();
                    if (parent instanceof MainController) {
                        break;
                    }
                }
            }
            repaintAllLayers();
        }
    }

    private void connectParentWithGranddchild() {
        parent.setChild(childCanvasController);
        if (childCanvasController != null) {
            childCanvasController.setParent(parent);
        }
    }

    @Override
    public void swapChainTowardsTop() {
        Controller parentsParent = parent.getParent();
        parentsParent.setChild(this);
        if (childCanvasController != null) {
            childCanvasController.setParent(parent);
        }
        parent.setChild(childCanvasController);
        childCanvasController = parent;
        parent.setParent(this);
        parent = parentsParent;
    }

    @Override
    public void swapChainTowardsBottom() {
        Controller childsChild = childCanvasController.getChild();
        if (childsChild != null) {
            childsChild.setParent(this);
        }
        childCanvasController.setChild(this);
        childCanvasController.setParent(parent);
        parent.setChild(childCanvasController);
        parent = childCanvasController;
        childCanvasController = childsChild;
    }

    @Override
    public void setChild(Controller controller) {
        childCanvasController = controller;
    }

    @Override
    public Controller getChild() {
        return childCanvasController;
    }

    @Override
    public Controller getParent() {
        return parent;
    }

    @Override
    public void repaintAllLayers() {
        if (parent instanceof MainController) {
            view.paintImmediately(0, 0, model.getWidth(), model.getHeight());
        } else if (parent != null) {
            parent.repaintAllLayers();
        }
    }

    @Override
    public void saveToFile(File file, String extension) throws IOException, UnsupportedExtension {
        FileSaver saveStrategy = new FileSaverFactory(this).createFileSaver(extension);
        saveStrategy.save(file);
    }

    @Override
    public LayersModel getLayersModel() {
        return parent.getLayersModel();
    }

    @Override
    public void openFile(File chosenFile) throws IOException, ClassNotFoundException, UnsupportedExtension {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void invert(boolean layer) {
        if (!layer || (layer && model.hasFocus())) {
            new InvertionFilter().processImage(model);
        }
        if (childCanvasController != null) {
            childCanvasController.invert(layer);
        }
        if (parent instanceof MainController) {
            repaintAllLayers();
        }
    }

    @Override
    public void rotate(double angle, boolean layer) {
        if (!layer || (layer && model.hasFocus())) {
            new RotationFilter(angle).processImage(model);
            addCurrentStateToHistory();
        }
        if (childCanvasController != null) {
            childCanvasController.rotate(angle, layer);
        }
        if (parent instanceof MainController) {
            repaintAllLayers();
        }
    }

    @Override
    public void changeBrightnessContrast(double brightessPercentage, double contrastPercentage, boolean layer) {
        if (!layer || (layer && model.hasFocus())) {
            if (brightessPercentage != 0) {
                new BrightnessFilter(brightessPercentage).processImage(model);
            }
            if (contrastPercentage != 0) {
                new ContrastFilter(contrastPercentage).processImage(model);
            }
            addCurrentStateToHistory();
        }
        if (childCanvasController != null) {
            childCanvasController.changeBrightnessContrast(brightessPercentage, contrastPercentage, layer);
        }
        if (parent instanceof MainController) {
            repaintAllLayers();
        }
    }

    @Override
    public void blur(boolean layer) {
        if (!layer || (layer && model.hasFocus())) {
            new BlurFilter().processImage(model);
            addCurrentStateToHistory();
        }
        if (childCanvasController != null) {
            childCanvasController.blur(layer);
        }
        if (parent instanceof MainController) {
            repaintAllLayers();
        }
    }

    @Override
    public void autoWhiteBalance(boolean layer) {
        if (!layer || (layer && model.hasFocus())) {
            new WhiteBalanceFilter().processImage(model);
            addCurrentStateToHistory();
        }
        if (childCanvasController != null) {
            childCanvasController.autoWhiteBalance(layer);
        }
        if (parent instanceof MainController) {
            repaintAllLayers();
        }
    }

    @Override
    public void sharpen(boolean layer) {
        if (!layer || (layer && model.hasFocus())) {
            new SharpnessFilter().processImage(model);
            addCurrentStateToHistory();
        }
        if (childCanvasController != null) {
            childCanvasController.sharpen(layer);
        }
        if (parent instanceof MainController) {
            repaintAllLayers();
        }
    }

    @Override
    public void setDrawingSize(int size) {
        drawingStrategy.setSize(size);
    }

    @Override
    public void disposeLayer(ImageModel deletion) {
        if (deletion.equals(model)) {
            model.deleteObservers();
            detachView();
            connectParentWithGranddchild();
            parent.repaintAllLayers();
        } else if (childCanvasController != null) {
            childCanvasController.disposeLayer(deletion);
        }
    }

    @Override
    public void mergeDown(ImageModel merge) {
        if (merge.equals(model)) {
            model.deleteObservers();
            detachView();
            connectParentWithGranddchild();
            Graphics2D g2d = (Graphics2D) parent.getModel().getImage().getGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, model.getOpacity()));
            g2d.drawImage(model.getImage(), model.getXOffset(), model.getYOffset(), null);
            g2d.dispose();
            parent.repaintAllLayers();
        } else if (childCanvasController != null) {
            childCanvasController.mergeDown(merge);
        }
    }

    private void detachView() {
        Component component = (Component) view;
        while (!(component instanceof JLayeredPane)) {
            component = component.getParent();
        }
        ((JLayeredPane) component).remove((Component) view);
    }

    public void addCurrentStateToHistory() {
        try {
            undoHistory.add(model.createMemento());
            redoHistory.clear();
        } catch (java.lang.OutOfMemoryError ex) {
            if (undoHistory.isEmpty()) {
                return;
            }
            undoHistory.remove();
            addCurrentStateToHistory();
        }
    }

    @Override
    public void setViewCursor(Cursor cursor) {
        if (parent instanceof MainController) {
            view.setCursor(cursor);
        } else {
            view.setCursor(cursor);
            parent.setViewCursor(cursor);
        }
    }

    @Override
    public void setViewDrawingColors(Color foreground, Color background) {
        parent.setViewDrawingColors(foreground, background);
    }

    @Override
    public boolean selectAll() {
        if (view.hasFocus()) {
            if (model.hasFocus()) {
                drawingStrategy.selectAll();
                return true;
            }
        } else if (childCanvasController != null) {
            return childCanvasController.selectAll();
        }
        return false;
    }

    @Override
    public boolean delete() {
        if (view.hasFocus()) {
            if (model.hasFocus()) {
                drawingStrategy.delete();
                return true;
            }
        } else if (childCanvasController != null) {
            return childCanvasController.delete();
        }
        return false;
    }

    @Override
    public void scale() {
        if (model.hasFocus()) {
            ResizeDialog userInput = new ResizeDialog("Skalowanie obecnej warstwy", getModel().getImage().getWidth(), getModel().getImage().getHeight());
            int width = userInput.getResizedWidth();
            int height = userInput.getResizedHeight();
            if (getModel().getImage().getWidth() != width || getModel().getImage().getHeight() != height) {
                getModel().setImage(ImageUtil.imageToBufferedImage(getModel().getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                repaintAllLayers();
            }
        } else if (childCanvasController != null) {
            childCanvasController.scale();
        }
    }

    @Override
    public void resize() {
        if (model.hasFocus()) {
            ResizeDialog userInput = new ResizeDialog("Zmiana rozmiaru obrazu", getModel().getImage().getWidth(), getModel().getImage().getHeight());
            int width = userInput.getResizedWidth();
            int height = userInput.getResizedHeight();
            if (getModel().getImage().getWidth() != width || getModel().getImage().getHeight() != height) {
                BufferedImage resizedImage = new BufferedImage(width, height, model.getImage().getType());
                Graphics2D g2d = (Graphics2D) resizedImage.getGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, width, height);
                g2d.dispose();
                resizedImage = ImageUtil.imageToBufferedImage(ImageUtil.makeColorTransparent(resizedImage, Color.WHITE));
                g2d = (Graphics2D) resizedImage.getGraphics();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                g2d.drawImage(getModel().getImage(), 0, 0, null);
                g2d.dispose();
                getModel().setImage(resizedImage);
                repaintAllLayers();
            }
        } else if (childCanvasController != null) {
            childCanvasController.resize();
        }
    }

    @Override
    public void changeOffset() {
        if (model.hasFocus()) {
            OffsetChangeJDialog userInput = new OffsetChangeJDialog(model.getXOffset(), model.getYOffset());
            if (!userInput.isCancelled()) {
                model.setOffset(new Point(userInput.getXOffset(), userInput.getYOffset()));
                repaintAllLayers();
            }
        } else if (childCanvasController != null) {
            childCanvasController.changeOffset();
        }
    }

    @Override
    public boolean isUndoHistoryEmpty() {
        if (model.hasFocus()) {
            return undoHistory.size() <= 1;
        } else if (childCanvasController != null) {
            return childCanvasController.isUndoHistoryEmpty();
        } else {
            return true;
        }
    }

    @Override
    public boolean isRedoHistoryEmpty() {
        if (model.hasFocus()) {
            return redoHistory.isEmpty();
        } else if (childCanvasController != null) {
            return childCanvasController.isRedoHistoryEmpty();
        } else {
            return true;
        }
    }

}
