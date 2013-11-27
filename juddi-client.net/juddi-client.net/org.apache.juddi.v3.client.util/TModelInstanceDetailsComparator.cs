using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace org.apache.juddi.v3.client.util
{
    /// <summary>
    /// Compares two UDDI TModelInstanceDetails, searching for a specific tModel key, then parsing to the selected data type, then comparing.
    /// @author Alex O'Ree
    /// </summary>
    public class TModelInstanceDetailsComparator
    {

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
         * @throws DatatypeConfigurationException, ArgumentOutOfRangeException, ArgumentNullException
         * FormatException
         */
        public TModelInstanceDetailsComparator(String TModelKey, bool number, bool XMLdate, bool XMLduration)
        {
            if (TModelKey == null || TModelKey.Length == 0)
            {
                throw new ArgumentNullException();
            }
            compareField = TModelKey;
            if (!number && !XMLdate && !XMLduration)
            {
                throw new ArgumentOutOfRangeException("only one data type can be selected");
            }
            if (number && XMLdate && !XMLduration)
            {
                throw new ArgumentOutOfRangeException("only one data type can be selected");
            }
            if (number && !XMLdate && XMLduration)
            {
                throw new ArgumentOutOfRangeException("only one data type can be selected");
            }
            if (!number && XMLdate && XMLduration)
            {
                throw new ArgumentOutOfRangeException("only one data type can be selected");
            }
            if (number && XMLdate && XMLduration)
            {
                throw new ArgumentOutOfRangeException("only one data type can be selected");
            }
            isNumber = number;
            isDate = XMLdate;
            isDuration = XMLduration;
        }

        String compareField = null;
        bool isNumber = false;
        bool isDate = false;
        bool isDuration = false;

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
         *  throws IllegalArgumentException, NumberFormatException, NullPointerException, ArrayIndexOutOfBoundsException
         */
        public int compare(tModelInstanceInfo[] lhs, tModelInstanceInfo[] rhs)
        {
            if (lhs == null)
            {
                throw new ArgumentNullException("lhs");
            }
            if (rhs == null)
            {
                throw new ArgumentNullException("rhs");
            }
            if (lhs.Length == 0 || rhs.Length == 0)
            {
                throw new ArgumentOutOfRangeException("no data to compare");
            }
            instanceDetails lhsc = null;
            instanceDetails rhsc = null;
            for (int i = 0; i < lhs.Length; i++)
            {
                if (lhs[i].tModelKey!=null && lhs[i].tModelKey.Equals(compareField, StringComparison.CurrentCultureIgnoreCase))
                {
                    lhsc = lhs[i].instanceDetails;
                }
            }
            for (int i = 0; i < rhs.Length; i++)
            {
                if (rhs[i].tModelKey!=null && rhs[i].tModelKey.Equals(compareField, StringComparison.CurrentCultureIgnoreCase))
                {
                    rhsc = rhs[i].instanceDetails;
                }
            }

            if (lhsc == null)
            {
                throw new ArgumentOutOfRangeException(compareField + " not found for lhs");
            }
            if (rhsc == null)
            {
                throw new ArgumentOutOfRangeException(compareField + " not found for rhs");
            }
            if (lhsc.instanceParms == null)
            {
                throw new ArgumentOutOfRangeException(compareField + " found lhs, but no data");
            }
            if (rhsc.instanceParms == null)
            {
                throw new ArgumentOutOfRangeException(compareField + " found rhs, but no data");
            }
            if (isNumber)
            {
                Double l = Double.Parse(lhsc.instanceParms);
                Double r = Double.Parse(rhsc.instanceParms);
                return l.CompareTo(r);
            }

            if (isDate)
            {

                DateTime l = XmlConvert.ToDateTime(lhsc.instanceParms);
                DateTime r = XmlConvert.ToDateTime(rhsc.instanceParms);
                int x = l.CompareTo(r);

                return x;
            }

            if (isDuration)
            {
                TimeSpan l = XmlConvert.ToTimeSpan(lhsc.instanceParms);
                TimeSpan r = XmlConvert.ToTimeSpan(rhsc.instanceParms);
                int x = l.CompareTo(r);
                return x;
            }

            return 0;
        }
    }
}
