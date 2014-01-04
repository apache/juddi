
package org.apache.juddi.api_v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for clientSubscriptionInfoDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="clientSubscriptionInfoDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clientSubscriptionInfo" type="{urn:juddi-apache-org:api_v3}clientSubscriptionInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clientSubscriptionInfoDetail", propOrder = {
    "clientSubscriptionInfo"
})
public class ClientSubscriptionInfoDetail {

    @XmlElement(nillable = true)
    protected List<ClientSubscriptionInfo> clientSubscriptionInfo;

    /**
     * Gets the value of the clientSubscriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clientSubscriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClientSubscriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClientSubscriptionInfo }
     * 
     * 
     */
    public List<ClientSubscriptionInfo> getClientSubscriptionInfo() {
        if (clientSubscriptionInfo == null) {
            clientSubscriptionInfo = new ArrayList<ClientSubscriptionInfo>();
        }
        return this.clientSubscriptionInfo;
    }

}
