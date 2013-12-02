
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.repl_v3.ReplicationConfiguration;


/**
 * <p>Java class for get_ReplicationNodesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get_ReplicationNodesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:repl_v3}replicationConfiguration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get_ReplicationNodesResponse", propOrder = {
    "replicationConfiguration"
})
public class GetReplicationNodesResponse {

    @XmlElement(namespace = "urn:uddi-org:repl_v3", required = true)
    protected ReplicationConfiguration replicationConfiguration;

    /**
     * Gets the value of the replicationConfiguration property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationConfiguration }
     *     
     */
    public ReplicationConfiguration getReplicationConfiguration() {
        return replicationConfiguration;
    }

    /**
     * Sets the value of the replicationConfiguration property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationConfiguration }
     *     
     */
    public void setReplicationConfiguration(ReplicationConfiguration value) {
        this.replicationConfiguration = value;
    }

}
