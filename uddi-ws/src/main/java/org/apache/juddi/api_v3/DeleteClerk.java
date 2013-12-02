
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for delete_Clerk complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="delete_Clerk">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authInfo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="clerkID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delete_Clerk", propOrder = {
    "authInfo",
    "clerkID"
})
public class DeleteClerk {

    @XmlElement(required = true)
    protected String authInfo;
    @XmlElement(required = true)
    protected String clerkID;

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
     * Gets the value of the clerkID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClerkID() {
        return clerkID;
    }

    /**
     * Sets the value of the clerkID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClerkID(String value) {
        this.clerkID = value;
    }

}
