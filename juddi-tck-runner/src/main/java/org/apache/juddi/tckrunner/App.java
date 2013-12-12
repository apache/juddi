package org.apache.juddi.tckrunner;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Hello world!
 *
 */
public class App {

        public static void main(String[] args) {
                System.out.println("Hello World!");

                JUnitCore junit = new JUnitCore();
                Result result = junit.run(org.apache.juddi.v3.bpel.BPEL_010_IntegrationTest.class,
                        org.apache.juddi.v3.bpel.BPEL_020_IntegrationTest.class,
                        org.apache.juddi.v3.tck.JUDDI_010_PublisherIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_160_RESTIntergrationTest.class,
                        org.apache.juddi.v3.tck.JUDDI_091_RMISubscriptionListenerIntegrationTest.class,
                        org.apache.juddi.v3.tck.JUDDI_100_ClientSubscriptionInfoTest.class,
                        org.apache.juddi.v3.tck.UDDI_001_UDDIServiceTest.class,
                        org.apache.juddi.v3.tck.UDDI_010_PublisherIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_020_TmodelIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_030_BusinessEntityIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_030_BusinessEntityLoadTest.class,
                        org.apache.juddi.v3.tck.UDDI_040_BusinessServiceIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_040_BusinessServiceLoadTest.class,
                        org.apache.juddi.v3.tck.UDDI_050_BindingTemplateIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_060_PublisherAssertionIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_070_FindEntityIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_080_SubscriptionIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_090_SubscriptionListenerIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_110_FindBusinessIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_120_CombineCategoryBagsFindServiceIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_130_CombineCategoryBagsFindBusinessIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_140_NegativePublicationIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_141_JIRAIntegrationTest.class,
                        org.apache.juddi.v3.tck.UDDI_150_CustodyTransferIntegrationTest.class);

                System.out.println("Summary");
                System.out.println("Failed Test Cases: " + result.getFailureCount());
                System.out.println("Skipped Test Cases: " + result.getIgnoreCount());
                System.out.println("Ran Test Cases: " + result.getRunCount());
                System.out.println("Time: " + result.getRunTime());
                for (int i=0; i < result.getFailures().size(); i++){
                        System.out.println("-------------------------------------");
                        System.out.println(result.getFailures().get(i).getTestHeader());
                        System.out.println(result.getFailures().get(i).getDescription().getClassName());
                        System.out.println(result.getFailures().get(i).getDescription().getMethodName());
                        System.out.println(result.getFailures().get(i).getMessage());
                        }
        }
}
