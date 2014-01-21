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
package org.uddi.api_v2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for URLType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="URLType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="mailto"/>
 *     &lt;enumeration value="http"/>
 *     &lt;enumeration value="https"/>
 *     &lt;enumeration value="ftp"/>
 *     &lt;enumeration value="fax"/>
 *     &lt;enumeration value="phone"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "URLType")
@XmlEnum
public enum URLType {

    @XmlEnumValue("mailto")
    MAILTO("mailto"),
    @XmlEnumValue("http")
    HTTP("http"),
    @XmlEnumValue("https")
    HTTPS("https"),
    @XmlEnumValue("ftp")
    FTP("ftp"),
    @XmlEnumValue("fax")
    FAX("fax"),
    @XmlEnumValue("phone")
    PHONE("phone"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    URLType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static URLType fromValue(String v) {
        for (URLType c: URLType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
