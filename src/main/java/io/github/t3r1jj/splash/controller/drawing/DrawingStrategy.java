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
package io.github.t3r1jj.splash.controller.drawing;

import java.awt.Color;
import java.awt.event.MouseEvent;

import io.github.t3r1jj.splash.controller.CanvasController;
import io.github.t3r1jj.splash.model.ImageModel;

public interface DrawingStrategy extends Cloneable {

    void trackMouse(MouseEvent e, ImageModel model);
    
    void mouseEntered(MouseEvent e);
    
    void mouseDragged(MouseEvent e);

    void mousePressed(MouseEvent e);

    void mouseReleased(MouseEvent e);
    
    void mouseExited(MouseEvent e);

    DrawingStrategy clone() throws CloneNotSupportedException;

    void setController(CanvasController controller);

    void mouseMoved(MouseEvent e);

    void copy();

    void paste();

    Color getFirstColor();

    void setFirstColor(Color firstColor);

    Color getSecondColor();

    void setSecondColor(Color secondColor);
    
    void setSize(int size);
    
    int getSize();

    void selectAll();

    public void delete();

}
