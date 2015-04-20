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
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>completionStatus</i></b>: This optional argument lets the
publisher restrict the result set to only those relationships that have the
specified status value.&nbsp; Assertion status is a calculated result based on the
sum total of assertions made by the individuals that control specific business
registrations.&nbsp; When no completionStatus element is provided, all assertions
involving the businesses that the publisher owns are retrieved, without regard
to the completeness of the relationship.&nbsp; completionStatus MUST contain one of
the following values</p>

<p class="MsoNormal" style="margin-left:1.5in;text-indent:-.25in"><span style="font-family:&quot;Courier New&quot;">o<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>status:complete</b>: Passing this value causes only the
publisher assertions that are complete to be returned.&nbsp; Each businessEntity
listed in assertions that are complete has a visible relationship that directly
reflects the data in a complete assertion (as described in the
find_relatedBusinesses API).</p>

<p class="MsoNormal" style="margin-left:1.5in;text-indent:-.25in"><span style="font-family:&quot;Courier New&quot;">o<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>status:toKey_incomplete</b>: Passing this value causes only
those publisher assertions where the party who controls the businessEntity
referenced by the toKey value in an assertion, has not made a matching
assertion, to be listed.</p>

<p class="MsoNormal" style="margin-left:1.5in;text-indent:-.25in"><span style="font-family:&quot;Courier New&quot;">o<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>status:fromKey_incomplete</b>: Passing this value causes only
those publisher assertions where the party who controls the businessEntity
referenced by the fromKey value in an assertion, has not made a matching
assertion, to be listed.</p>

<p class="MsoNormal" style="margin-left:1.5in;text-indent:-.25in"><span style="font-family:&quot;Courier New&quot;">o<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>status:both_incomplete</b>. This status value, however, is only
applicable to the context of UDDI subscription and SHOULD not be present as
part of a response to a get_assertionStatusReport request.</p>  
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

    /**
     * causes only the publisher assertions that are complete to be returned
     */
    @XmlEnumValue("status:complete")
    STATUS_COMPLETE("status:complete"),
    /**
     * causes only those publisher assertions where the party who controls the businessEntity referenced by the fromKey value in an assertion, has not made a matching assertion, to be listed.
     */
    @XmlEnumValue("status:fromKey_incomplete")
    STATUS_FROM_KEY_INCOMPLETE("status:fromKey_incomplete"),
    /**
     *  causes only those publisher assertions where the party who controls the businessEntity referenced by the toKey value in an assertion, has not made a matching assertion, to be listed.
     */
    @XmlEnumValue("status:toKey_incomplete")
    STATUS_TO_KEY_INCOMPLETE("status:toKey_incomplete"),
    /**
     * only applicable to the context of UDDI subscription and SHOULD not be present as part of a response to a get_assertionStatusReport request.
     *  When appearing in an assertionStatusItem of a subscriptionResultsList, status:both_incomplete indicates that the publisher assertion embedded in the <b>assertionStatusItem has been deleted from both ends. </b>
     */
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
