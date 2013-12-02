
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for syncSubscriptionDetailResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncSubscriptionDetailResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="syncSubscriptionDetail" type="{urn:juddi-apache-org:api_v3}syncSubscriptionDetail"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscriptionDetailResponse", propOrder = {
    "syncSubscriptionDetail"
})
public class SyncSubscriptionDetailResponse {

    @XmlElement(required = true, nillable = true)
    protected SyncSubscriptionDetail syncSubscriptionDetail;

    /**
     * Gets the value of the syncSubscriptionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link SyncSubscriptionDetail }
     *     
     */
    public SyncSubscriptionDetail getSyncSubscriptionDetail() {
        return syncSubscriptionDetail;
    }

    /**
     * Sets the value of the syncSubscriptionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncSubscriptionDetail }
     *     
     */
    public void setSyncSubscriptionDetail(SyncSubscriptionDetail value) {
        this.syncSubscriptionDetail = value;
    }

}
