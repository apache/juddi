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

import java.util.Vector;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of TModelDetail objects.
 * Returns TModelDetail."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelDetailHandler extends AbstractHandler
{
  public static final String TAG_NAME = "tModelDetail";

  private HandlerMaker maker = null;

  protected TModelDetailHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    TModelDetail obj = new TModelDetail();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // We could use the generic attribute value to
    // determine which version of UDDI was used to
    // format the request XML. - Steve

    // Attributes
    obj.setGeneric(element.getAttribute("generic"));
    obj.setOperator(element.getAttribute("operator"));

    // We can ignore the xmlns attribute since we
    // can always determine it's value using the
    // "generic" attribute. - Steve

    String truncValue = element.getAttribute("truncated");
    if (truncValue != null)
      obj.setTruncated(truncValue.equalsIgnoreCase("true"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,TModelHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(TModelHandler.TAG_NAME);
      obj.addTModel((TModel)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    TModelDetail detail = (TModelDetail)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String generic = detail.getGeneric();
    if (generic != null)
    {
      element.setAttribute("generic",generic);

      if (generic.equals(IRegistry.UDDI_V1_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V1_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V2_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V2_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V3_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V3_NAMESPACE);
    }

    String operator = detail.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

    boolean truncated = detail.isTruncated();
    if (truncated)
      element.setAttribute("truncated","true");

    Vector vector = detail.getTModelVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      handler = maker.lookup(TModelHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
        handler.marshal((TModel)vector.elementAt(i),element);
    }

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(TModelDetailHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    OverviewDoc overDoc = new OverviewDoc();
    overDoc.setOverviewURL("http://www.sviens.com/service.html");
    overDoc.addDescription(new Description("over-doc Descr"));
    overDoc.addDescription(new Description("over-doc Descr Two","en"));

    CategoryBag catBag = new CategoryBag();
    catBag.addKeyedReference(new KeyedReference("catBagKeyName","catBagKeyValue"));
    catBag.addKeyedReference(new KeyedReference("uuid:dfddb58c-4853-4a71-865c-f84509017cc7","catBagKeyName2","catBagKeyValue2"));

    IdentifierBag idBag = new IdentifierBag();
    idBag.addKeyedReference(new KeyedReference("idBagKeyName","idBagkeyValue"));
    idBag.addKeyedReference(new KeyedReference("uuid:f78a135a-4769-4e79-8604-54d440314bc0","idBagKeyName2","idBagkeyValue2"));

    TModel tModel = new TModel();
    tModel.setTModelKey("uuid:269855db-62eb-4862-8e5a-1b06f2753038");
    tModel.setOperator("jUDDI");
    tModel.setAuthorizedName("Steve Viens");
    tModel.setName("jUDDI TModel");
    tModel.addDescription(new Description("tModel whatever"));
    tModel.addDescription(new Description("tModel whatever too","fr"));
    tModel.setCategoryBag(catBag);
    tModel.setIdentifierBag(idBag);
    tModel.setOverviewDoc(overDoc);

    TModelDetail detail = new TModelDetail();
    detail.setGeneric("2.0");
    detail.setOperator("jUDDI.org");
    detail.setTruncated(true);
    detail.addTModel(tModel);
    detail.addTModel(tModel);

    System.out.println();

    RegistryObject regObject = detail;
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);

    System.out.println();

    regObject = handler.unmarshal(child);
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);
  }
}