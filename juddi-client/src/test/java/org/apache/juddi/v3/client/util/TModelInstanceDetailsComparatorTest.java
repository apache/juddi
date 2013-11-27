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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

/**
 *
 * @author Alex O'Ree
 */
public class TModelInstanceDetailsComparatorTest {

    public TModelInstanceDetailsComparatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compare method, of class TModelInstanceDetailsComparator.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls");
        TModelInstanceDetails lhs = null;
        TModelInstanceDetails rhs = null;
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator(null, true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls2() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls2");
        TModelInstanceDetails lhs = null;
        TModelInstanceDetails rhs = null;
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, true, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls3() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls3");
        TModelInstanceDetails lhs = null;
        TModelInstanceDetails rhs = null;
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, true);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls4() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls4");
        TModelInstanceDetails lhs = null;
        TModelInstanceDetails rhs = null;
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, true);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls5() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls5");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        TModelInstanceDetails rhs = new TModelInstanceDetails();
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls6() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls6");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        TModelInstanceDetails rhs = new TModelInstanceDetails();
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls7() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls7");
        TModelInstanceDetails lhs = new TModelInstanceDetails();

        TModelInstanceDetails rhs = new TModelInstanceDetails();
        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNulls8() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare nulls8");
        TModelInstanceDetails lhs = null;
        TModelInstanceDetails rhs = null;
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNotFound() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare notfound");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("asd");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("asd");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToNoData() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToNoData");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToLHSNull() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToLHSNull");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        //lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("xyz");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("xyz");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToRHSNull() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToRHSNull");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("xyz");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        //rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("xyz");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test(expected = NumberFormatException.class)
    public void testCompareToNotNumberData() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToNotNumberData");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("xyz");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("xyz");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
    }

    @Test
    public void testCompareToNumberDataEquals() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToNumberDataEquals");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("3.14");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("3.14");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);
        int expResult = 0;
        int result = instance.compare(lhs, rhs);
        Assert.assertEquals("result " + result, expResult, result);
    }

    @Test
    public void testCompareToNumberDataGT() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToNumberDataGT");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("3.15");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("3.14");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);

        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + result, result > 0);
    }

    @Test
    public void testCompareToNumberDataLT() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToNumberDataLT");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("3.10");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("3.14");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", true, false, false);

        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + result, result < 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToDate() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDate");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("asdasd");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("asdasdasd");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

        int result = instance.compare(lhs, rhs);
        //Assert.assertTrue("result " + result,result < 0);

    }

    @Test
    public void testCompareToDateGT() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDateGT");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("2006-05-30T09:30:10-06:00");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("2004-05-30T09:30:10-06:00");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " compare to " +
                rhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " " +
                result, result > 0);

    }

    @Test
    public void testCompareToDateLT() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDateLT");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("2002-05-30T09:30:10-06:00");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("2005-05-30T09:30:10-06:00");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " compare to " +
                rhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " " +
                result, result < 0);

    }

    @Test
    public void testCompareToDateEQ() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDateEQ");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("2002-05-30T09:30:10-06:00");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("2002-05-30T09:30:10-06:00");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, true, false);

        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " compare to " +
                rhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " " +
                result, result == 0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareToDurationInvalid() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDurationInvalid");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("asdasd");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("asdasd");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + result, result == 0);
    }

    @Test
    public void testCompareToDurationLT() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDurationLT");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("P1Y");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("P3Y");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " compare to " +
                rhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " " +
                result, result < 0);
    }
    
    @Test
    public void testCompareToDurationGT() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDurationGT");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("P5Y");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("P2Y");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " compare to " +
                rhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " " +
                result, result > 0);
    }
    
    @Test
    public void testCompareToDurationEQ() throws DatatypeConfigurationException {
        System.out.println("TModelInstanceDetailsComparator.compare testCompareToDurationEQ");
        TModelInstanceDetails lhs = new TModelInstanceDetails();
        lhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        lhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        lhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        lhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("P5Y");
        TModelInstanceDetails rhs = new TModelInstanceDetails();

        rhs.getTModelInstanceInfo().add(new TModelInstanceInfo());
        rhs.getTModelInstanceInfo().get(0).setTModelKey("hi");
        rhs.getTModelInstanceInfo().get(0).setInstanceDetails(new InstanceDetails());
        rhs.getTModelInstanceInfo().get(0).getInstanceDetails().setInstanceParms("P5Y");
        TModelInstanceDetailsComparator instance = new TModelInstanceDetailsComparator("hi", false, false, true);
        int result = instance.compare(lhs, rhs);
        Assert.assertTrue("result " + lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " compare to " +
                lhs.getTModelInstanceInfo().get(0).getInstanceDetails().getInstanceParms() + " " +
                result, result == 0);
    }
}