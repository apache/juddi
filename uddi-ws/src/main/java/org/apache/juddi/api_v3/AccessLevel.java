
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccessLevel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccessLevel">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="READ"/>
 *     &lt;enumeration value="WRITE"/>
 *     &lt;enumeration value="OWN"/>
 *     &lt;enumeration value="CREATE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccessLevel")
@XmlEnum
public enum AccessLevel {

    NONE((short)0),
    READ((short)1),
    WRITE((short)2),
    OWN((short)3),
    CREATE((short)4);

    public String value() {
        return name();
    }

    public static AccessLevel fromValue(String v) {
        return valueOf(v);
    }
    
    private final short value;
    
    private AccessLevel(short val){
        value=val;
    }
    
    public short getLevel(){
        return value;
    }
}
