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
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.BindingTemplates;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * BusinessServiceHandler
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessServiceHandler extends AbstractHandler
{
  public static final String TAG_NAME = "businessService";

  private HandlerMaker maker = null;

  protected BusinessServiceHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    BusinessService obj = new BusinessService();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setBusinessKey(element.getAttribute("businessKey"));
    obj.setServiceKey(element.getAttribute("serviceKey"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      obj.addName((Name)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      obj.addDescription((Description)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,BindingTemplatesHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(BindingTemplatesHandler.TAG_NAME);
      obj.setBindingTemplates((BindingTemplates)handler.unmarshal((Element)nodeList.elementAt(0)));
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
    BusinessService service = (BusinessService)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String serviceKey = service.getServiceKey();
    if (serviceKey != null)
      element.setAttribute("serviceKey",serviceKey);
    else
      element.setAttribute("serviceKey","");

    String businessKey = service.getBusinessKey();
    if (businessKey != null)
      element.setAttribute("businessKey",businessKey);

    Vector nameVector = service.getNameVector();
    if ((nameVector!=null) && (nameVector.size() > 0))
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      for (int i=0; i < nameVector.size(); i++)
        handler.marshal((Name)nameVector.elementAt(i),element);
    }

    Vector descrVector = service.getDescriptionVector();
    if ((descrVector!=null) && (descrVector.size() > 0))
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      for (int i=0; i < descrVector.size(); i++)
        handler.marshal((Description)descrVector.elementAt(i),element);
    }

    BindingTemplates bindings = service.getBindingTemplates();
    if (bindings != null)
    {
      handler = maker.lookup(BindingTemplatesHandler.TAG_NAME);
      handler.marshal(bindings,element);
    }

    CategoryBag categoryBag = service.getCategoryBag();
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
    AbstractHandler handler = maker.lookup(BusinessServiceHandler.TAG_NAME);
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

    BindingTemplates bindings = new BindingTemplates();
    bindings.addBindingTemplate(binding);

    CategoryBag catBag = new CategoryBag();
    catBag.addKeyedReference(new KeyedReference("keyName","keyValue"));
    catBag.addKeyedReference(new KeyedReference("uuid:dfddb58c-4853-4a71-865c-f84509017cc7","keyName2","keyValue2"));

    BusinessService service = new BusinessService();
    service.setServiceKey("fe8af00d-3a2c-4e05-b7ca-95a1259aad4f");
    service.setBusinessKey("b8cc7266-9eed-4675-b621-34697f252a77");
    service.setBindingTemplates(bindings);
    service.setCategoryBag(catBag);
    service.addName(new Name("serviceNm"));
    service.addName(new Name("serviceNm2","en"));
    service.addDescription(new Description("service whatever"));
    service.addDescription(new Description("service whatever too","it"));

    System.out.println();

    RegistryObject regObject = service;
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


//    String xml =
//      "<businessService businessKey=\"34D599999999-4444-4444-4444-88888888\" " +
//      "serviceKey=\"34D599999999-4444-4444-4444-88888888\">\n" +
//      "<name>fred</name>\n" +
//      "<description>desc one</description>" +
//      "<bindingTemplates>\n" +
//      "<bindingTemplate bindingKey=\"34D599999999-4444-4444-4444-33333333\">" +
//      " serviceKey=\"34D599999999-4444-4444-4444-22222222\">\n" +
//      "<description>whatever</description>" +
//      "<description>whatever too</description>" +
//      "<hostingRedirector bindingKey=\"999999999999-4444-4444-4444-88888888\"></hostingRedirector>" +
//      "<tModelInstanceDetails>\n" +
//      "<tModelInstanceInfo tModelKey=\"uuid:34D599999999-4444-5555-4444-88888888\">\n" +
//      "<description>GR Proprietary XML purchase order</description>\n" +
//      "<description> d1 </description>\n" +
//      "</tModelInstanceInfo>\n" +
//      "<tModelInstanceInfo tModelKey=\"uuid:34D599999999-4444-4444-4444-88888888\">\n" +
//      "<description>Fred Proprietary XML purchase order</description>\n" +
//      "<description> d2 </description>\n" +
//      "</tModelInstanceInfo>\n" +
//      "</tModelInstanceDetails>\n" +
//      "</bindingTemplate>\n" +
//      "</bindingTemplates>\n" +
//      "<categoryBag>\n" +
//      "<keyedReference tModelKey=\"uuid:999999999999-4444-4444-4444-88888888\"\n" +
//      "keyName=\"name\" keyValue=\"value\"></keyedReference>\n" +
//      "<keyedReference tModelKey=\"uuid:119999999999-4444-4444-4444-88888888\"\n" +
//      "keyName=\"name of key\" keyValue=\"val\"></keyedReference>\n" +
//      "</categoryBag>\n" +
//      "</businessService>\n";
  }
}