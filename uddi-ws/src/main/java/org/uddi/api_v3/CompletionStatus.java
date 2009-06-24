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
 * <p>Java class for completionStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="completionStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;maxLength value="32"/>
 *     &lt;whiteSpace value="collapse"/>
 *     &lt;enumeration value="status:complete"/>
 *     &lt;enumeration value="status:fromKey_incomplete"/>
 *     &lt;enumeration value="status:toKey_incomplete"/>
 *     &lt;enumeration value="status:both_incomplete"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "completionStatus")
@XmlEnum
public enum CompletionStatus implements Serializable{

    @XmlEnumValue("status:complete")
    STATUS_COMPLETE("status:complete"),
    @XmlEnumValue("status:fromKey_incomplete")
    STATUS_FROM_KEY_INCOMPLETE("status:fromKey_incomplete"),
    @XmlEnumValue("status:toKey_incomplete")
    STATUS_TO_KEY_INCOMPLETE("status:toKey_incomplete"),
    @XmlEnumValue("status:both_incomplete")
    STATUS_BOTH_INCOMPLETE("status:both_incomplete");
    private final String value;

    CompletionStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CompletionStatus fromValue(String v) {
        for (CompletionStatus c: CompletionStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
