/*
 * MIT License
 *
 * Copyright (c) 2019 Jun Ohtani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.johtani.misc.ja.dictionary.compare;

import java.net.URL;
import java.net.URLClassLoader;

public class RestrictedURLClassLoader extends URLClassLoader {
  public RestrictedURLClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  public Class loadClass(String name) throws ClassNotFoundException {
    Class cls = super.loadClass(name);
    
    if (cls == null) {
      throw new ClassNotFoundException("Restricted ClassLoader"
          + " is unable to find class: " + name);
    }

    return cls;
  }
}