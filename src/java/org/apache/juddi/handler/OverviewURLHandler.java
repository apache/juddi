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
package org.apache.juddi.handler;

import org.apache.juddi.datatype.OverviewURL;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of AddressLine objects.
 * Returns AddressLine."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class OverviewURLHandler extends AbstractHandler
{
  public static final String TAG_NAME = "overviewURL";

  private HandlerMaker maker = null;

  protected OverviewURLHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    OverviewURL obj = new OverviewURL();

    // Attributes
    // {none}

    // Text Node Value
    obj.setValue(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    OverviewURL overURL = (OverviewURL)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String urlValue = overURL.getValue();
    if (urlValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(urlValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
  }
}