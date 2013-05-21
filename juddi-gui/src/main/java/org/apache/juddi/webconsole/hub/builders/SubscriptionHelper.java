/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole.hub.builders;

import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.uddi.sub_v3.Subscription;

/**
 * Provides some basic helper functions for the edit Subscription page
 *
 * @author Alex O'Ree
 */
public class SubscriptionHelper {

    public static String getItemKeySpecific(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionFilter() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getGetAssertionStatusReport() != null && sub.getSubscriptionFilter().getGetAssertionStatusReport().getCompletionStatus() != null) {
            return sub.getSubscriptionFilter().getGetAssertionStatusReport().getCompletionStatus().toString();
        }

        if (sub.getSubscriptionFilter().getGetBindingDetail() != null) {
            return ToHtmlOption(sub.getSubscriptionFilter().getGetBindingDetail().getBindingKey());
        }

        if (sub.getSubscriptionFilter().getGetBusinessDetail() != null) {
            return ToHtmlOption(sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey());
        }

        if (sub.getSubscriptionFilter().getGetServiceDetail() != null) {
            return ToHtmlOption(sub.getSubscriptionFilter().getGetServiceDetail().getServiceKey());
        }

        if (sub.getSubscriptionFilter().getGetTModelDetail() != null) {
            return ToHtmlOption(sub.getSubscriptionFilter().getGetTModelDetail().getTModelKey());
        }
        return "";
    }

    public static String ToHtmlOption(List<String> items) {
        StringBuilder sb = new StringBuilder();
        if (items == null || items.isEmpty()) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                continue;
            }
            if (items.get(i).trim().isEmpty()) {
                continue;
            }
            sb.append("<option value\"").append(StringEscapeUtils.escapeHtml(items.get(i).trim()))
                    .append("\">")
                    .append(StringEscapeUtils.escapeHtml(items.get(i)))
                    .append("</option>");
        }
        return sb.toString();
    }

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

    public static String isBindingSpecific(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionKey() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getGetBindingDetail() != null && !sub.getSubscriptionFilter().getGetBindingDetail().getBindingKey().isEmpty()) {
            return " active ";
        }
        return "";
    }

    public static String isBusinessSpecific(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionKey() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getGetBusinessDetail() != null && !sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().isEmpty()) {
            return " active ";
        }
        return "";
    }

    public static String isServiceSpecific(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionKey() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getGetServiceDetail() != null && !sub.getSubscriptionFilter().getGetServiceDetail().getServiceKey().isEmpty()) {
            return " active ";
        }
        return "";
    }

    public static String isTModelSpecific(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionKey() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getGetTModelDetail() != null && !sub.getSubscriptionFilter().getGetTModelDetail().getTModelKey().isEmpty()) {
            return " active ";
        }
        return "";
    }

    public static String isPublisherAssertionSpecific(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionKey() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getGetAssertionStatusReport() != null && sub.getSubscriptionFilter().getGetAssertionStatusReport().getCompletionStatus() != null) {
            return " active ";
        }
        return "";
    }
}
