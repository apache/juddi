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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides read access to key/value pairs loaded
 * from a properties file.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Config extends Properties
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(Config.class);

  // jUDDI properties file name
  private static final String PROPFILE_NAME = "juddi.properties";

  // Config singleton instance
  static Config config;

  /**
   * Initialize jUDDI with configuration properties. Default
   * values are first loaded and then any properties declared
   * in the 'juddi.properties' file will be loaded. Property
   * names/values found in 'juddi.properties' will take
   * precedence.
   */
  private Config()
  {
    // Load default property values
    init();

    // Add jUDDI properties from the juddi.properties
    // file found in the classpath. Duplicate property
    // values added in init() will be overwritten.
    try
    {
      InputStream stream = Loader.getResourceAsStream(PROPFILE_NAME);
      if (stream != null)
      {
        Properties props = new Properties();
        props.load(stream);
        putAll(props);
      }
    }
    catch (IOException ioex)
    {
      log.error(
        "An error occured while loading jUDDI "
          + "properties from \""
          + PROPFILE_NAME
          + "\".",
        ioex);
    }
  }

  /**
   * Initialize jUDDI with configuration properties. Default
   * values are first loaded and then any properties found
   * in the 'props' parameter will override any like-named
   * default values.
   */
  private Config(Properties props)
  {
    // Load default property values
    init();

    // Add jUDDI properties from the Properties instance
    // passed in. Duplicate property values added in
    // init() will be overwritten.
    if (props != null)
      putAll(props);
  }

  /**
   * Adds default jUDDI property values. Called
   * only from Config() and Config(Properties).
   */
  public void init()
  {
    // Remove existing property values.
    this.clear();

    // Load core jUDDI default property values.
    this.put("juddi.operatorName","jUDDI.org");
    this.put("juddi.operatorSiteURL","http://localhost:8080/juddi");
    this.put("juddi.adminEmailAddress", "admin@juddi.org");
    this.put("juddi.maxMessageSize","2097152");
    this.put("juddi.maxNameElementsAllowed", "5");
    this.put("juddi.maxNameLength","255");
    this.put("juddi.dataStoreFactory",  "org.apache.juddi.datastore.jdbc.JDBCDataStoreFactory");
    this.put("juddi.auth", "org.apache.juddi.auth.DefaultAuthenticator");
    this.put("juddi.uuidgen", "org.apache.juddi.uuidgen.DefaultUUIDGen");
    this.put("juddi.useMonitor", "true");
    this.put("juddi.monitor", "org.apache.juddi.monitor.jdbc.JDBCMonitor");
    this.put("juddi.transport", "org.apache.juddi.transport.axis.AxisTransport");
    this.put("juddi.useConnectionPool", "false");

    // Load jUDDI Proxy-specific default property values.
    this.put("juddi.inquiryURL", "http://localhost:8080/juddi/inquiry");
    this.put("juddi.publishURL", "http://localhost:8080/juddi/publish");
    this.put("juddi.adminURL", "http://localhost:8080/juddi/admin");
  }

  /**
   * Returns a reference to the singleton Properties instance.
   *
   * @return Config A reference to the singleton Properties instance.
   */
  public static Properties getProperties()
  {
    if (config == null)
      config = createConfig();
    return config;
  }

  /**
   *
   */
  public static String getOperator()
  {
    return getStringProperty("juddi.operatorName");
  }

  /**
   *
   */
  public static String getAdminEmailAddress()
  {
    return getStringProperty("juddi.adminEmailAddress");
  }

  /**
   *
   */
  public static String getDataStoreFactory()
  {
    return getStringProperty("juddi.dataStoreFactory");
  }

  /**
   *
   */
  public static String getMonitor()
  {
    return getStringProperty("juddi.useMonitor");
  }

  public static String getMonitorClass()
  {
    return getStringProperty("juddi.monitor");
  }

  /**
   *
   */
  public static String getTransport()
  {
    return getStringProperty("juddi.transport", null);
  }

  /**
   *
   */
  public static int getMaxMessageSize()
  {
    return getIntProperty("juddi.maxMessageSize", 0);
  }

  /**
   *
   */
  public static int getMaxNameLength()
  {
    return getIntProperty("juddi.maxNameLength", 0);
  }

  /**
   *
   */
  public static int getMaxNameElementsAllowed()
  {
    return getIntProperty("juddi.maxNameElementsAllowed", 0);
  }

  /**
   *
   */
  public static String getOperatorSiteURL()
  {
    return getStringProperty("juddi.operatorSiteURL");
  }

  /**
   *
   */
  public static URL getInquiryURL()
  {
    return getURLProperty("juddi.inquiryURL", null);
  }

  /**
   *
   */
  public static URL getPublishURL()
  {
    return getURLProperty("juddi.publishURL", null);
  }

  /**
   *
   */
  public static URL getAdminURL()
  {
    return getURLProperty("juddi.adminURL", null);
  }

  /**
   * Determines if jUDDI should use an instance of it's
   * supplied org.apache.juddi.util.ConnectionPool or if it should
   * rather than a JDBC DataStore aquired via a JNDI lookup.
   */
  public static boolean useConnectionPool()
  {
    return getBooleanProperty("juddi.useConnectionPool", false);
  }

  /**
   * Retrieves a configuration property as a String object.
   * Loads the juddi.properties file if not already initialized.
   *
   * @param key Name of the property to be returned.
   * @return  Value of the property as a string or null if no property found.
   */
  public static String getStringProperty(String key, String defaultValue)
  {
    String stringVal = defaultValue;

    String propValue = getStringProperty(key);
    if (propValue != null)
      stringVal = propValue;

    return stringVal;
  }

  /**
   * Get a configuration property as an int primitive.
   *
   * @param key Name of the numeric property to be returned.
   * @return Value of the property as an Integer or null if no property found.
   */
  public static int getIntProperty(String key, int defaultValue)
  {
    int intVal = defaultValue;

    String propValue = getStringProperty(key);
    if (propValue != null)
      intVal = Integer.parseInt(propValue);

    return intVal;
  }

  /**
   * Get a configuration property as an long primitive.
   *
   * @param key Name of the numeric property to be returned.
   * @return  Value of the property as an Long or null if no property found.
   */
  public static long getLongProperty(String key, long defaultValue)
  {
    long longVal = defaultValue;

    String propValue = getStringProperty(key);
    if (propValue != null)
      longVal = Long.parseLong(propValue);

    return longVal;
  }

  /**
   * Get a configuration property as a boolean primitive. Note
   * that the value of the returned Boolean will be false if
   * the property sought after exists but is not equal to
   * "true" (ignoring case).
   *
   * @param key Name of the numeric property to be returned.
   * @return Value of the property as an Boolean or null if no property found.
   */
  public static boolean getBooleanProperty(String key, boolean defaultValue)
  {
    boolean boolVal = defaultValue;

    String propValue = getStringProperty(key);
    if ((propValue != null) && (propValue.equalsIgnoreCase("true")))
      boolVal = true;

    return boolVal;
  }

  /**
   * Get a configuration property as a URL object.
   *
   * @param key Name of the url property to be returned.
   * @return Value of the property as an URL or null if no property found.
   */
  public static URL getURLProperty(String key, URL defaultValue)
  {
    URL urlVal = defaultValue;

    String propValue = getStringProperty(key);
    if (propValue != null)
    {
      try
      {
        urlVal = new URL(propValue);
      }
      catch (MalformedURLException muex)
      {
        log.error(
          "The " + key + " property value " + "is invalid: " + propValue,
          muex);
      }
    }

    return urlVal;
  }

  /**
   * Retrieves a configuration property as a String object.
   * Loads the juddi.properties file if not already initialized.
   *
   * @param key Name of the property to be returned.
   * @return  Value of the property as a string or null if no property found.
   */
  public static String getStringProperty(String key)
  {
    if (config == null)
      config = createConfig();

    // no properties to look into, return null
    if (config == null)
      return null;

    // no property name/key to lookup, return null
    if (key == null)
      return null;

    return config.getProperty(key);
  }

  /**
   * Sets a property value in jUDDI's property
   * registry.  Loads the juddi.properties file if
   * not already initialized.
   *
   * @param key Name of the property to be returned.
   * @param property Value as a string.
   */
  public static void setStringProperty(String key, String value)
  {
    if (config == null)
      config = createConfig();

    // no properties to save to, just return
    if (config == null)
      return;

    // no property name/key, just return
    if (key == null)
      return;

    // no property value, attempt removal (otherwise save/replace)
    if (value == null)
      config.remove(key);
    else
      config.setProperty(key, value); // save or replace prop value
  }

  /**
   * Creates a single (singleton) instance of "org.util.Config"
   *
   * @return Config A reference to the singleton Config instance.
   */
  private static synchronized Config createConfig()
  {
    // If multiple threads are waiting to envoke
    // this method only allow the first one to do so.

    if (config == null)
      config = new Config();
    return config;
  }

  /**
   * Returns a String containing a pipe-delimited ('|') list
   * of name/value pairs.
   * @return String pipe-delimited list of name/value pairs.
   */
  public String toString()
  {
    // let's create a place to put the property information
    StringBuffer buff = new StringBuffer(100);

    // gran an enumeration of the property names (or keys)
    Enumeration propKeys = keys();
    while (propKeys.hasMoreElements())
    {
      // extract the Property Name (aka Key) and Value
      String propName = (String) propKeys.nextElement();
      String propValue = getProperty(propName);

      // append the name=value pair to the return buffer
      buff.append(propName.trim());
      buff.append("=");
      buff.append(propValue.trim());
      buff.append("\n");
    }

    return buff.toString();
  }

  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/

  public static void main(String[] args)
  {
    Properties sysProps = null;
    SortedSet sortedPropsSet = null;

    sysProps = Config.getProperties();
    sortedPropsSet = new TreeSet(sysProps.keySet());
    for (Iterator keys = sortedPropsSet.iterator(); keys.hasNext();)
    {
      String key = (String) keys.next();
      System.out.println(key + ": " + sysProps.getProperty(key));
    }
    System.out.println("");
    System.out.println("Inquiry: " + Config.getInquiryURL());
    System.out.println("Publish: " + Config.getPublishURL());
    System.out.println("Admin: " + Config.getAdminURL());

    Config.setStringProperty(
      "juddi.inquiryURL",
      "http://abc.xyz.com/abcdefghijklmnopqrstuvwxyz");

    sysProps = Config.getProperties();
    sortedPropsSet = new TreeSet(sysProps.keySet());
    for (Iterator keys = sortedPropsSet.iterator(); keys.hasNext();)
    {
      String key = (String) keys.next();
      System.out.println(key + ": " + sysProps.getProperty(key));
    }
    System.out.println("");
    System.out.println("Inquiry: " + Config.getInquiryURL());
    System.out.println("Publish: " + Config.getPublishURL());
    System.out.println("Admin: " + Config.getAdminURL());
  }
}