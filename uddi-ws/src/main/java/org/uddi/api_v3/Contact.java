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
 * <h4 style="margin-left:0in;text-indent:0in">3.3.2.6 contact</h4>

<p class="MsoBodyText">The contact structure records contact information for a
person or a job role within the businessEntity so that someone who finds the
information can make human contact for any purpose. This information consists
of one or more optional elements, along with a person’s name. Contact
information exists by containment relationship alone; the contact structure
does not provide keys for tracking individual contact instances.</p>

<p class="MsoBodyText"><img src="http://uddi.org/pubs/uddi-v3.0.2-20041019_files/image012.jpg" border="0" width="508" height="330"></p>

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
  <p class="MsoNormal">useType&nbsp; </p>
  </td>
  <td style="border-top:none;border-left:none;border-bottom:solid black 1.0pt;
  border-right:solid black 1.0pt;padding:0in 5.4pt 0in 5.4pt" valign="top">
  <p class="MsoNormal">optional&nbsp; </p>
  </td>
 </tr>
</tbody></table>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">The <b>useType</b> attribute is used to describe the type
of contact in unstructured text. Suggested examples include "technical
questions", "technical contact", "establish account", "sales
contact", etc.</p>

<p class="MsoBodyText"><b>description</b> is used to describe how the contact
information should be used. Publishing several descriptions, e.g. in different
languages, is supported. To signify the language in which the descriptions are
expressed, they MAY carry <b>xml:lang</b> values.</p>

<p class="MsoBodyText"><b>personName</b> is the name of the person or name of the
job role supporting the contact. Publishing several names, e.g. for
romanization purposes, is supported.</p>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText"><b>Attributes</b></p>

<table class="specTable" style="margin-left:
 .5in;border-collapse:collapse;border:none" border="1" cellpadding="0" cellspacing="0">
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
</tbody></table>

<p class="MsoBodyText">&nbsp;</p>

<p class="MsoBodyText">In order to signify the contextual language (if any) in
which a given name is expressed in (such as the language into which a name has
been romanized), it SHOULD carry the <b>xml:lang</b> attribute See Section <a href="#_Ref8977786 ">3.3.2.3</a> <i>name</i> for details on using xml:lang
values in name elements. There is no restriction on the number of personNames
or what xml:lang value each may have. An example for a role might be:</p>

<p class="codeSample">&lt;contact useType="Technical support"&gt;</p>

<p class="codeSample">&nbsp;&nbsp;&nbsp; &lt;personName&gt;Administrator&lt;/personName&gt;</p>

<p class="codeSample">&nbsp;&nbsp;&nbsp; …</p>

<p class="codeSample">&lt;/contact&gt;</p>

<p class="MsoBodyText"><b>&nbsp;</b></p>

<p class="MsoBodyText"><b>phone</b> is used to hold telephone numbers for the
contact. This element MAY be adorned with an optional <b>useType</b> attribute
for descriptive purposes.</p>

<p class="MsoBodyText"><b>email</b> is the email address for the contact. This
element MAY be adorned with an optional <b>useType</b> attribute for
descriptive purposes.</p>

<p class="MsoBodyText"><b>address</b> is the postal address for the contact. </p>
 * <p>Java class for contact complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contact">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}personName" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}phone" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}email" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}address" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="useType" type="{urn:uddi-org:api_v3}useType" default="" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contact", propOrder = {
    "description",
    "personName",
    "phone",
    "email",
    "address"
})
public class Contact implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -714656423425450747L;
	protected List<Description> description;
    @XmlElement(required = true)
    protected List<PersonName> personName;
    protected List<Phone> phone;
    protected List<Email> email;
    protected List<Address> address;
    @XmlAttribute
    protected String useType;

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description }
     * 
     * 
     */
    public List<Description> getDescription() {
        if (description == null) {
            description = new ArrayList<Description>();
        }
        return this.description;
    }

    /**
     * Gets the value of the personName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonName }
     * 
     * 
     */
    public List<PersonName> getPersonName() {
        if (personName == null) {
            personName = new ArrayList<PersonName>();
        }
        return this.personName;
    }

    /**
     * Gets the value of the phone property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phone property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhone().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Phone }
     * 
     * 
     */
    public List<Phone> getPhone() {
        if (phone == null) {
            phone = new ArrayList<Phone>();
        }
        return this.phone;
    }

    /**
     * Gets the value of the email property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the email property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Email }
     * 
     * 
     */
    public List<Email> getEmail() {
        if (email == null) {
            email = new ArrayList<Email>();
        }
        return this.email;
    }

    /**
     * Gets the value of the address property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the address property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Address }
     * 
     * 
     */
    public List<Address> getAddress() {
        if (address == null) {
            address = new ArrayList<Address>();
        }
        return this.address;
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

}
