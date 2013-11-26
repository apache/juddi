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
 * · authInfo: This optional argument is an element that contains an
 * authentication token. Registries that wish to restrict who can perform an
 * inquiry in them typically require authInfo for this call.<br><br>
 *
 * · categoryBag: This is a list of category references in the form of
 * keyedReference elements and keyedReferenceGroup structures. The returned
 * businessList contains businessInfo elements matching all of the categories
 * passed (logical AND by default). Specifying the appropriate findQualifiers
 * can override this behavior. Matching rules for each can be found in Section
 * 5.1.7 Matching Rules for keyedReferences and keyedReferenceGroups.<br><br>
 *
 * · discoveryURLs: This is a list of discoveryURL structures to be matched
 * against the discoveryURL data associated with registered businessEntity
 * information. To search for URL without regard to useType attribute values,
 * omit the useType attribute or pass it as an empty attribute. If useType
 * values are included, the match occurs only on registered information that
 * matches both the useType and URL value. The returned businessList contains
 * businessInfo structures matching any of the URL's passed (logical OR).<br><br>
 *
 * · identifierBag: This is a list of business identifier references in the form
 * of keyedReference elements. The returned businessList contains businessInfo
 * structures matching any of the identifiers passed (logical OR by default).
 * Specifying the appropriate findQualifiers can override this behavior.
 * Matching rules can be found in Section 5.1.7 Matching Rules for
 * keyedReferences and keyedReferenceGroups.<br><br>
 *
 * · findQualifiers: This collection of findQualifier elements can be used to
 * alter the default behavior of search functionality. See the Section 5.1.4
 * Find Qualifiers, for more information.<br><br>
 *
 * · find_relatedBusinesses: This argument is an embedded inquiry and limits the
 * search results to those businesses that are related to a specified business
 * in a specified way. The result is comprised of an intersection of the
 * businesses located with this embedded inquiry and the businesses discovered
 * using the remaining inquiry criteria. The standard syntax and arguments for
 * find_relatedBusinesses apply here. Note that the authInfo argument to this
 * embedded find_relatedBusinesses argument is always ignored. Large result set
 * behavior involving the return of a listDescription does not apply within an
 * embedded argument. If the intermediate result set produced is too large, then
 * the overall query will return E_resultSetTooLarge with an indication that the
 * embedded query returned too many results. If an E_unsupported error occurs as
 * part of processing this embedded argument, it is propagated up to the
 * containing (calling) API. See Section 5.1.11 find_relatedBusinesses, for
 * further information.<br><br>
 *
 * · find_tModel: This argument provides an alternative or additional way of
 * specifying tModelKeys that are used to find businesses which have service
 * bindings with specific technical fingerprints as described above for the
 * tModelBag element. When specified, the find_tModel argument is treated as an
 * embedded inquiry that is performed prior to searching for businesses. The
 * tModelKeys found are those whose tModels match the criteria contained within
 * the find_tModel element. The tModelKeys found are added to the (possibly
 * empty) collection specified by the tModelBag prior to finding qualified
 * businesses. Note that the authInfo argument to this embedded find_tModel
 * argument is always ignored. Large result set behavior involving the return of
 * a listDescription does not apply within an embedded argument. If the
 * intermediate result set produced is too large, then the overall query will
 * return E_resultSetTooLarge with an indication that the embedded query
 * returned too many results. If an E_unsupported error occurs as part of
 * processing this embedded argument, it is propagated up to the containing
 * (calling) API.<br><br>
 *
 * · listHead: This optional integer value is used to indicate which item SHOULD
 * be returned as the head of the list. The client may request a subset of the
 * matching data by indicating which item in the resultant set constitutes the
 * beginning of the returned data. The use of the listDescription element is
 * mutually exclusive to the use of the truncated attribute that simply
 * indicates a truncated result list in the Inquiry APIs. See Section 5.1.5 Use
 * of listDescription, for a detailed description of the listHead argument.<br><br>
 *
 * · maxRows: This optional integer value allows the requesting program to limit
 * the number of results returned. This argument can be used in conjunction with
 * the listHead argument.<br><br>
 *
 * · name: This optional collection of string values represents one or more
 * names potentially qualified with xml:lang attributes. Since "exactMatch" is
 * the default behavior, the value supplied for the name argument must be an
 * exact match. If the "approximateMatch" findQualifier is used together with an
 * appropriate wildcard character in the name, then any businessEntity matching
 * this name with wildcards and the other criteria will be referenced in the
 * results. For more on wildcard matching, see Section 5.1.6 About Wildcards.
 * The businessList returned contains businessInfo structures for businesses
 * whose name matches the value(s) passed (lexical-order match – i.e., leftmost
 * in left-to-right languages). If multiple name values are passed, the match
 * occurs on a logical OR basis. Each name MAY be marked with an xml:lang
 * adornment. If a language markup is specified, the search results report a
 * match only on those entries that match both the name value and language
 * criteria. The match on language is a leftmost case-insensitive comparison of
 * the characters supplied. This allows one to find all businesses whose name
 * begins with an "A" and are expressed in any dialect of French, for example.
 * Values which can be passed in the language criteria adornment MUST obey the
 * rules governing the xml:lang data type as defined in Section 3.3.2.3 name.<br><br>
 *
 * · tModelBag: Every Web service instance exposed by a registered
 * businessEntity is represented in UDDI by a bindingTemplate contained within
 * the businessEntity. Each bindingTemplate contains a collection of tModel
 * references called its "technical fingerprint" that specifies its type. The
 * tModelBag argument is a collection of tModelKey elements specifying that the
 * search results are to be limited to businesses that expose Web services with
 * technical fingerprints that match.<br><br>
 *
 * If a find_tModel argument is specified (see above), it is treated as an
 * embedded inquiry. The tModelKeys returned as a result of this embedded
 * find_tModel argument are used as if they had been supplied in a tModelBag
 * argument. Changing the order of the keys in the collection or specifying the
 * same tModelKey more than once does not change the behavior of the find.  *
 * By default, only bindingTemplates that contain all of the tModelKeys in the
 * technical fingerprint match (logical AND). Specifying appropriate
 * findQualifiers can override this behavior so that bindingTemplates containing
 * any of the specified tModelKeys match (logical OR).<br><br>
 * <p>Java class for find_business complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="find_business">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}findQualifiers" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}name" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}identifierBag" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}categoryBag" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}tModelBag" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_tModel" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}discoveryURLs" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_relatedBusinesses" minOccurs="0"/>
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
@XmlType(name = "find_business", propOrder = {
    "authInfo",
    "findQualifiers",
    "name",
    "identifierBag",
    "categoryBag",
    "tModelBag",
    "findTModel",
    "discoveryURLs",
    "findRelatedBusinesses"
})
public class FindBusiness implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 1590618928847932109L;
	protected String authInfo;
    protected FindQualifiers findQualifiers;
    protected List<Name> name;
    protected IdentifierBag identifierBag;
    protected CategoryBag categoryBag;
    protected TModelBag tModelBag;
    @XmlElement(name = "find_tModel")
    protected FindTModel findTModel;
    protected DiscoveryURLs discoveryURLs;
    @XmlElement(name = "find_relatedBusinesses")
    protected FindRelatedBusinesses findRelatedBusinesses;
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the name property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Name }
     * 
     * 
     */
    public List<Name> getName() {
        if (name == null) {
            name = new ArrayList<Name>();
        }
        return this.name;
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
     * Gets the value of the tModelBag property.
     * 
     * @return
     *     possible object is
     *     {@link TModelBag }
     *     
     */
    public TModelBag getTModelBag() {
        return tModelBag;
    }

    /**
     * Sets the value of the tModelBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link TModelBag }
     *     
     */
    public void setTModelBag(TModelBag value) {
        this.tModelBag = value;
    }

    /**
     * Gets the value of the findTModel property.
     * 
     * @return
     *     possible object is
     *     {@link FindTModel }
     *     
     */
    public FindTModel getFindTModel() {
        return findTModel;
    }

    /**
     * Sets the value of the findTModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindTModel }
     *     
     */
    public void setFindTModel(FindTModel value) {
        this.findTModel = value;
    }

    /**
     * Gets the value of the discoveryURLs property.
     * 
     * @return
     *     possible object is
     *     {@link DiscoveryURLs }
     *     
     */
    public DiscoveryURLs getDiscoveryURLs() {
        return discoveryURLs;
    }

    /**
     * Sets the value of the discoveryURLs property.
     * 
     * @param value
     *     allowed object is
     *     {@link DiscoveryURLs }
     *     
     */
    public void setDiscoveryURLs(DiscoveryURLs value) {
        this.discoveryURLs = value;
    }

    /**
     * Gets the value of the findRelatedBusinesses property.
     * 
     * @return
     *     possible object is
     *     {@link FindRelatedBusinesses }
     *     
     */
    public FindRelatedBusinesses getFindRelatedBusinesses() {
        return findRelatedBusinesses;
    }

    /**
     * Sets the value of the findRelatedBusinesses property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindRelatedBusinesses }
     *     
     */
    public void setFindRelatedBusinesses(FindRelatedBusinesses value) {
        this.findRelatedBusinesses = value;
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
