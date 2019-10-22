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

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

public class ComponentContainer {
  
  private File[] targetJarFiles;
  private URL[] targetJarUrls;
  
  private RestrictedURLClassLoader urlClassLoader;
  
  public ComponentContainer(File[] targetJarFiles){
    this.targetJarFiles = targetJarFiles;
  }
  
  private Class findComponent(String targetClass)throws ClassNotFoundException {
    ClassLoader ctxClsLoader = Thread.currentThread().getContextClassLoader();
    if(targetJarFiles != null){

      if(targetJarUrls == null){
        targetJarUrls = new URL[targetJarFiles.length];
        try{
          for(int i=0;i<targetJarFiles.length;i++){
            targetJarUrls[i] = toUrl(targetJarFiles[i]);
          }
        }catch(MalformedURLException mue){
          throw new RuntimeException("File toUrl convert fail!");
        }
      }
      if(urlClassLoader == null){
        synchronized (this){
          urlClassLoader = new RestrictedURLClassLoader(targetJarUrls, ctxClsLoader);
        }
      }
      Class cls = urlClassLoader.loadClass(targetClass);
      return cls;
    }
    throw new ClassNotFoundException("Not Found "+targetClass+"");
  }
  
  public Class loadComponent(String targetClass)throws ClassNotFoundException {
    Class cls = findComponent(targetClass);
    if(cls != null){
      return cls;
    }
    throw new ClassNotFoundException("Not Found "+targetClass+"");
  }
  
  private URL toUrl(File file) throws MalformedURLException {
    String filePath = file.getAbsolutePath();
    filePath = filePath.replace('\\', '/');
    if (filePath.charAt(0) != '/') filePath = "/" + filePath;
    if (file.isDirectory()) filePath = filePath + "/";
    URL url = new URL("file", null, filePath);
    return url;
  }
  
  /**
   * TODO 引数にnullが渡せる場合にうまくいかない？コンストラクタ引数のクラス配列は別途与えたほうがいいか？
   * @param targetClass
   * @param args
   * @return
   */
  public Object createComponent(String targetClass, Class[] argTypes, Object[] args)throws ClassNotFoundException {
    Class cls = loadComponent(targetClass);
    
    try{
      Constructor constructor = cls.getConstructor(argTypes);
      Object obj = constructor.newInstance(args);
      return obj;
    }catch(Exception e){
      throw new RuntimeException("Unable to instantiate target["+targetClass+"]", e);
    }
  }
}
