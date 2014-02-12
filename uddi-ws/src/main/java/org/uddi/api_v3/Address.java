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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <h4 style="margin-left:0in;text-indent:0in"><a name="_Ref8977291">3.3.2.7 address</a></h4>

<p class="MsoBodyText"><b>address</b> represents the contact’s postal address, in
a form suitable for addressing an envelope. The address structure is a simple
list of address lines.</p>

<p class="MsoBodyText"><b>Attributes</b></p>

<table class="MsoNormalTable" style="margin-left:.5in;border-collapse:collapse;border:none" border="1" cellpadding="0" cellspacing="0">
 <tbody><tr>
  <td style="border:solid black 1.0pt;background:#FFFFCA;padding:
  0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal"><b>Name&nbsp; </b></p>
  </td>
  <td style="border:solid black 1.0pt;border-left:none;background:
  #FFFFCA;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal"><b>Use&nbsp; </b></p>
  </td>
 </tr>
 <tr>
  <td style="border:solid black 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">xml:lang&nbsp; </p>
  </td>
  <td style="border-top:none;border-left:none;border-bottom:solid black 1.0pt;
  border-right:solid black 1.0pt;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">optional&nbsp; </p>
  </td>
 </tr>
 <tr>
  <td style="border:solid black 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">useType</p>
  </td>
  <td style="border-top:none;border-left:none;border-bottom:solid black 1.0pt;
  border-right:solid black 1.0pt;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">optional</p>
  </td>
 </tr>
 <tr>
  <td style="border:solid black 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">sortCode</p>
  </td>
  <td style="border-top:none;border-left:none;border-bottom:solid black 1.0pt;
  border-right:solid black 1.0pt;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">optional</p>
  </td>
 </tr>
 <tr>
  <td style="border:solid black 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">tModelKey</p>
  </td>
  <td style="border-top:none;border-left:none;border-bottom:solid black 1.0pt;
  border-right:solid black 1.0pt;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">optional</p>
  </td>
 </tr>
</tbody></table>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">Address structures have four optional attributes.</p>

<p class="MsoBodyText">The <b>xml:lang</b> value describes the language the
address is expressed in. There is no restriction on the number of addresses or
what xml:lang value they may have. Publication of addresses in several
languages, e.g. for use in multilingual countries, is supported. See Appendix D
<i>Internationalization</i> for an example.</p>

<p class="MsoBodyText">The <b>useType</b> describes the address’ type in
unstructured text. Suggested examples include "headquarters", "sales
office", "billing department", etc.</p>

<p class="MsoBodyText">The <b>sortCode</b> attribute is deprecated because of the
guarantee of preserving the document order (see Section <a href="#_Ref8978002 ">4.5.3</a>
<i>Preservation of Document Order</i>). In order to achieve this behavior, the
data has just to be published in the desired order.</p>

<p class="MsoBodyText">The <b>tModelKey</b> is a tModel reference that specifies
that keyName keyValue pairs given by subsequent addressLine elements, if
addressLine elements are present at all, are to be interpreted by the address
structure associated with the tModel that is referenced. For a description of
how to use tModels in order to give the addressLine list structure and meaning,
see Appendix D<i> Internationalization</i>.</p>
 * <p>Java class for address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="address">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}addressLine" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *       &lt;attribute name="useType" type="{urn:uddi-org:api_v3}useType" default="" />
 *       &lt;attribute name="sortCode" type="{urn:uddi-org:api_v3}sortCode" default="" />
 *       &lt;attribute name="tModelKey" type="{urn:uddi-org:api_v3}tModelKey" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address", propOrder = {
    "addressLine"
})
public class Address implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -8188040108029962551L;
	@XmlElement(required = true)
    protected List<AddressLine> addressLine;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;
    @XmlAttribute
    protected String useType;
    @XmlAttribute
    protected String sortCode;
    @XmlAttribute
    protected String tModelKey;

    /**
     * Gets the value of the addressLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressLine }
     * 
     * 
     */
    public List<AddressLine> getAddressLine() {
        if (addressLine == null) {
            addressLine = new ArrayList<AddressLine>();
        }
        return this.addressLine;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the useType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
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

    /**
     * Gets the value of the sortCode property.
     * The sortCode attribute is deprecated because of the guarantee of preserving the document order (see Section 4.5.3 Preservation of Document Order). In order to achieve this behavior, the data has just to be published in the desired order.
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortCode() {
        if (sortCode == null) {
            return "";
        } else {
            return sortCode;
        }
    }

    /**
     * Sets the value of the sortCode property.
     * The sortCode attribute is deprecated because of the guarantee of preserving the document order (see Section 4.5.3 Preservation of Document Order). In order to achieve this behavior, the data has just to be published in the desired order.
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortCode(String value) {
        this.sortCode = value;
    }

    /**
     * Gets the value of the tModelKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTModelKey() {
        return tModelKey;
    }

    /**
     * Sets the value of the tModelKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTModelKey(String value) {
        this.tModelKey = value;
    }

}
