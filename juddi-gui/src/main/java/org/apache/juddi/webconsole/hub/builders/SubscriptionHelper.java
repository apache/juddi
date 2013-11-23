/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole.hub.builders;

import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.sub_v3.Subscription;

/**
 * Provides some basic helper functions for the edit Subscription page
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class SubscriptionHelper {

    /**
     * returns
     *
     * @param sub
     * @param key
     * @return
     */
    public static String containsFindQualifier(Subscription sub, String key) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionFilter() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getFindBinding() != null) {
            return contains(sub.getSubscriptionFilter().getFindBinding().getFindQualifiers(), key);
        }
        if (sub.getSubscriptionFilter().getFindBusiness() != null) {
            return contains(sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers(), key);
        }
        if (sub.getSubscriptionFilter().getFindRelatedBusinesses() != null) {
            return contains(sub.getSubscriptionFilter().getFindRelatedBusinesses().getFindQualifiers(), key);
        }
        if (sub.getSubscriptionFilter().getFindService() != null) {
            return contains(sub.getSubscriptionFilter().getFindService().getFindQualifiers(), key);
        }
        if (sub.getSubscriptionFilter().getFindTModel() != null) {
            return contains(sub.getSubscriptionFilter().getFindTModel().getFindQualifiers(), key);
        }
        return "";
    }

    public static String getSearchName(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionFilter() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getFindBinding() != null) {
            return sub.getSubscriptionFilter().getFindBinding().getServiceKey();
        }
        if (sub.getSubscriptionFilter().getFindBusiness() != null) {
            return Printers.ListNamesToString(sub.getSubscriptionFilter().getFindBusiness().getName());
        }
        if (sub.getSubscriptionFilter().getFindService() != null) {
            return Printers.ListNamesToString(sub.getSubscriptionFilter().getFindService().getName());
        }
        if (sub.getSubscriptionFilter().getFindTModel() != null) {
            return (sub.getSubscriptionFilter().getFindTModel().getName().getValue());
        }
        return "";
    }

    public static String getSearchLang(Subscription sub) {
        if (sub == null) {
            return "";
        }
        if (sub.getSubscriptionFilter() == null) {
            return "";
        }
        if (sub.getSubscriptionFilter().getFindBusiness() != null) {
            if (!sub.getSubscriptionFilter().getFindBusiness().getName().isEmpty()) {
                return sub.getSubscriptionFilter().getFindBusiness().getName().get(0).getLang();
            }
        }
        if (sub.getSubscriptionFilter().getFindService() != null) {
            if (!sub.getSubscriptionFilter().getFindService().getName().isEmpty()) {
                return sub.getSubscriptionFilter().getFindService().getName().get(0).getLang();
            }
        }
        if (sub.getSubscriptionFilter().getFindTModel() != null) {
            if (sub.getSubscriptionFilter().getFindTModel().getName()!=null) {
                return (sub.getSubscriptionFilter().getFindTModel().getName().getLang());
            }
        }
        return "";
    }

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

        if (sub.getSubscriptionFilter().getGetBindingDetail() != null && !sub.getSubscriptionFilter().getGetBindingDetail().getBindingKey().isEmpty()) {
            return " active ";
        }
        return "";
    }

    public static String isBusinessSpecific(Subscription sub) {
        if (sub == null) {
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

        if (sub.getSubscriptionFilter().getGetAssertionStatusReport() != null && sub.getSubscriptionFilter().getGetAssertionStatusReport().getCompletionStatus() != null) {
            return " active ";
        }
        return "";
    }

    public static String isFindBusiness(Subscription sub) {
        if (sub == null) {
            return "";
        }

        if (sub.getSubscriptionFilter().getFindBusiness() != null) {
            return " active ";
        }
        return "";
    }

    public static String isFindBinding(Subscription sub) {
        if (sub == null) {
            return "";
        }

        if (sub.getSubscriptionFilter().getFindBinding() != null) {
            return " active ";
        }
        return "";
    }

    public static String isFindRelatedBusinesses(Subscription sub) {
        if (sub == null) {
            return "";
        }

        if (sub.getSubscriptionFilter().getFindRelatedBusinesses() != null) {
            return " active ";
        }
        return "";
    }

    public static String isFindService(Subscription sub) {
        if (sub == null) {
            return "";
        }

        if (sub.getSubscriptionFilter().getFindService() != null) {
            return " active ";
        }
        return "";
    }

    public static String isFindTModel(Subscription sub) {
        if (sub == null) {
            return "";
        }

        if (sub.getSubscriptionFilter().getFindTModel() != null) {
            return " active ";
        }
        return "";
    }

    private static String contains(FindQualifiers findQualifiers, String key) {
        if (findQualifiers == null) {
            return "";
        }
        for (int i = 0; i < findQualifiers.getFindQualifier().size(); i++) {
            if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(key)) {
                return " checked ";
            }
        }
        return "";
    }
}
