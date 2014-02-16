
package org.uddi.api_v2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for get_bindingDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get_bindingDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v2}bindingKey" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="generic" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get_bindingDetail", propOrder = {
    "bindingKey"
})
public class GetBindingDetail {

    @XmlElement(required = true)
    protected List<String> bindingKey;
    @XmlAttribute(name = "generic", required = true)
    protected String generic="2.0";

    /**
     * Gets the value of the bindingKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bindingKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBindingKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBindingKey() {
        if (bindingKey == null) {
            bindingKey = new ArrayList<String>();
        }
        return this.bindingKey;
    }

    /**
     * Gets the value of the generic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneric() {
        return generic;
    }

    /**
     * Sets the value of the generic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneric(String value) {
        this.generic = value;
    }

}
