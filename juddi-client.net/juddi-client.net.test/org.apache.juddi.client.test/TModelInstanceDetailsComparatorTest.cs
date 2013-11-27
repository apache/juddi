using NUnit.Framework;
using org.apache.juddi.v3.client.util;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace juddi_client.net.test.org.apache.juddi.client.test
{
    [TestFixture]
    public class TModelInstanceDetailsComparatorTest
    {

        /**
 * Test of compare method, of class TModelInstanceDetailsComparator.
 */
        [Test]
        [ExpectedException(typeof(ArgumentNullException))]
        public void testCompareToNulls()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls");
            tModelInstanceInfo[] lhs = null;
            tModelInstanceInfo[] rhs = null;
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator(null, true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNulls2()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls2");
            tModelInstanceInfo[] lhs = null;
            tModelInstanceInfo[] rhs = null;
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, true, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNulls3()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls3");
            tModelInstanceInfo[] lhs = null;
            tModelInstanceInfo[] rhs = null;
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, true);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNulls4()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls4");
            tModelInstanceInfo[] lhs = null;
            tModelInstanceInfo[] rhs = null;
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, true);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNulls5()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls5");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNulls6()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls6");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNulls7()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls7");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentNullException))]
        public void testCompareToNulls8()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare nulls8");
            tModelInstanceInfo[] lhs = null;
            tModelInstanceInfo[] rhs = null;
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNotFound()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare notfound");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };
            lhs[0].tModelKey = ("asd");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };
            rhs[0].tModelKey = ("asd");
            rhs[0].instanceDetails = new instanceDetails();
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToNoData()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToNoData");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void testCompareToLHSNull()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToLHSNull");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();

            rhs[0].instanceDetails.instanceParms = ("xyz");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(System.ArgumentOutOfRangeException))]

        public void testCompareToRHSNull()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToRHSNull");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("xyz");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            //rhs[0].instanceDetails.instanceParms=("xyz");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        [ExpectedException(typeof(FormatException))]
        public void testCompareToNotNumberData()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToNotNumberData");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("xyz");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("xyz");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
        }

        [Test]
        public void testCompareToNumberDataEquals()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToNumberDataEquals");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("3.14");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("3.14");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
            int expResult = 0;
            int result = instance.compare(lhs, rhs);
            Assert.AreEqual(expResult, result, "result " + result);
        }

        [Test]
        public void testCompareToNumberDataGT()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToNumberDataGT");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("3.15");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("3.14");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);

            int result = instance.compare(lhs, rhs);
            Assert.True(result > 0, "result " + result);
        }

        [Test]
        public void testCompareToNumberDataLT()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToNumberDataLT");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("3.10");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("3.14");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);

            int result = instance.compare(lhs, rhs);
            Assert.True(result < 0, "result " + result);
        }

        [Test]
        [ExpectedException(typeof(System.FormatException))]
        public void testCompareToDate()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDate");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("asdasd");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("asdasdasd");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

            int result = instance.compare(lhs, rhs);
            //Assert.assertTrue("result " + result,result < 0);

        }

        [Test]
        public void testCompareToDateGT()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDateGT");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("2006-05-30T09:30:10-06:00");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("2004-05-30T09:30:10-06:00");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

            int result = instance.compare(lhs, rhs);
            Assert.True(result > 0, "result " + lhs[0].instanceDetails.instanceParms + " compare to " +
                    rhs[0].instanceDetails.instanceParms + " " +
                    result);

        }

        [Test]
        public void testCompareToDateLT()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDateLT");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("2002-05-30T09:30:10-06:00");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("2005-05-30T09:30:10-06:00");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

            int result = instance.compare(lhs, rhs);
            Assert.True(result < 0, "result " + lhs[0].instanceDetails.instanceParms + " compare to " +
                    rhs[0].instanceDetails.instanceParms + " " + result);

        }

        [Test]
        public void testCompareToDateEQ()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDateEQ");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("2002-05-30T09:30:10-06:00");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("2002-05-30T09:30:10-06:00");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

            int result = instance.compare(lhs, rhs);
            Assert.True(result == 0, "result " + lhs[0].instanceDetails.instanceParms + " compare to " +
                    rhs[0].instanceDetails.instanceParms + " " +
                    result);

        }

        [Test]
        [ExpectedException(typeof(System.FormatException))]
        public void testCompareToDurationInvalid()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDurationInvalid");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("asdasd");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("asdasd");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
            int result = instance.compare(lhs, rhs);
            Assert.True(result == 0, "result " + result);
        }

        [Test]
        public void testCompareToDurationLT()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDurationLT");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("P1Y");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("P3Y");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
            int result = instance.compare(lhs, rhs);
            Assert.True(result < 0, "result " + lhs[0].instanceDetails.instanceParms + " compare to " +
                    rhs[0].instanceDetails.instanceParms + " " +
                    result);
        }

        [Test]
        public void testCompareToDurationGT()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDurationGT");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("P5Y");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("P2Y");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
            int result = instance.compare(lhs, rhs);
            Assert.True(result > 0, "result " + lhs[0].instanceDetails.instanceParms + " compare to " +
                    rhs[0].instanceDetails.instanceParms + " " +
                    result);
        }

        [Test]
        public void testCompareToDurationEQ()
        {
            System.Console.Out.WriteLine("TModelInstanceDetailsComparator.compare testCompareToDurationEQ");
            tModelInstanceInfo[] lhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };

            lhs[0].tModelKey = ("hi");
            lhs[0].instanceDetails = new instanceDetails();
            lhs[0].instanceDetails.instanceParms = ("P5Y");
            tModelInstanceInfo[] rhs = new tModelInstanceInfo[1] { new tModelInstanceInfo() };


            rhs[0].tModelKey = ("hi");
            rhs[0].instanceDetails = new instanceDetails();
            rhs[0].instanceDetails.instanceParms = ("P5Y");
            TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
            int result = instance.compare(lhs, rhs);
            Assert.True(result == 0, "result " + lhs[0].instanceDetails.instanceParms + " compare to " +
                     lhs[0].instanceDetails.instanceParms + " " +
                    result);
        }
    }
}
