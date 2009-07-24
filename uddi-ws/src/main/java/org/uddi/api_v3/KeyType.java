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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for keyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="keyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="businessKey"/>
 *     &lt;enumeration value="tModelKey"/>
 *     &lt;enumeration value="serviceKey"/>
 *     &lt;enumeration value="bindingKey"/>
 *     &lt;enumeration value="subscriptionKey"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "keyType")
@XmlEnum
public enum KeyType implements Serializable{

    @XmlEnumValue("businessKey")
    BUSINESS_KEY("businessKey"),
    @XmlEnumValue("tModelKey")
    T_MODEL_KEY("tModelKey"),
    @XmlEnumValue("serviceKey")
    SERVICE_KEY("serviceKey"),
    @XmlEnumValue("bindingKey")
    BINDING_KEY("bindingKey"),
    @XmlEnumValue("subscriptionKey")
    SUBSCRIPTION_KEY("subscriptionKey");
    private final String value;

    KeyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeyType fromValue(String v) {
        for (KeyType c: KeyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
