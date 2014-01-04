
package org.apache.juddi.api_v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.sub_v3.GetSubscriptionResults;


/**
 * <p>Java class for syncSubscription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncSubscription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="get_subscriptionResultsList" type="{urn:uddi-org:sub_v3}get_subscriptionResults" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscription", propOrder = {
    "authInfo",
    "getSubscriptionResultsList"
})
public class SyncSubscription {

    protected String authInfo;
    @XmlElement(name = "get_subscriptionResultsList", nillable = true)
    protected List<GetSubscriptionResults> getSubscriptionResultsList;

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
     * Gets the value of the getSubscriptionResultsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getSubscriptionResultsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetSubscriptionResultsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GetSubscriptionResults }
     * 
     * 
     */
    public List<GetSubscriptionResults> getGetSubscriptionResultsList() {
        if (getSubscriptionResultsList == null) {
            getSubscriptionResultsList = new ArrayList<GetSubscriptionResults>();
        }
        return this.getSubscriptionResultsList;
    }

}
