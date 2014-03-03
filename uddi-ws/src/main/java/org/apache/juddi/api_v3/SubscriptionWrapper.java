
package org.apache.juddi.api_v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.sub_v3.Subscription;


/**
 * <p>Java class for subscriptionWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subscriptionWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="publisherIdOrUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscription" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscriptionWrapper", propOrder = {
    "publisherIdOrUsername",
    "subscription"
})
public class SubscriptionWrapper {

    @XmlElement(required = true)
    protected String publisherIdOrUsername;
    @XmlElement(namespace = "urn:uddi-org:sub_v3", required = true)
    protected List<Subscription> subscription;

    /**
     * Gets the value of the publisherIdOrUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisherIdOrUsername() {
        return publisherIdOrUsername;
    }

    /**
     * Sets the value of the publisherIdOrUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisherIdOrUsername(String value) {
        this.publisherIdOrUsername = value;
    }

    /**
     * Gets the value of the subscription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subscription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubscription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Subscription }
     * 
     * 
     */
    public List<Subscription> getSubscription() {
        if (subscription == null) {
            subscription = new ArrayList<Subscription>();
        }
        return this.subscription;
    }

}
