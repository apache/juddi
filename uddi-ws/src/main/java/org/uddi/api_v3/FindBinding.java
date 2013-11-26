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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * The find_binding API is used to find UDDI bindingTemplate elements. The
 * find_binding API call returns a bindingDetail that contains zero or more
 * bindingTemplate structures matching the criteria specified in the argument
 * list.
 * * <br><br>
 * authInfo: This optional argument is an element that contains an
 * authentication token. Registries that wish to restrict who can perform an
 * inquiry typically require authInfo for this call.
 * <br><br>
 * · categoryBag: This optional argument is a list of category references in the
 * form of keyedReference elements and keyedReferenceGroup structures. When
 * used, the returned bindingDetail for this API will contain elements matching
 * all of the categories passed (logical AND by default). Specifying the
 * appropriate findQualifiers can override this behavior. Matching rules for
 * each can be found in Section 5.1.7 Matching Rules for keyedReferences and
 * keyedReferenceGroups.
 * <br><br>
 * · findQualifiers: This optional collection of findQualifier elements can be
 * used to alter the default behavior of search functionality. See Section 5.1.4
 * Find Qualifiers, for more information.
 * <br><br>
 * · find_tModel: This argument provides an alternative or additional way of
 * specifying tModelKeys that are to be used to find the bindingTemplate
 * elements. When specified, the find_tModel argument is treated as an embedded
 * inquiry that is performed prior to searching for bindingTemplate elements.
 * The tModelKeys found are those whose tModels match the criteria contained
 * within the find_tModel element. The tModelKeys found are added to the
 * (possibly empty) collection specified by the tModelBag prior to finding
 * qualified bindingTemplates. Note that the authInfo argument to this embedded
 * find_tModel argument is always ignored. Large result set behavior involving
 * the return of a listDescription does not apply within an embedded argument.
 * If the intermediate result set produced is too large, then the overall query
 * will return E_resultSetTooLarge with an indication that the embedded query
 * returned too many results. If an E_unsupported error occurs as part of
 * processing this embedded argument, it is propagated up to the containing
 * (calling) API.
 * <br><br>
 * · listHead: This optional integer value is used to indicate which item SHOULD
 * be returned as the head of the list. The client may request a subset of the
 * matching data by indicating which item in the resultant set constitutes the
 * beginning of the returned data. The use of the listDescription element is
 * mutually exclusive to the use of the truncated attribute that simply
 * indicates a truncated result list in the Inquiry APIs. See Section 5.1.5 Use
 * of listDescription, for a detailed description of the listHead argument.
 * <br><br>
 * · maxRows: This optional integer value allows the requesting program to limit
 * the number of results returned. This argument can be used in conjunction with
 * the listHead argument.
 * <br><br>
 * · serviceKey: This optional uddi_key is used to specify a particular instance
 * of a businessService element in the registered data. Only bindings in the
 * specific businessService data identified by the serviceKey passed are
 * searched. When it is either omitted or specified as empty (i.e.,
 * serviceKey=""), this indicates that all businessServices are to be searched
 * for bindings that meet the other criteria supplied without regard to the
 * service that provides them, and "projected" services are suppressed.
 * <br><br>
 * · tModelBag: This collection of tModelKey elements represent in part or in
 * whole the technical fingerprint of the bindingTemplate structures for which
 * the search is being performed. At least one of either a tModelBag or a
 * find_tModel argument SHOULD be supplied, unless a categoryBag based search is
 * being used.
 * <br><br>
 * If a find_tModel argument is specified (see above), it is treated as an
 * embedded inquiry. The tModelKeys returned as a result of this embedded
 * find_tModel argument are used as if they had been supplied in a tModelBag
 * argument. Changing the order of the keys in the collection or specifying the
 * same tModelKey more than once does not change the behavior of the find.
 * <br><br>
 * By default, only bindingTemplates that have a technical fingerprint
 * containing all of the supplied tModelKeys match (logical AND). Specifying
 * appropriate findQualifiers can override this behavior so that
 * bindingTemplates with a technical fingerprint containing any of the specified
 * tModelKeys are returned (logical OR).
 * <br><br>
 * <p>Java class for find_binding complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="find_binding">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}findQualifiers" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}tModelBag" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_tModel" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}categoryBag" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="maxRows" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="serviceKey" type="{urn:uddi-org:api_v3}serviceKey" />
 *       &lt;attribute name="listHead" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "find_binding", propOrder = {
    "authInfo",
    "findQualifiers",
    "tModelBag",
    "findTModel",
    "categoryBag"
})
public class FindBinding implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 5788351026931169344L;
	protected String authInfo;
    protected FindQualifiers findQualifiers;
    protected TModelBag tModelBag;
    @XmlElement(name = "find_tModel")
    protected FindTModel findTModel;
    protected CategoryBag categoryBag;
    @XmlAttribute
    protected Integer maxRows;
    @XmlAttribute
    protected String serviceKey;
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
     * Gets the value of the serviceKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceKey() {
        return serviceKey;
    }

    /**
     * Sets the value of the serviceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceKey(String value) {
        this.serviceKey = value;
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