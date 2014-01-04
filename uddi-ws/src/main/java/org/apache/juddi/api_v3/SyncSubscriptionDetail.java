
package org.apache.juddi.api_v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.sub_v3.SubscriptionResultsList;


/**
 * <p>Java class for syncSubscriptionDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syncSubscriptionDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriptionResultsList" type="{urn:uddi-org:sub_v3}subscriptionResultsList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscriptionDetail", propOrder = {
    "subscriptionResultsList"
})
public class SyncSubscriptionDetail {

    @XmlElement(nillable = true)
    protected List<SubscriptionResultsList> subscriptionResultsList;

    /**
     * Gets the value of the subscriptionResultsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subscriptionResultsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubscriptionResultsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubscriptionResultsList }
     * 
     * 
     */
    public List<SubscriptionResultsList> getSubscriptionResultsList() {
        if (subscriptionResultsList == null) {
            subscriptionResultsList = new ArrayList<SubscriptionResultsList>();
        }
        return this.subscriptionResultsList;
    }

}
