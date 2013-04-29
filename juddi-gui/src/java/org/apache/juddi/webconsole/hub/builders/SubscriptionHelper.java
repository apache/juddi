/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole.hub.builders;

import org.uddi.sub_v3.Subscription;

/**
 *
 * @author Alex O'Ree
 */
public class SubscriptionHelper {

    public static boolean isSpecificItem(Subscription sub) {
        if (sub == null) {
            throw new IllegalArgumentException();
        }
        if (sub.getSubscriptionFilter() == null) {
            throw new IllegalArgumentException();
        }
        return (sub.getSubscriptionFilter().getGetAssertionStatusReport() != null
                || sub.getSubscriptionFilter().getGetBindingDetail() != null
                || sub.getSubscriptionFilter().getGetBusinessDetail() != null
                || sub.getSubscriptionFilter().getGetServiceDetail() != null
                || sub.getSubscriptionFilter().getGetTModelDetail() != null);
    }
}
