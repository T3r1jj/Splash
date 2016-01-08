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
package ztppro.util.filefilter;

import java.io.File;

public class SLHFileFilter extends DefaultImageFileFilter {

    @Override
    public boolean accept(File f) {
        if (super.accept(f)) {
            return true;
        }
        return f.getName().toLowerCase().endsWith(".slh");
    }

    @Override
    public String getDescription() {
        return "Stan aplikacji Splash (*.slh)";
    }

    @Override
    public String getExtension() {
        return ".slh";
    }

}
