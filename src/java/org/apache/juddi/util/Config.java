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
  // private reference to the log
  private static Log log = LogFactory.getLog(Config.class);

  // shared instance
  static Config config;

  /**
   * Default Config Constructor. Creates a new instance
   * of Config and initializes it with default values. 
   */
  private Config()
  {
    // Load default property values
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
    this.put("juddi.useConnectionPool", "false");
  }

  /**
   * Returns a reference to the singleton Properties instance.
   *
   * @return Config A reference to the singleton Properties instance.
   */
  public static void addProperties(Properties props)
  {
    if (config == null)
      config = createConfig();
    config.putAll(props);
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
  }
}