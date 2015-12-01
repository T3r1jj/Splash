package ztppro.util.io;

import ztppro.controller.Controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import ztppro.model.Memento;

/**
 *
 * @author Damian Terlecki
 */
public class ApplicationStateLoader implements FileOpener {

    private final Controller controller;

    public ApplicationStateLoader(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            controller.getLayersModel().restoreState((Memento) ois.readObject());
            ois.close();
        }
    }

}