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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

import io.github.t3r1jj.splash.controller.CanvasController;
import io.github.t3r1jj.splash.view.TextDialog;

public class TextStrategy extends DefaultDrawingStrategy {

    protected int characterVerticalIndex = 0;

    public TextStrategy(CanvasController controller) {
        super(controller);
        drawingCursor = new Cursor(Cursor.TEXT_CURSOR);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        TextDialog userInput = new TextDialog();
        if (userInput.getText() != null && !userInput.getText().isEmpty()) {
            String[] lines = userInput.getText().split("\n");
            for (String line : lines) {
                AttributedString attributedString = new AttributedString(line);
                Font font = null;
                int style = -1;
                if (userInput.isBold()) {
                    style = Font.BOLD;
                }
                if (userInput.isItalic()) {
                    style |= Font.ITALIC;
                }
                if (style == -1) {
                    style = Font.PLAIN;
                }
                font = new Font(userInput.getTextFont(), style, userInput.getTextSize());
                attributedString.addAttribute(TextAttribute.FONT, font);
                attributedString.addAttribute(TextAttribute.FOREGROUND, userInput.getTextColor());
                if (userInput.isStrikethrough()) {
                    attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                }
                if (userInput.isUnderline()) {
                    attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                if (userInput.isBold()) {
                    attributedString.addAttribute(TextAttribute.BACKGROUND, TextAttribute.STRIKETHROUGH_ON);
                }
                if (userInput.getFillingColor() != null) {
                    attributedString.addAttribute(TextAttribute.BACKGROUND, userInput.getFillingColor());
                }

                drawText(attributedString, userInput.getTextSize(), e);
            }
            controller.repaintAllLayers();
            characterVerticalIndex = 0;
            controller.addCurrentStateToHistory();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    protected void drawText(AttributedString text, int fontSize, MouseEvent startingEvent) {
        Graphics2D g2d = (Graphics2D) controller.getModel().getImage().getGraphics();
        g2d.setColor(firstColor);
        g2d.drawString(text.getIterator(),
                (startingEvent.getX() - controller.getModel().getZoomedXOffset()) / controller.getModel().getZoom(),
                (startingEvent.getY() - controller.getModel().getZoomedYOffset()) / controller.getModel().getZoom() + (1 + characterVerticalIndex) * fontSize);
        characterVerticalIndex++;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
