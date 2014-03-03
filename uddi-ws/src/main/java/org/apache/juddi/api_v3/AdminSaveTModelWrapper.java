
package org.apache.juddi.api_v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.api_v3.TModel;


/**
 * <p>Java class for adminSave_tModelWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminSave_tModelWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="publisherID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tModel" type="{urn:uddi-org:api_v3}tModel" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminSave_tModelWrapper", propOrder = {
    "publisherID",
    "tModel"
})
public class AdminSaveTModelWrapper {

    protected String publisherID;
    @XmlElement(nillable = true)
    protected List<TModel> tModel;

    /**
     * Gets the value of the publisherID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisherID() {
        return publisherID;
    }

    /**
     * Sets the value of the publisherID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisherID(String value) {
        this.publisherID = value;
    }

    /**
     * Gets the value of the tModel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tModel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TModel }
     * 
     * 
     */
    public List<TModel> getTModel() {
        if (tModel == null) {
            tModel = new ArrayList<TModel>();
        }
        return this.tModel;
    }

}
