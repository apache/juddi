/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */


package org.uddi.api_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.apache.juddi.api_v3.AccessPointType;


/**
 * <div style="border:none;border-top:solid #333399 1.0pt;padding:1.0pt 0in 0in 0in"><p class="AppH1" style="margin-left:0in;text-indent:0in"><a name="_Toc8974358">B<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;
</span>Appendix B:&nbsp; Using and Extending the useType Attribute</a></p>

</div>

<p class="MsoBodyText">UDDI provides type information through the useType
attribute on the following UDDI elements: accessPoint, overviewURL,
discoveryURL, contact, address, email and phone. The useType attribute is
intended to provide information on how to use or invoke the resource contained
within the element.&nbsp; This Appendix establishes and explains common values and
conventions for the useType attribute in the context of certain elements, as
well as a model for establishing new common values and conventions.</p>

<p class="AppH2" style="margin-left:0in;text-indent:0in"><a name="_Toc85908385"></a><a name="_Toc53709543"></a><a name="_Toc45096631"></a><a name="_Toc45096173"></a><a name="_Toc42047544"></a><a name="_Toc8974359">B.1<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span>accessPoint</a></p>

<p class="MsoBodyText">Four common values for providing type information about
the accessPoint are: "endPoint", "wsdlDeployment", "bindingTemplate",
and "hostingRedirector.</p>

<p class="AppH3" style="margin-left:0in;text-indent:0in"><a name="_Toc85908386"></a><a name="_Toc53709544"></a><a name="_Toc45096632"></a><a name="_Toc45096174"></a><a name="_Toc42047545">B.1.1<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;
</span>Using the "endPoint" value</a></p>

<p class="MsoBodyText">Typically, the network address of a Web service described
by a bindingTemplate is found in the accessPoint element.&nbsp; This common behavior
is denoted by using the string "endpoint" as the value of the
accessPoint.&nbsp; Decorating an accessPoint with a useType="endPoint"
signifies that a user or application can invoke a Web service at that address.&nbsp;
A sample of such behavior is as follows:</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:example.org:catalog"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Browse catalog Web service<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="endPoint"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://www.example.org/CatalogWebService<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&nbsp;&nbsp; &lt;tModelInstanceDetails&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;tModelInstanceInfo
tModelKey="uddi:example.org:catalog_interface"/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;tModelInstanceInfo
tModelKey="uddi:uddi.org:transport:http"/&gt;<br>
&nbsp;&nbsp; &lt;/tModelInstanceDetails&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="codeSample">&nbsp;</p>

<p class="MsoBodyText">In the example above, a client would be able to parse the
bindingTemplate and discover the end point of the Web service itself.&nbsp; However,
the information about how to invoke that Web service would be modeled using
tModels.&nbsp; All interface information about that service would be represented by
the tModelInstanceInfo structures contained as children of the bindingTemplate.</p>

<p class="MsoBodyText">The client knows the transport of the accessPoint either
by checking to see if a protocol tModel has been associated with the
bindingTemplate or inspecting the URI prefix.&nbsp; In the example above, the http
transport was used, denoted by the tModelInstanceInfo.</p>

<span style="font-size:10.0pt;font-family:Arial;letter-spacing:-.25pt"><br style="page-break-before:always" clear="all">
</span>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">UDDI RECOMMENDS that endpoints for phone, fax and modem
communication follow the guidelines outlined in RFC 2806 <i>URLs for Telephone
Calls</i><a href="#_ftn51" name="_ftnref51" title=""><span class="MsoFootnoteReference"><span class="MsoFootnoteReference"><span style="font-size:10.0pt;letter-spacing:-.25pt">[51]</span></span></span></a>.&nbsp;
Following such a convention for a phone number accessPoint would result in the
following bindingTemplate sample:</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:example.org:catalog"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Browse catalog Web service<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="endPoint"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; tel:+1-512-555-1212<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&nbsp;&nbsp; &lt;tModelInstanceDetails&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;tModelInstanceInfo
tModelKey="uddi:uddi.org:transport:telephone"/&gt;<br>
&nbsp;&nbsp; &lt;/tModelInstanceDetails&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="MsoBodyText">&nbsp;</p>

<p class="AppH3" style="margin-left:0in;text-indent:0in"><a name="_Toc85908387"></a><a name="_Toc53709545"></a><a name="_Toc45096633"></a><a name="_Toc45096175"></a><a name="_Toc42047546">B.1.2<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;
</span>Using the "wsdlDeployment" value</a></p>

<p class="MsoBodyText">Instead of directly providing the network address in the accessPoint,
it is occasionally useful or necessary to provide this information through
indirect means.&nbsp; One common scenario for such a behavior is when the
accessPoint is embedded within a WSDL file.&nbsp; In such a scenario, the UDDI
accessPoint contains the address of the WSDL file, and the client then must
retrieve the WSDL file and extract the end point address from the WSDL file
itself.</p>

<p class="MsoBodyText">In this case, decorating the UDDI accessPoint with a
useType="wsdlDeployment" is appropriate.&nbsp; A sample of such behavior is
as follows:</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:example.org:catalog"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Browse catalog Web service<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="wsdlDeployment"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://www.example.org/CatalogWebService/catalog.wsdl<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;</p>

<p class="codeSample">&nbsp;&nbsp; &lt;categoryBag&gt;<br>
&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &lt;keyedReference keyName="uddi-org:types:wsdl"<br>
&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; keyValue="wsdlDeployment" <br>
&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
tModelKey="uddi:uddi.org:categorization:types"/&gt; <br>
&nbsp;&nbsp; &lt;/categoryBag&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="codeSample">&nbsp;</p>

<p class="MsoBodyText">In the example above, a client would be able to parse the
result of the bindingTemplate and determine the end point of the Web service
within the WSDL file discovered in the accessPoint element. Note that the
bindingTemplate has also been categorized with the "wsdlDeployment"
value from the uddi.org:categorization:types scheme so that it can be
discovered through a find_binding API call.</p>

<p class="AppH3" style="margin-left:0in;text-indent:0in"><a name="_Toc85908388"></a><a name="_Toc53709546"></a><a name="_Toc45096634"></a><a name="_Toc45096176"></a><a name="_Toc42047547">B.1.3<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;
</span>Using the "bindingTemplate" value</a></p>

<p class="MsoBodyText">Another form of indirection uses UDDI itself to discover
the location of the final end point.&nbsp; Categorizing a bindingTemplate with
either "bindingTemplate" or "hostingRedirector" specifies
that the accessPoint contains a bindingKey intended to be used in a
get_bindingDetail API call to a UDDI registry Inquiry API Set.&nbsp; How the
resultant bindingTemplate is interpreted depends on which one of the two
methods described below is used.&nbsp; In the case of "bindingTemplate", a
bindingKey refers to another binding within the same UDDI registry.</p>

<p class="MsoBodyText">For example, suppose tempuri.com, the well known but
fictitious maker of crispy batter coating for fried foods, contracts with the
equally fictitious Web service hosting company ws-o-rama.com to host tempuri’s
Web service that exposes its product catalog. Tempuri.com wishes to publish a
bindingTemplate for this service in the UDDI Business Registry, but wishes to
leave the details of the technical implementation and its description up to
ws-o-rama.com who may wish to change them over time. To do this, tempuri.com
and ws-o-rama.com use bindingTemplate indirection. In the UDDI Business
Registry the bindingTemplate in tempuri’s businessEntity that describes this
service appears as follows:</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:tempuri.com:catalog"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Browse catalog Web service<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="bindingTemplate"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; uddi:ws-o-rama.com:tempuri:bt1<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="codeSample">&nbsp;</p>

<p class="MsoBodyText">Here, the bindingTemplate describing tempuri’s catalog
browsing Web service uses an accessPoint to refer to the bindingTemplate (also
in the UDDI Business Registry) whose bindingKey is uddi:ws-o-rama.com:tempura:bt1.
When a client does a get_bindingDetail asking for the bindingTemplate with that
key, the following bindingTemplate is returned:</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:ws-o-rama.com:tempuri:bt1"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Tempuri.com’s catalog browsing service hosted by ws-o-rama<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="endPoint"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://sf1.ws-o-rama.com/tempuri/catalog/<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&nbsp;&nbsp; &lt;tModelInstanceDetails&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;tModelInstanceInfo tModelKey="uddi:..."/&gt;<br>
&nbsp;&nbsp; &lt;/tModelInstanceDetails&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">This bindingTemplate describes the actual Web service that
is to be used.</p>

<p class="AppH3" style="margin-left:0in;text-indent:0in"><a name="_Toc85908389"></a><a name="_Toc53709547"></a><a name="_Toc45096635"></a><a name="_Toc45096177"></a><a name="_Toc42047548">B.1.4<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;
</span>Using the "hostingRedirector" value</a></p>

<p class="MsoBodyText">It may be necessary to "hide" the network
address of a Web service from unauthorized access.&nbsp; In this case, a useType="hostingRedirector"
can be used to indicate that the client must follow a longer path of
indirection to retrieve the accessPoint.&nbsp; In the bindingTemplate, the client
will discover a bindingKey, which will lead to a bindingTemplate that contains
the end point for a Web service which supports the UDDI get_bindingDetail Web
Service.&nbsp; The client will then be able to re-issue the get_bindingDetail
message with the original key, presumably with authentication, to this other
Web service.&nbsp; Such an indirection mechanism allows a Web service to be
discoverable but not accessible from a given node.&nbsp; </p>

<p class="MsoBodyText">For example, tempuri.com uses ws-o-rama.com to host more
than just its publicly visible catalog browsing service. In particular it has a
number of services it does not wish to expose fully in the UDDI Business
Registry. Instead, it wishes to keep their full definition in its private UDDI
registry, which ws-o-rama.com also happens to host, and supply the end points
to these Web services only to authorized inquirers. </p>

<p class="MsoBodyText">In particular, tempuri has a Web service that its
suppliers use to bill it for goods they deliver. In the UDDI Business Registry,
tempuri publishes the following bindingTemplate, which contains a bindingKey.</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:tempuri.com:billing"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Tempuri supplier billing Web service<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="hostingRedirector"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; uddi:ws-o-rama.com:tempuri:bt47<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">Here, the bindingTemplate describing tempuri’s supplier
billing Web service uses an accessPoint to refer to the bindingTemplate (also
in the UDDI Business Registry) whose bindingKey is uddi:ws-o-rama.com: tempuri:bt47.
Note that the useType equals "hostingRedirector" which indicates that
the bindingKey refers to a hostingRedirector service. When a client does a
get_bindingDetail (on the UDDI Business Registry) asking for the
bindingTemplate with that key, the following indirect bindingTemplate is
returned: </p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:ws-o-rama.com:tempura:bt47"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Hosting Redirector Service for Tempuri.com<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hosted by ws-o-rama.com<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="endPoint"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://sf1.ws-o-rama.com/tempuri/uddi/inquiry<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&nbsp;&nbsp; &lt;tModelInstanceDetails&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;tModelInstanceInfo <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; tModelKey="uddi:uddi.org:specification:hostingredirector"/&gt;<br>
&nbsp;&nbsp; &lt;/tModelInstanceDetails&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="codeSample">&nbsp;</p>

<p class="MsoBodyText">This bindingTemplate describes the hosting redirector Web
service hosted for tempuri.com by ws-o-rama.com. By definition, it responds to
the get_bindingDetail API call using SOAP over HTTP. The description in the
bindingTemplate says it responds only to authorized requests. For authorized
clients, invoking get_bindingDetail, passing the key of the original
bindingTemplate (uddi:tempuri.com:billing) retrieves the following
bindingTemplate:</p>

<p class="codeSample">&lt;bindingTemplate
bindingKey="uddi:tempuri.com:billing"&gt;<br>
&nbsp;&nbsp; &lt;description xml:lang="en"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Tempuri.com’s supplier billing browsing service<br>
&nbsp;&nbsp; hosted by ws-o-rama.com<br>
&nbsp;&nbsp; &lt;/description&gt;<br>
&nbsp;&nbsp; &lt;accessPoint useType="endPoint"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http:sf1.ws-o-rama.com/tempuri/billing/<br>
&nbsp;&nbsp; &lt;/accessPoint&gt;<br>
&nbsp;&nbsp; &lt;tModelInstanceDetails&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;tModelInstanceInfo tModelKey="uddi:..."/&gt;<br>
&nbsp;&nbsp; &lt;/tModelInstanceDetails&gt;<br>
&lt;/bindingTemplate&gt;</p>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">This bindingTemplate describes the actual Web service that
is to be used.</p>
 * <p>Java class for accessPoint complex type.</p>
 * 
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>
 * &lt;complexType name="accessPoint">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:uddi-org:api_v3>validationTypeString4096">
 *       &lt;attribute name="useType" type="{urn:uddi-org:api_v3}useType" default="" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accessPoint", propOrder = {
    "value"
})
public class AccessPoint implements Serializable {
	
	@XmlTransient
	private static final long serialVersionUID = -6377843530866292358L;

	@XmlValue
    protected String value;
    @XmlAttribute
    protected String useType;
    public AccessPoint()
    {}
    /**
     * A convenience constructor<br>
     *@see org.apache.juddi.api_v3.AccessPointType for help on useType
     * @param value
     * @param useType 
     */
    public AccessPoint(String value, String useType)
    {
        this.value = value;
        this.useType = useType;
    }
    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     @see AccessPointType for spec defined values
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the useType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     * @see AccessPointType for spec defined values
     */
    public String getUseType() {
        if (useType == null) {
            return "";
        } else {
            return useType;
        }
    }

    /**
     * Sets the value of the useType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUseType(String value) {
        this.useType = value;
    }

}