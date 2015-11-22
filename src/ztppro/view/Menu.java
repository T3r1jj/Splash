package ztppro.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import ztppro.controller.CanvasController;
import ztppro.controller.Controller;
import ztppro.model.LayersModel;
import ztppro.model.Model;

/**
 *
 * @author Damian Terlecki
 */
public class Menu extends JMenuBar implements View {

    Controller mainController;
    JLayeredPane layeredPane;
    private LayersModel layersModel = new LayersModel();
    private Model model;

    public void setModel(Model model) {
        this.model = model;
    }
    
    public void setLayeredPane(JLayeredPane layeredPane) {
        this.layeredPane = layeredPane;
    }

    Menu(Controller controller, LayersModel layersModel) {
        this.mainController = controller;
        this.layersModel = layersModel;
        //Set up the lone menu.
        JMenu menu = new JMenu("Plik");
        menu.setMnemonic(KeyEvent.VK_P);
        this.add(menu);

        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("Nowy");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.addActionListener((ActionEvent e) -> {
            JDialog dialog = new NewSheet(800, 600);
            dialog.pack();
            dialog.setVisible(true);
        });
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Wyjdź");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        menu.add(menuItem);

    }

    public Controller getController() {
        return mainController;
    }
    
    

//    //Create a new internal frame.
//    protected void createFrame() {
//        MyInternalFrame frame = new MyInternalFrame(layersModel);
//        frame.setVisible(true); //necessary as of 1.3
//        mainController.addToDesktop(frame);
//        try {
//            frame.setSelected(true);
//        } catch (java.beans.PropertyVetoException e) {
//        }
//    }

    @Override
    public void addToDesktop(MyInternalFrame frame) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Graphics paintLayer(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public class NewSheet extends JDialog {

        private final JTextField widthTextField;
        private final JTextField heightTextField;
        private static final int BORDER_WIDTH = 5;

        public NewSheet(int defaultWidth, int defaultHeight) {
            setTitle("Nowy");
            setLayout(new GridBagLayout());
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            //noinspection SuspiciousNameCombination
            widthTextField = new IntTextField(Integer.toString(defaultWidth));
            widthTextField.setName("widthTF");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 1;
            c.gridy = 0;
            this.add(new JLabel("Szerokość: "), c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 2;
            c.gridy = 0;
            this.add(widthTextField, c);
//            gridBagHelper.addLabelWithControl("Width:", widthTextField);

            heightTextField = new IntTextField(Integer.toString(defaultHeight));
            heightTextField.setName("heightTF");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 1;
            c.gridy = 1;
            this.add(new JLabel("Wysokość: "), c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 2;
            c.gridy = 1;
            this.add(heightTextField, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 1;
            c.gridy = 4;
            JButton create = new JButton("Stwórz");
            this.add(create);
            create.addActionListener(new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    layeredPane = new JLayeredPane();
                    JPanel panel = new JPanel();
                    MyInternalFrame frame = new MyInternalFrame(layersModel, Menu.this);
                    frame.setVisible(true); //necessary as of 1.3
                    mainController.addToDesktop(frame);
                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException ex) {
                        Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    panel.setLayout(new GridBagLayout());

                    Canvas canvas = new Canvas(mainController, ((IntTextField) widthTextField).getIntValue(), ((IntTextField) heightTextField).getIntValue(), null);
                    model = canvas.getModel();
                    JScrollPane scroller = new JScrollPane(layeredPane);
                    frame.getContentPane().add(scroller, BorderLayout.CENTER);
                    panel.setLayout(new GridBagLayout());
//                    panel.add(canvas);
                    layersModel.addLayer(canvas.getModel());
                    layeredPane.add(canvas, 1);
                    frame.add(layeredPane, BorderLayout.CENTER);
                    frame.setController((CanvasController) canvas.getController());
                    frame.add(new InfoPanel(mainController), BorderLayout.SOUTH);
                    NewSheet.this.dispose();
                }

            });
            JButton layer = new JButton("Nowa warstwa");
            this.add(layer);
            layer.addActionListener(new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
//                    JPanel panel = new JPanel();
//                    MyInternalFrame frame = new MyInternalFrame();
//                    frame.setVisible(true); //necessary as of 1.3
//                    mainController.addToDesktop(frame);
//                    try {
//                        frame.setSelected(true);
//                    } catch (PropertyVetoException ex) {
//                        Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    panel.setLayout(new GridBagLayout());
                    Canvas canvas = new Canvas(mainController, ((IntTextField) widthTextField).getIntValue(), ((IntTextField) heightTextField).getIntValue(), model);
//                    layeredPane.requestFocus();
//                    mainController.addChildController((CanvasController) canvas.getController());
//                    JScrollPane scroller = new JScrollPane(panel);
//                    frame.getContentPane().add(scroller, BorderLayout.CENTER);
//                    panel.setLayout(new GridBagLayout());
//                    JPanel wrapperPanel = new JPanel();
//                    wrapperPanel.add(canvas)
                    layersModel.addLayer(canvas.getModel());
//                    System.out.println(layeredPane.getComponentCount());
                    layeredPane.add(canvas, layeredPane.getComponentCount());
                    System.out.println(layeredPane.getComponentCount());
                    canvas.getModel().setLayerNumber(layeredPane.getComponentCount());

//                    frame.add(panel, BorderLayout.CENTER);
//                    frame.setController((CanvasController) canvas.getController());
//                    frame.add(new InfoPanel(mainController), BorderLayout.SOUTH);
                    NewSheet.this.dispose();
                }

            });
            this.pack();
            this.setVisible(true);
        }

    }
}
