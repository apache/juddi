/*
 * Copyright 2013 The Apache Software Foundation.
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
 */
package org.apache.juddi.v3.client.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.TModelInstanceDetails;

/**
 * Compares two UDDI TModelInstanceDetails, searching for a specific tModel key,
 * then parsing to the selected data type, then comparing.
 *
 * @author Alex O'Ree
 */
public class TModelInstanceDetailsComparator {

    /**
     *
     * @param TModelKey for TModelInstanceInfo to use for comparison
     * @param number if true, the InstanceDetails.InstanceParms will be treated
     * like a number
     * @param XMLdate if true, the InstanceDetails.InstanceParms will be treated
     * like a XML Date
     * @param XMLduration if true, the InstanceDetails.InstanceParms will be
     * treated like a XML Gregorian Calendar
     * @see Duration
     * @see XMLGregorianCalendar
     * @throws DatatypeConfigurationException
     */
    public TModelInstanceDetailsComparator(String TModelKey, boolean number, boolean XMLdate, boolean XMLduration) throws DatatypeConfigurationException, IllegalArgumentException {
        if (TModelKey == null || TModelKey.length() == 0) {
            throw new IllegalArgumentException();
        }
        compareField = TModelKey;
        if (!number && !XMLdate && !XMLduration) {
            throw new IllegalArgumentException("only one data type can be selected");
        }
        if (number && XMLdate && !XMLduration) {
            throw new IllegalArgumentException("only one data type can be selected");
        }
        if (number && !XMLdate && XMLduration) {
            throw new IllegalArgumentException("only one data type can be selected");
        }
        if (!number && XMLdate && XMLduration) {
            throw new IllegalArgumentException("only one data type can be selected");
        }
        if (number && XMLdate && XMLduration) {
            throw new IllegalArgumentException("only one data type can be selected");
        }
        fac = DatatypeFactory.newInstance();
        isNumber = number;
        isDate = XMLdate;
        isDuration = XMLduration;
    }
    DatatypeFactory fac = null;
    String compareField = null;
    boolean isNumber = false;
    boolean isDate = false;
    boolean isDuration = false;

    /**
     * Compares two non-null instances of TModelInstanceDetails by only
     * comparing the field designated from the constructor. It will also cast or
     * parse TModelInstanceDetails[i].InstanceDetails[k].InstanceParms to the
     * selected data type double, XMLGregorgian or Duration, using that as a
     * basis for comparison. If a parsing error occurs, an exception will be
     * thrown.
     *
     * @param lhs
     * @param rhs
     * @return less than 0 if lhs &lt; rhs, greater than 0 if lhs &gt; rhs.
     * @throws IllegalArgumentException if the tModel key to search for is
     * missing, if either sides are null
     * @throws ArrayIndexOutOfBoundsException if the values were found but could
     * not be compared
     */
    public int compare(TModelInstanceDetails lhs, TModelInstanceDetails rhs) throws IllegalArgumentException, NumberFormatException, NullPointerException, ArrayIndexOutOfBoundsException {
        if (lhs == null) {
            throw new IllegalArgumentException("lhs");
        }
        if (rhs == null) {
            throw new IllegalArgumentException("rhs");
        }
        if (lhs.getTModelInstanceInfo().isEmpty() || rhs.getTModelInstanceInfo().isEmpty()) {
            throw new IllegalArgumentException("no data to compare");
        }
        InstanceDetails lhsc = null;
        InstanceDetails rhsc = null;
        for (int i = 0; i < lhs.getTModelInstanceInfo().size(); i++) {
            if (lhs.getTModelInstanceInfo().get(i).getTModelKey().equalsIgnoreCase(compareField)) {
                lhsc = lhs.getTModelInstanceInfo().get(i).getInstanceDetails();
            }
        }
        for (int i = 0; i < rhs.getTModelInstanceInfo().size(); i++) {
            if (rhs.getTModelInstanceInfo().get(i).getTModelKey().equalsIgnoreCase(compareField)) {
                rhsc = rhs.getTModelInstanceInfo().get(i).getInstanceDetails();
            }
        }

        if (lhsc == null) {
            throw new IllegalArgumentException(compareField + " not found for lhs");
        }
        if (rhsc == null) {
            throw new IllegalArgumentException(compareField + " not found for rhs");
        }
        if (lhsc.getInstanceParms() == null) {
            throw new IllegalArgumentException(compareField + " found lhs, but no data");
        }
        if (rhsc.getInstanceParms() == null) {
            throw new IllegalArgumentException(compareField + " found rhs, but no data");
        }
        if (isNumber) {
            Double l = Double.parseDouble(lhsc.getInstanceParms());
            Double r = Double.parseDouble(rhsc.getInstanceParms());
            return l.compareTo(r);
        }

        if (isDate) {
            XMLGregorianCalendar l = fac.newXMLGregorianCalendar(lhsc.getInstanceParms());
            XMLGregorianCalendar r = fac.newXMLGregorianCalendar(rhsc.getInstanceParms());
            //System.out.println(l.toXMLFormat() + " " + r.toXMLFormat());
            int x = l.compare(r);

            if (x == DatatypeConstants.LESSER) {
                return -1;
            }

            if (x == DatatypeConstants.GREATER) {
                return 1;
            }
            if (x == DatatypeConstants.EQUAL) {
                return 0;
            }
            throw new ArrayIndexOutOfBoundsException("cannot compare, result was " + x);
        }

        if (isDuration) {
            Duration l = fac.newDuration(lhsc.getInstanceParms());
            Duration r = fac.newDuration(rhsc.getInstanceParms());
          //  System.out.println(l.toString() + " " + r.toString());
            int x = l.compare(r);

            if (x == DatatypeConstants.LESSER) {
                return -1;
            }

            if (x == DatatypeConstants.GREATER) {
                return 1;
            }
            if (x == DatatypeConstants.EQUAL) {
                return 0;
            }
            throw new ArrayIndexOutOfBoundsException("cannot compare, result was " + x);
        }

        return 0;
    }
}
