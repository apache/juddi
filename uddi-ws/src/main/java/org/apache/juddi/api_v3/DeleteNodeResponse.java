
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.api_v3.DispositionReport;


/**
 * <p>Java class for delete_NodeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="delete_NodeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dispositionReport" type="{urn:uddi-org:api_v3}dispositionReport"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delete_NodeResponse", propOrder = {
    "dispositionReport"
})
public class DeleteNodeResponse {

    @XmlElement(required = true)
    protected DispositionReport dispositionReport;

    /**
     * Gets the value of the dispositionReport property.
     * 
     * @return
     *     possible object is
     *     {@link DispositionReport }
     *     
     */
    public DispositionReport getDispositionReport() {
        return dispositionReport;
    }

    /**
     * Sets the value of the dispositionReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link DispositionReport }
     *     
     */
    public void setDispositionReport(DispositionReport value) {
        this.dispositionReport = value;
    }

}
