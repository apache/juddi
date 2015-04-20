/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.uddi.api_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * The overviewURL appears as a child of the overviewDoc, which appears twice in
 * the UDDI information model, once with tModel element and once with
 * tModelInstanceInfo element. There are two conventions established, "text" and
 * "wsdlInterface".
 * <p>Java class for overviewURL complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="overviewURL">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:uddi-org:api_v3>validationTypeAnyURI4096">
 *       &lt;attribute name="useType" type="{urn:uddi-org:api_v3}useType" default="" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "overviewURL", propOrder = {
    "value"
})
public class OverviewURL implements Serializable {

    /**
     * default const
     */
    public OverviewURL() {
    }

    /**
     * convenience const
     *
     * @param val
     * @param type There are two conventions established, "text" and
     * "wsdlInterface".
     */
    public OverviewURL(String val, String type) {
        value = val;
        useType = type;
    }
    @XmlTransient
    private static final long serialVersionUID = 7142971435321837783L;
    @XmlValue
    protected String value;
    @XmlAttribute
    protected String useType;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the useType property. There are two conventions
     * established, "text" and "wsdlInterface".
     *
     * @return possible object is {@link String }
     *
     */
    public String getUseType() {
        if (useType == null) {
            return "";
        } else {
            return useType;
        }
    }

    /**
     * Sets the value of the useType property.
     *
     * @param value allowed object is {@link String } There are two conventions
     * established, "text" and "wsdlInterface".
     *
     */
    public void setUseType(String value) {
        this.useType = value;
    }
}
