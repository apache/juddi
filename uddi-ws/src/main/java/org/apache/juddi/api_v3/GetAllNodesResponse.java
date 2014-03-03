
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for get_AllNodesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get_AllNodesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nodeList" type="{urn:juddi-apache-org:api_v3}nodeList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get_AllNodesResponse", propOrder = {
    "nodeList"
})
public class GetAllNodesResponse {

    @XmlElement(required = true, nillable = true)
    protected NodeList nodeList;

    /**
     * Gets the value of the nodeList property.
     * 
     * @return
     *     possible object is
     *     {@link NodeList }
     *     
     */
    public NodeList getNodeList() {
        return nodeList;
    }

    /**
     * Sets the value of the nodeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeList }
     *     
     */
    public void setNodeList(NodeList value) {
        this.nodeList = value;
    }

}
