
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for get_AllClerksResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get_AllClerksResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clerkList" type="{urn:juddi-apache-org:api_v3}clerkList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get_AllClerksResponse", propOrder = {
    "clerkList"
})
public class GetAllClerksResponse {

    @XmlElement(required = true, nillable = true)
    protected ClerkList clerkList;

    /**
     * Gets the value of the clerkList property.
     * 
     * @return
     *     possible object is
     *     {@link ClerkList }
     *     
     */
    public ClerkList getClerkList() {
        return clerkList;
    }

    /**
     * Sets the value of the clerkList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClerkList }
     *     
     */
    public void setClerkList(ClerkList value) {
        this.clerkList = value;
    }

}
