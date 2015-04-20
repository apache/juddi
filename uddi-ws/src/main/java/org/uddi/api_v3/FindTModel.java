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


/**
 * The find_tModel API is used to find UDDI tModel elements. The find_tModel API
 * call returns a list of tModel entries that match a set of specific criteria.
 * The response consists of summary information about registered tModel data
 * returned in a tModelList structure.
 *<Br><Br>
 * · authInfo: This optional argument is an element that contains an
 * authentication token. Registries that wish to restrict who can perform an
 * inquiry in them typically require authInfo for this call.
 *<Br><Br>
 * · categoryBag: This is a list of category references. The returned tModelList
 * contains tModelInfo elements whose associated tModels match all of the
 * categories passed (logical AND by default). Specifying the appropriate
 * findQualifiers can override this behavior. Matching rules for the use of
 * keyedReferences and keyedReferenceGroups are described in Section 5.1.7
 * Matching Rules for keyedReferences and keyedReferenceGroups.
 *<Br><Br>
 * · findQualifiers: This collection of findQualifier elements is used to alter
 * the default behavior of search functionality. See Section 5.1.4 Find
 * Qualifiers for more information.
 *<Br><Br>
 * · identifierBag This is a list of identifier references in the form of
 * keyedReference elements. The returned tModelList contains tModelInfo elements
 * whose associated tModels match any of the identifiers passed (logical OR by
 * default). Specifying the appropriate findQualifiers can override this
 * behavior. Matching rules are described in Section 5.1.7 Matching Rules for
 * keyedReferences and keyedReferenceGroups.
 *<Br><Br>
 * · listHead: This optional integer value is used to indicate which item SHOULD
 * be returned as the head of the list. The client may request a subset of the
 * matching data by indicating which item in the resultant set constitutes the
 * beginning of the returned data. The use of the listDescription element is
 * mutually exclusive to the use of the truncated attribute that simply
 * indicates a truncated result list in the Inquiry APIs. See Section 5.1.5 Use
 * of listDescription, for a detailed description of the listHead argument.
 *<Br><Br>
 * · maxRows: This optional integer value allows the requesting program to limit
 * the number of results returned. This argument can be used in conjunction with
 * the listHead argument.
 *<Br><Br>
 * · name: This string value represents the name of the tModel elements to be
 * found. Since tModel data only has a single name, only a single name may be
 * passed. The argument must match exactly since "exactMatch" is the default
 * behavior, but if the "approximateMatch" findQualifier is used together with
 * the appropriate wildcard character, then matching is done according to
 * wildcard rules. See Section 5.1.6 About Wildcards for additional information.
 * The name MAY be marked with an xml:lang adornment. If a language markup is
 * specified, the search results report a match only on those entries that match
 * both the name value and language criteria. The match on language is a
 * leftmost case-insensitive comparison of the characters supplied. This allows
 * one to find all tModels whose name begins with an "A" and are expressed in
 * any dialect of French, for example. Values which can be passed in the
 * language criteria adornment MUST obey the rules governing the xml:lang data
 * type as defined in Section 3.3.2.3 name.<Br><Br>
 * <p>Java class for find_tModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="find_tModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}findQualifiers" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}name" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}identifierBag" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}categoryBag" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="maxRows" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="listHead" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "find_tModel", propOrder = {
    "authInfo",
    "findQualifiers",
    "name",
    "identifierBag",
    "categoryBag"
})
public class FindTModel implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -8389225501417978080L;
	protected String authInfo;
    protected FindQualifiers findQualifiers;
    protected Name name;
    protected IdentifierBag identifierBag;
    protected CategoryBag categoryBag;
    @XmlAttribute
    protected Integer maxRows;
    @XmlAttribute
    protected Integer listHead;

    /**
     * Gets the value of the authInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthInfo() {
        return authInfo;
    }

    /**
     * Sets the value of the authInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthInfo(String value) {
        this.authInfo = value;
    }

    /**
     * Gets the value of the findQualifiers property.
     * 
     * @return
     *     possible object is
     *     {@link FindQualifiers }
     *     
     */
    public FindQualifiers getFindQualifiers() {
        return findQualifiers;
    }

    /**
     * Sets the value of the findQualifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindQualifiers }
     *     
     */
    public void setFindQualifiers(FindQualifiers value) {
        this.findQualifiers = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setName(Name value) {
        this.name = value;
    }

    /**
     * Gets the value of the identifierBag property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierBag }
     *     
     */
    public IdentifierBag getIdentifierBag() {
        return identifierBag;
    }

    /**
     * Sets the value of the identifierBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierBag }
     *     
     */
    public void setIdentifierBag(IdentifierBag value) {
        this.identifierBag = value;
    }

    /**
     * Gets the value of the categoryBag property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryBag }
     *     
     */
    public CategoryBag getCategoryBag() {
        return categoryBag;
    }

    /**
     * Sets the value of the categoryBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryBag }
     *     
     */
    public void setCategoryBag(CategoryBag value) {
        this.categoryBag = value;
    }

    /**
     * Gets the value of the maxRows property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxRows() {
        return maxRows;
    }

    /**
     * Sets the value of the maxRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxRows(Integer value) {
        this.maxRows = value;
    }

    /**
     * Gets the value of the listHead property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getListHead() {
        return listHead;
    }

    /**
     * Sets the value of the listHead property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setListHead(Integer value) {
        this.listHead = value;
    }

}
