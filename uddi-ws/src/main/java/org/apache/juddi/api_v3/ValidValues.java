
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validValues complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validValues">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tModekKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validationClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validValues", propOrder = {
    "tModekKey",
    "validationClass"
})
public class ValidValues {

    protected String tModekKey;
    protected String validationClass;

    /**
     * Gets the value of the tModekKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTModekKey() {
        return tModekKey;
    }

    /**
     * Sets the value of the tModekKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTModekKey(String value) {
        this.tModekKey = value;
    }

    /**
     * Gets the value of the validationClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidationClass() {
        return validationClass;
    }

    /**
     * Sets the value of the validationClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidationClass(String value) {
        this.validationClass = value;
    }

}
