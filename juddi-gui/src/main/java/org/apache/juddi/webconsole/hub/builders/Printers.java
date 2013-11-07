/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.webconsole.hub.builders;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.uddi.api_v3.*;

/**
 * Provides very basic UDDI spec to String formats, mostly used for debugging
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class Printers {

    private static String TModelInfoToString(TModelInstanceDetails info) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < info.getTModelInstanceInfo().size(); i++) {
            sb.append(info.getTModelInstanceInfo().get(i).getTModelKey());
        }
        return StringEscapeUtils.escapeHtml(sb.toString());
    }

    /**
     * Converts category bags of tmodels to a readable string used from hub
     *
     * @param categoryBag
     * @return
     */
    public static String CatBagToString(CategoryBag categoryBag, String locale) {
        StringBuilder sb = new StringBuilder();
        if (categoryBag == null) {
            return ResourceLoader.GetResource(locale, "errors.nodatareturned");
        }
        for (int i = 0; i < categoryBag.getKeyedReference().size(); i++) {
            sb.append(KeyedReferenceToString(categoryBag.getKeyedReference().get(i), locale));
        }
        for (int i = 0; i < categoryBag.getKeyedReferenceGroup().size(); i++) {
            sb.append(ResourceLoader.GetResource(locale, "items.keyrefgroup")).
                    append(" " + ": ").append(ResourceLoader.GetResource(locale, "items.tmodel.key")).
                    append("=").
                    append(categoryBag.getKeyedReferenceGroup().get(i).getTModelKey());
            for (int k = 0; k < categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {
                sb.append(KeyedReferenceToString(categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().get(k), locale));
            }
        }
        return StringEscapeUtils.escapeHtml(sb.toString());
    }

    private static String KeyedReferenceToString(KeyedReference item, String locale) {
        //TODO i18n
        StringBuilder sb = new StringBuilder();
        sb.append(ResourceLoader.GetResource(locale, "items.keyrefgroup")).
                append(": ").
                append(ResourceLoader.GetResource(locale, "items.name")).
                append("=").
                append(item.getKeyName()).
                append(" ").
                append(ResourceLoader.GetResource(locale, "items.value")).
                append("=").
                append(item.getKeyValue()).
                append(" ").
                append(ResourceLoader.GetResource(locale, "items.tmodel")).
                append("=").
                append(item.getTModelKey()).
                append(System.getProperty("<br>"));
        return StringEscapeUtils.escapeHtml(sb.toString());
    }

    /**
     * This function is useful for translating UDDI's somewhat complex data
     * format to something that is more useful. used from hub
     *
     * @param bindingTemplates
     */
    public static String PrintBindingTemplates(BindingTemplates bindingTemplates, String locale) {
        if (bindingTemplates == null) {
            return ResourceLoader.GetResource(locale, "errors.nobindingtemplates");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
            sb.append(ResourceLoader.GetResource(locale, "items.bindingtemplate.key")).
                    append(": ").
                    append(StringEscapeUtils.escapeHtml(bindingTemplates.getBindingTemplate().get(i).getBindingKey())).
                    append("<Br>");
            sb.append(ResourceLoader.GetResource(locale, "items.description")).
                    append(": ").
                    append(ListToDescString(bindingTemplates.getBindingTemplate().get(i).getDescription())).
                    append("<Br>");
            sb.append(ResourceLoader.GetResource(locale, "pages.editor.tabnav.categories")).
                    append(": ").append(CatBagToString(bindingTemplates.getBindingTemplate().get(i).getCategoryBag(), locale)).
                    append("<Br>");
            sb.append(ResourceLoader.GetResource(locale, "items.tmodel")).
                    append(": ").append(TModelInfoToString(bindingTemplates.getBindingTemplate().get(i).getTModelInstanceDetails())).
                    append("<Br>");
            if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {
                sb.append(ResourceLoader.GetResource(locale, "items.accesspoint")).
                        append(": ").
                        append(StringEscapeUtils.escapeHtml(bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue())).
                        append(" ").
                        append(ResourceLoader.GetResource(locale, "items.type")).
                        append(" ").
                        append(StringEscapeUtils.escapeHtml(bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType())).
                        append("<Br>");
            }
            if (bindingTemplates.getBindingTemplate().get(i).getHostingRedirector() != null) {
                sb.append(ResourceLoader.GetResource(locale, "items.hostingredirector")).
                        append(": ").
                        append(bindingTemplates.getBindingTemplate().get(i).getHostingRedirector().getBindingKey()).
                        append("<br>");
            }
        }
        return (sb.toString());
    }

    /**
     * Description to space separated string
     *
     * @param name
     * @return
     */
    public static String ListToDescString(List<Description> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return StringEscapeUtils.escapeHtml(sb.toString());
    }

    /**
     * Name to space separated string
     *
     * @param name
     * @return
     */
    public static String ListNamesToString(List<Name> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return StringEscapeUtils.escapeHtml(sb.toString());
    }

   
    /**
     * used from Hub at tModelListAsHtml(..)
     *
     * @param findTModel
     * @param session
     * @param isChooser
     * @return
     */
    public static String PrintTModelListAsHtml(TModelList findTModel, HttpSession session, boolean isChooser) {

        StringBuilder sb = new StringBuilder();

        sb.append("<table class=\"table table-hover\"><tr><th>");
        if (isChooser) {
            sb.append("</th><th>");
        }
        sb.append(ResourceLoader.GetResource(session, "items.key"))
                .append("</th><th>")
                .append(ResourceLoader.GetResource(session, "items.name"))
                .append("</th><th>")
                .append(ResourceLoader.GetResource(session, "items.description"))
                .append("</th></tr>");
        for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
            sb.append("<tr><td>");
            if (isChooser) {
                sb.append("<input class=\"modalableTmodel\" type=checkbox id=\"")
                        .append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey()))
                        .append("\"></td><td>");
            }
            if (!isChooser) {
                sb.append("<a href=\"tmodelEditor.jsp?id=")
                        .append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey()))
                        .append("\" >");
            }
            sb.append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey()));
            if (!isChooser) {
                sb.append("</a>");
            }
            sb.append("</td><td>")
                    .append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getName().getValue()));
            if (findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang() != null) {
                sb.append(", ")
                        .append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang()));
            }
            sb.append("</td><td>")
                    .append(StringEscapeUtils.escapeHtml(Printers.ListToDescString(findTModel.getTModelInfos().getTModelInfo().get(i).getDescription())))
                    .append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * used from hub
     *
     * @param findBusiness
     * @param session
     * @param isChooser
     * @return
     */
    public static String BusinessListAsTable(BusinessList findBusiness, HttpSession session, boolean isChooser) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"table table-hover\"<tr><th>");
        if (isChooser) {
            sb.append("</th><th>");
        }
        sb.append(ResourceLoader.GetResource(session, "items.name")).
                append("</th><th>").
                append(ResourceLoader.GetResource(session, "items.service")).
                append("</th></tr>");
        for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
            sb.append("<tr><td>");
            if (isChooser) {
                sb.append("<input type=\"checkbox\" class=\"modalableBusinessChooser\" id=\"").
                        append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                        append("\"></td><td>");
            }
            sb.append("<a title=\"").
                    append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                    append("\"  href=\"businessEditor2.jsp?id=").
                    append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                    append("\">").
                    append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName()))).
                    append("</a></td><td>").
                    append("<a class=\"btn btn-primary\" href=\"javascript:ShowServicesByBusinessKey('").
                    append(StringEscapeUtils.escapeJavaScript(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                    append("');\">");

            if (findBusiness.getBusinessInfos().getBusinessInfo().get(i).getServiceInfos() == null) {
                sb.append("0");
            } else {
                sb.append(ResourceLoader.GetResource(session, "actions.show")).append(" ").append(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getServiceInfos().getServiceInfo().size());
            }
            sb.append("</a>");
            if (!isChooser) {
                sb.append("<a class=\"btn btn-primary\" href=\"serviceEditor.jsp?bizid=").
                        append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                        append("\"><i class=\"icon-plus-sign icon-white  icon-large\"></i></a>");
            }
            sb.append("</td></tr>");

            sb.append("<tr><td colspan=3><div id=\"").
                    append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                    append("\"></div></td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * service list as html, used
     *
     * @param findService
     * @param chooser
     * @param session
     * @return
     */
    public static String ServiceListAsHtml(ServiceList findService, boolean chooser, HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"table\"><tr><th>");
        if (chooser) {
            sb.append("</th><th>");
        }
        sb.append(ResourceLoader.GetResource(session, "items.name")).
                append("</th><th>").
                append(ResourceLoader.GetResource(session, "items.key")).
                append("</th><th>").
                append(ResourceLoader.GetResource(session, "items.business")).
                append("</th></tr>");
        for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
            sb.append("<tr><td>");
            if (chooser) {
                sb.append("<input class=\"modalableServiceChooser\" type=\"checkbox\" id=\"").
                        append(StringEscapeUtils.escapeHtml(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey())).
                        append("\">");
                sb.append("</td><td>");
            }
            sb.append("<a href=\"serviceEditor.jsp?id=").
                    append(StringEscapeUtils.escapeHtml(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey())).
                    append("\" title=\"").
                    append(StringEscapeUtils.escapeHtml(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey()))
                    .append("\">");
            sb.append(Printers.ListNamesToString(findService.getServiceInfos().getServiceInfo().get(i).getName())).append("<i class=\"icon-edit icon-large\"></i<</a></td><td>");

            sb.append((findService.getServiceInfos().getServiceInfo().get(i).getServiceKey())).append("</td><td>");
            sb.append("<a href=\"businessEditor2.jsp?id=")
                    .append(StringEscapeUtils.escapeHtml((findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey())))
                    .append("\">");
            sb.append(StringEscapeUtils.escapeHtml((findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey())))
                    .append("<i class=\"icon-edit icon-large\"></i<</a></td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
}
