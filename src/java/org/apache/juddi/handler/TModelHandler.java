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

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * TModelHandler
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class TModelHandler extends AbstractHandler
{
  public static final String TAG_NAME = "tModel";

  private HandlerMaker maker = null;

  protected TModelHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    TModel obj = new TModel();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setTModelKey(element.getAttribute("tModelKey"));
    obj.setOperator(element.getAttribute("operator"));
    obj.setAuthorizedName(element.getAttribute("authorizedName"));

    // Text Node Value
    // [none]

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      obj.setName((Name)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      obj.addDescription((Description)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,OverviewDocHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(OverviewDocHandler.TAG_NAME);
      obj.setOverviewDoc((OverviewDoc)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,IdentifierBagHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(IdentifierBagHandler.TAG_NAME);
      obj.setIdentifierBag((IdentifierBag)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,CategoryBagHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(CategoryBagHandler.TAG_NAME);
      obj.setCategoryBag((CategoryBag)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    TModel tModel = (TModel)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String tModelKey = tModel.getTModelKey();
    if (tModelKey != null)
      element.setAttribute("tModelKey",tModelKey);
    else
      element.setAttribute("tModelKey","");

    String operator = tModel.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

    String authName = tModel.getAuthorizedName();
    if (authName != null)
      element.setAttribute("authorizedName",authName);

    String name = tModel.getName();
    if (name != null)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      handler.marshal(new Name(name),element);
    }

    Vector descrVector = tModel.getDescriptionVector();
    if ((descrVector!=null) && (descrVector.size() > 0))
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      for (int i=0; i < descrVector.size(); i++)
        handler.marshal((Description)descrVector.elementAt(i),element);
    }

    OverviewDoc overDoc = tModel.getOverviewDoc();
    if (overDoc != null)
    {
      handler = maker.lookup(OverviewDocHandler.TAG_NAME);
      handler.marshal(overDoc,element);
    }

    IdentifierBag identifierBag = tModel.getIdentifierBag();
    if ((identifierBag != null) && (identifierBag.getKeyedReferenceVector() != null) && (!identifierBag.getKeyedReferenceVector().isEmpty()))
    {
      handler = maker.lookup(IdentifierBagHandler.TAG_NAME);
      handler.marshal(identifierBag,element);
    }

    CategoryBag categoryBag = tModel.getCategoryBag();
    if ((categoryBag != null) && (categoryBag.getKeyedReferenceVector() != null) && (!categoryBag.getKeyedReferenceVector().isEmpty()))
    {
      handler = maker.lookup(CategoryBagHandler.TAG_NAME);
      handler.marshal(categoryBag,element);
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
    AbstractHandler handler = maker.lookup(TModelHandler.TAG_NAME);

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

    System.out.println();

    RegistryObject regObject = tModel;
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