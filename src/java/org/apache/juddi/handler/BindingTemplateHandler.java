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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class BindingTemplateHandler extends AbstractHandler
{
  public static final String TAG_NAME = "bindingTemplate";

  private HandlerMaker maker = null;

  protected BindingTemplateHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    BindingTemplate obj = new BindingTemplate();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setServiceKey(element.getAttribute("serviceKey"));
    obj.setBindingKey(element.getAttribute("bindingKey"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      obj.addDescription((Description)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,AccessPointHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(AccessPointHandler.TAG_NAME);
      obj.setAccessPoint((AccessPoint)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,HostingRedirectorHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(HostingRedirectorHandler.TAG_NAME);
      obj.setHostingRedirector((HostingRedirector)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,TModelInstanceDetailsHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(TModelInstanceDetailsHandler.TAG_NAME);
      obj.setTModelInstanceDetails((TModelInstanceDetails)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

// TODO (UDDI v3) This code should be uncommented when jUDDI is ready to support UDDI v3.0     
//    nodeList = XMLUtils.getChildElementsByTagName(element,CategoryBagHandler.TAG_NAME); // UDDI v3.0
//    if (nodeList.size() > 0)
//    {
//      handler = maker.lookup(CategoryBagHandler.TAG_NAME);
//      obj.setCategoryBag((CategoryBag)handler.unmarshal((Element)nodeList.elementAt(0)));
//    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    BindingTemplate binding = (BindingTemplate)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String serviceKey = binding.getServiceKey();
    if (serviceKey != null)
      element.setAttribute("serviceKey",serviceKey);

    String bindingKey = binding.getBindingKey();
    if (bindingKey != null)
      element.setAttribute("bindingKey",bindingKey);

    Vector descrVector = binding.getDescriptionVector();
    if ((descrVector!=null) && (descrVector.size() > 0))
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      for (int i=0; i < descrVector.size(); i++)
        handler.marshal((Description)descrVector.elementAt(i),element);
    }

    AccessPoint accessPoint = binding.getAccessPoint();
    if (accessPoint != null)
    {
      handler = maker.lookup(AccessPointHandler.TAG_NAME);
      handler.marshal(accessPoint,element);
    }

    HostingRedirector redirector = binding.getHostingRedirector();
    if (redirector != null)
    {
      handler = maker.lookup(HostingRedirectorHandler.TAG_NAME);
       handler.marshal(redirector,element);
    }

    TModelInstanceDetails tModInstDet = binding.getTModelInstanceDetails();
    if (tModInstDet != null)
    {
      handler = maker.lookup(TModelInstanceDetailsHandler.TAG_NAME);
      handler.marshal(tModInstDet,element);
    }

//  TODO (UDDI v3) This code should be uncommented when jUDDI is ready to support UDDI v3.0     
//    CategoryBag categoryBag = binding.getCategoryBag(); // UDDI v3.0
//    if (categoryBag != null)
//    {
//      handler = maker.lookup(CategoryBagHandler.TAG_NAME);
//      handler.marshal(categoryBag,element);
//    }

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(BindingTemplateHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    OverviewDoc overDoc = new OverviewDoc();
    overDoc.setOverviewURL("http://www.sviens.com/service.html");
    overDoc.addDescription(new Description("over-doc Descr"));
    overDoc.addDescription(new Description("over-doc Descr Two","en"));

    InstanceDetails instDetails = new InstanceDetails();
    instDetails.addDescription(new Description("body-isa-wunder"));
    instDetails.addDescription(new Description("sweetchild-o-mine","it"));
    instDetails.setInstanceParms("inst-det parameters");
    instDetails.setOverviewDoc(overDoc);

    TModelInstanceInfo tModInstInfo = new TModelInstanceInfo();
    tModInstInfo.setTModelKey("uuid:ae0f9fd4-287f-40c9-be91-df47a7c72fd9");
    tModInstInfo.addDescription(new Description("tMod-Inst-Info"));
    tModInstInfo.addDescription(new Description("tMod-Inst-Info too","es"));
    tModInstInfo.setInstanceDetails(instDetails);

    TModelInstanceDetails tModInstDet = new TModelInstanceDetails();
    tModInstDet.addTModelInstanceInfo(tModInstInfo);

    BindingTemplate binding =  new BindingTemplate();
    binding.setBindingKey("c9613c3c-fe55-4f34-a3da-b3167afbca4a");
    binding.setServiceKey("997a55bc-563d-4c04-8a94-781604870d31");
    binding.addDescription(new Description("whatever"));
    binding.addDescription(new Description("whatever too","fr"));
    binding.setHostingRedirector(new HostingRedirector("92658289-0bd7-443c-8948-0bb4460b44c0"));
    binding.setAccessPoint(new AccessPoint(AccessPoint.HTTP,"http://www.sviens.com/service.wsdl"));
    binding.setTModelInstanceDetails(tModInstDet);

    System.out.println();

    RegistryObject regObject = binding;
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

    System.out.println();
  }
}