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
package io.github.t3r1jj.splash.model.imagefilter;

import java.awt.image.*;

import io.github.t3r1jj.splash.model.ImageModel;

public class BrightnessFilter implements ImageFilter {

    private final double percentage;

    public BrightnessFilter(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public void processImage(ImageModel model) {
        BufferedImage image = model.getImage();
        processImage(image);
    }

    @Override
    public void processImage(BufferedImage image) {
        float scaleFactor = (float) ((percentage / 100.0) + 1f);
        RescaleOp rescaleOp = new RescaleOp(scaleFactor, 0, null);
        rescaleOp.filter(image, image);
    }

}
