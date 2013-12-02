
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for syncSubscriptionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncSubscriptionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="syncSubscription" type="{urn:juddi-apache-org:api_v3}syncSubscription" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscriptionRequest", propOrder = {
    "syncSubscription"
})
public class SyncSubscriptionRequest {

    protected SyncSubscription syncSubscription;

    /**
     * Gets the value of the syncSubscription property.
     * 
     * @return
     *     possible object is
     *     {@link SyncSubscription }
     *     
     */
    public SyncSubscription getSyncSubscription() {
        return syncSubscription;
    }

    /**
     * Sets the value of the syncSubscription property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncSubscription }
     *     
     */
    public void setSyncSubscription(SyncSubscription value) {
        this.syncSubscription = value;
    }

}
