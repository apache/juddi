/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.util;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Release 
{
  private static final String VERSION = "0.9rc1";
  
  public static String getVersion()
  {
    return VERSION;
  }
  
  public static String getLastModified()
  {
    String filePath = getClassFileLocation(Release.class);
    if (filePath == null)
      return "Unknown";
      
    File file = new File(filePath);
    long lastMod = file.lastModified();
  
    return DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG).format(new Date(lastMod));
  }

  /**
   * Determine the jar file or class file containing a Java 
   * classes byte code.
   *
   * @param clazz
   * @return the file path to the jar file or class 
   *  file where the class byte code is located.
   */
  private static String getClassFileLocation(Class clazz)
  {
    // class was found, now get it's URL
    URL url = null;
    try {
      url = clazz.getProtectionDomain().getCodeSource().getLocation();
      if (url == null)
        return "";
    }
    catch(Throwable t) {
      return "";
    }

    try
    {
      String location = url.toString();
      if (location.startsWith("jar:file:/"))
      {
        File file = new File(url.getFile());
        return file.getPath().substring(6);
      }
      else if (location.startsWith("jar")) 
      {
        url = ((JarURLConnection)url.openConnection()).getJarFileURL();
        return url.toString();
      }
      else if (location.startsWith("file")) 
      {
        File file = new File(url.getFile());
        return file.getAbsolutePath();
      }
      else
      {
        return url.toString();
      }
    } 
    catch (Throwable t) { 
      return null;
    }
  }
}
