/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.util;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The idea for most of this code was taken from the Apache
 * (Jakarta) Log4j project: http://jakarta.apache.org/log4j
 *
 *  - Steve
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author anou_mana;
*/
public class Loader
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(Loader.class);

  /**
   * @param resource
   * @return InputStream to the named resource
   */
  public static InputStream getResourceAsStream(String resource)
  {
    ClassLoader classLoader = null;
    InputStream stream = null;

    // Get the Thread Context Class Loader which is
    // only available in JDK 1.2 and later.
    try
    {
      classLoader = getContextClassLoader();
      if (classLoader != null)
      {
        log.debug("Trying to find ["+resource+"] using context classloader "+classLoader+".");
        stream = classLoader.getResourceAsStream(resource);
        if(stream != null) {
          return stream;
        }
      }

      // We could not find resource. Ler us now try
      // with the class loader that loaded this class.
      classLoader = Loader.class.getClassLoader();
      if (classLoader != null)
      {
        log.debug("Trying to find ["+resource+"] using "+classLoader+" class loader.");
        stream = classLoader.getResourceAsStream(resource);
        if(stream != null) {
          return stream;
        }
      }
    }
    catch(Throwable th) {
      log.warn("Exception thrown from Loader.getResource(\""+resource+"\").",th);
    }

    // Last ditch attempt: get the resource from the class path. It
    // may be the case that class was loaded by the Extentsion class
    // loader which the parent of the system class loader.
    log.debug("Trying to find ["+resource+"] using ClassLoader.getSystemResource().");

    return ClassLoader.getSystemResourceAsStream(resource);
  }

  /**
   * @param resource name
   * @return URL to the named resource
   */
  public static URL getResource(String resource)
  {
    ClassLoader classLoader = null;
    URL url = null;

    // Get the Thread Context Class Loader which is
    // only available in JDK 1.2 and later.
    try
    {
      classLoader = getContextClassLoader();
      if (classLoader != null)
      {
        log.debug("Trying to find ["+resource+"] using context classloader "+classLoader+".");
        url = classLoader.getResource(resource);
        if(url != null) {
          return url;
        }
      }

      // We could not find resource. Ler us now try
      // with the class loader that loaded this class.
      classLoader = Loader.class.getClassLoader();
      if (classLoader != null)
      {
        log.debug("Trying to find ["+resource+"] using "+classLoader+" class loader.");
        url = classLoader.getResource(resource);
        if(url != null) {
          return url;
        }
      }
    }
    catch(Throwable th) {
      log.warn("Exception thrown from Loader.getResource(\""+resource+"\").",th);
    }

    // Last ditch attempt: get the resource from the class path. It
    // may be the case that class was loaded by the Extentsion class
    // loader which the parent of the system class loader.
    log.debug("Trying to find ["+resource+"] using ClassLoader.getSystemResource().");

    return ClassLoader.getSystemResource(resource);
  }

  /**
   * Get the Thread Context Class Loader which is only available
   * in JDK 1.2 and later.
   * @return null if running under a JDK that's earlier than 1.2
   **/
  private static ClassLoader getContextClassLoader()
    throws IllegalAccessException, InvocationTargetException
  {
    Method method = null;
    try {
      method = Thread.class.getMethod("getContextClassLoader",null);
    }
    catch (NoSuchMethodException e) {
      return null; // Using JDK 1.1 or earlier
    }

    return (ClassLoader)method.invoke(Thread.currentThread(),null);
  }

  /**
   * 
   * @param name
   * @return The class object for the name given
   * @throws ClassNotFoundException
   * @throws NoClassDefFoundError
   */
  public static Class getClassForName(String name)
    throws ClassNotFoundException, NoClassDefFoundError
  {
    Class clazz = null;

    try
    {
      log.info("Using the Context ClassLoader");
      ClassLoader ccl = Thread.currentThread().getContextClassLoader();
      clazz = Class.forName(name, true, ccl);
    }
    catch (Exception e)
    {
      log.warn("Failed to load the class " + name + " with context class loader " + e);
    }

    if (null == clazz)
    {
      ClassLoader scl = ClassLoader.getSystemClassLoader();

      try
      {
        log.info("Using the System ClassLoader");
        clazz = Class.forName(name, true, scl);
      }
      catch (Exception e)
      {
        log.warn("Failed to load the class " + name + " with system class loader " + e);
      }
    }

    return clazz;
  }
}