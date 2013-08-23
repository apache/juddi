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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeFactory;
import org.apache.juddi.webconsole.PostBackConstants;
import org.apache.juddi.webconsole.hub.UddiHub;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;

/**
 * This class provides functions for building UDDI entities from Http request parameters
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class Builders {

    /**
     * important - regex to separate postback names from indexes, do not remove
     * or alter
     */
    static final Pattern p = Pattern.compile("[a-zA-Z]");

    /**
     * Returns a new map, filtering the original map by key string starts with
     *
     * @param map
     * @param pattern
     * @return
     */
    public static Map MapFilter(Map map, String pattern) {
        Map ret = new HashMap();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.startsWith(pattern)) {
                ret.put(key, map.get(key));
            }
        }
        return ret;
    }

    /**
     * Prefix should be contactXName
     *
     * @param map
     * @param prefix
     * @return
     */
    public static List<PersonName> BuildContactPersonNames(Map map, String prefix, String cte) {
        List<PersonName> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    PersonName pn = new PersonName();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0] == null || t[0].equalsIgnoreCase(cte)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * overview docs
     * @param map
     * @param prefix
     * @param cte
     * @return 
     */
    public static List<OverviewDoc> BuildOverviewDocs(Map map, String prefix, String cte) {
        List<OverviewDoc> ret = new ArrayList<OverviewDoc>();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    OverviewDoc pn = new OverviewDoc();
                    pn.setOverviewURL(new OverviewURL());
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.getOverviewURL().setValue(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.getOverviewURL().setUseType(t[0]);
                    pn.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.DESCRIPTION, cte));
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * phone numbers
     * @param map
     * @param prefix
     * @return 
     */
    public static List<Phone> BuildPhone(Map map, String prefix) {
        List<Phone> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Phone pn = new Phone();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.setUseType(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * builds a contact
     * @param m
     * @param prefix
     * @param cte
     * @return 
     */
    public static Contact BuildSingleContact(Map m, String prefix, String cte) {
        Contact c = new Contact();
        String[] t = (String[]) m.get(prefix + PostBackConstants.TYPE);
        c.setUseType(t[0]);
        c.getPersonName().addAll(BuildContactPersonNames(MapFilter(m, prefix + PostBackConstants.NAME), prefix + PostBackConstants.NAME, cte));
        c.getDescription().addAll(BuildDescription(MapFilter(m, prefix + PostBackConstants.DESCRIPTION), prefix + PostBackConstants.DESCRIPTION, cte));
        c.getEmail().addAll(BuildEmail(MapFilter(m, prefix + PostBackConstants.EMAIL), prefix + PostBackConstants.EMAIL));
        c.getPhone().addAll(BuildPhone(MapFilter(m, prefix + PostBackConstants.PHONE), prefix + PostBackConstants.PHONE));
        c.getAddress().addAll(BuildAddress(MapFilter(m, prefix + PostBackConstants.ADDRESS), prefix + PostBackConstants.ADDRESS, cte));
        return c;
    }

    /**
     * name elements
     * @param map
     * @param prefix
     * @param cte
     * @return 
     */
    public static List<Name> BuildNames(Map map, String prefix, String cte) {
        List<Name> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Name pn = new Name();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0].equalsIgnoreCase(cte)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * builds a compelte category bag
     * @param map
     * @param prefix
     * @return 
     */
    public static CategoryBag BuildCatBag(Map map, String prefix) {
        CategoryBag ret = new CategoryBag();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    KeyedReference pn = new KeyedReference();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setTModelKey(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    pn.setKeyName(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYVALUE);
                    pn.setKeyValue(t[0]);
                    ret.getKeyedReference().add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }
    
/**
 * identifier bag
 * @param map
 * @param prefix
 * @return 
 */
    public static IdentifierBag BuildIdentBag(Map map, String prefix) {
        IdentifierBag ret = new IdentifierBag();
        ret.getKeyedReference().addAll(BuildKeyedReference(map, prefix));
        if (ret.getKeyedReference().isEmpty()) {
            return null;
        }
        return ret;
    }
/**
 * discovery urls
 * @param map
 * @param prefix
 * @return 
 */
    public static DiscoveryURLs BuildDisco(Map map, String prefix) {
        DiscoveryURLs list = new DiscoveryURLs();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    DiscoveryURL pn = new DiscoveryURL();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.setUseType(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    list.getDiscoveryURL().add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        if (list.getDiscoveryURL().isEmpty()) {
            return null;
        }
        return list;
    }

    /**
     * addresses
     * @param map
     * @param prefix
     * @param cte
     * @return 
     */
    public static List<Address> BuildAddress(Map map, String prefix, String cte) {
        List<Address> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Address pn = new Address();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0] == null || t[0].equalsIgnoreCase(cte)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    if (t[0] == null || t[0].equalsIgnoreCase(cte)) {
                        pn.setUseType(null);
                    } else {
                        pn.setUseType(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.SORTCODE);
                    if (t[0] == null || t[0].equalsIgnoreCase(cte)) {
                        pn.setSortCode(null);
                    } else {
                        pn.setSortCode(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    if (t[0] == null || t[0].equalsIgnoreCase(cte)) {
                        pn.setTModelKey(null);
                    } else {
                        pn.setTModelKey(t[0]);
                    }
                    pn.getAddressLine().addAll(BuildAddressLine(MapFilter(map, prefix + index + PostBackConstants.ADDRESSLINE), prefix + index + PostBackConstants.ADDRESSLINE));
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * keyed reference group
     * @param map
     * @param prefix
     * @return 
     */
    public static List<KeyedReferenceGroup> BuildKeyedReferenceGroup(Map map, String prefix) {
        List<KeyedReferenceGroup> ret = new ArrayList<KeyedReferenceGroup>();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    KeyedReferenceGroup pn = new KeyedReferenceGroup();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    if (t != null) {
                        pn.setTModelKey(t[0]);
                        pn.getKeyedReference().addAll(BuildKeyedReference(MapFilter(map, prefix + index + PostBackConstants.KEY_REF), prefix + index + PostBackConstants.KEY_REF));
                        ret.add(pn);
                    } else {
                        UddiHub.log.warn("Unexpected null from BuildKeyedReferenceGroup " + filteredkey + " " + prefix + " " + key);
                    }
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * contactX
     *
     * @param map
     * @return
     */
    public static Contacts BuildContacts(Map map, String cte) {
        Contacts cb = new Contacts();
        Map contactdata = MapFilter(map, PostBackConstants.CONTACT_PREFIX);
        Iterator it = contactdata.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            key = key.replace(PostBackConstants.CONTACT_PREFIX, "");
            Matcher match = p.matcher(key);
            if (match.find()) {
                String index = key.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    cb.getContact().add(BuildSingleContact(MapFilter(contactdata, PostBackConstants.CONTACT_PREFIX + index), PostBackConstants.CONTACT_PREFIX + index, cte));
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        if (cb.getContact().isEmpty()) {
            return null;
        }
        return cb;
    }

    /**
     * email
     * @param map
     * @param prefix
     * @return 
     */
    public static List<Email> BuildEmail(Map map, String prefix) {
        List<Email> list = new ArrayList<Email>();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Email pn = new Email();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.setUseType(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    list.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return list;
    }

    /**
     * description
     * @param map
     * @param prefix
     * @param cte
     * @return 
     */
    public static List<Description> BuildDescription(Map map, String prefix, String cte) {
        List<Description> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Description pn = new Description();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0] == null || t[0].equalsIgnoreCase(cte)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * keyed references
     * @param map
     * @param prefix
     * @return 
     */
    public static List<KeyedReference> BuildKeyedReference(Map map, String prefix) {
        List<KeyedReference> ret = new ArrayList<KeyedReference>();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    KeyedReference pn = new KeyedReference();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    if (t != null) {
                        pn.setTModelKey(t[0]);
                        t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                        pn.setKeyName(t[0]);
                        t = (String[]) map.get(prefix + index + PostBackConstants.KEYVALUE);
                        pn.setKeyValue(t[0]);
                        ret.add(pn);
                    }
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * address lines
     * @param map
     * @param prefix
     * @return 
     */
    public static List<AddressLine> BuildAddressLine(Map map, String prefix) {
        List<AddressLine> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    AddressLine pn = new AddressLine();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    pn.setKeyName(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYVALUE);
                    pn.setKeyValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * binding templates
     * @param map
     * @param prefix
     * @param cte
     * @return 
     */
    public static List<BindingTemplate> BuildBindingTemplates(Map map, String prefix, String cte) {
        List<BindingTemplate> ret = new ArrayList();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    BindingTemplate pn = new BindingTemplate();
//bindingTemplate0Value
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    if (t != null && t.length > 0) {
                        pn.setBindingKey(t[0]);
                    }

                    t = (String[]) map.get(prefix + index + PostBackConstants.HOSTINGREDIRECTOR);
                    if (t != null && t.length > 0) {
                        pn.setHostingRedirector(new HostingRedirector());
                        pn.getHostingRedirector().setBindingKey(t[0]);
                    }
                    AccessPoint ap = new AccessPoint();
                    t = (String[]) map.get(prefix + index + PostBackConstants.ACCESSPOINT_TYPE);
                    if (t != null && t.length > 0) {
                        ap.setUseType(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.ACCESSPOINT_VALUE);
                    if (t != null && t.length > 0) {
                        ap.setValue(t[0]);
                    }
                    if (ap.getValue() != null) {
                        pn.setAccessPoint(ap);
                    }
                    pn.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.DESCRIPTION, cte));
                    CategoryBag cb = new CategoryBag();
                    cb.getKeyedReference().addAll(BuildKeyedReference(MapFilter(map, prefix + index + PostBackConstants.CATBAG_KEY_REF), prefix + index + PostBackConstants.CATBAG_KEY_REF));
                    cb.getKeyedReferenceGroup().addAll(BuildKeyedReferenceGroup(MapFilter(map, prefix + index + PostBackConstants.CATBAG_KEY_REF_GRP), prefix + index + PostBackConstants.CATBAG_KEY_REF_GRP));
                    if (cb.getKeyedReference().isEmpty() && cb.getKeyedReferenceGroup().isEmpty()) {
                        cb = null;
                    }

                    pn.setCategoryBag(cb);
                    pn.setTModelInstanceDetails(BuildTmodelInstanceDetails(MapFilter(map, prefix + index + PostBackConstants.TMODELINSTANCE), prefix + index + PostBackConstants.TMODELINSTANCE, cte));

                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private static TModelInstanceDetails BuildTmodelInstanceDetails(Map map, String prefix, String cte) {
        TModelInstanceDetails ret = new TModelInstanceDetails();

        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    TModelInstanceInfo tmi = new TModelInstanceInfo();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    tmi.setTModelKey(t[0]);

                    tmi.setInstanceDetails(BuildInstanceDetails(MapFilter(map, prefix + index + PostBackConstants.INSTANCE), prefix + index + PostBackConstants.INSTANCE, cte));

                    tmi.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION, cte));

                    ret.getTModelInstanceInfo().add(tmi);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        if (ret.getTModelInstanceInfo().isEmpty()) {
            return null;
        }
        return ret;
    }

    private static InstanceDetails BuildInstanceDetails(Map map, String prefix, String cte) {
        InstanceDetails ret = new InstanceDetails();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {

                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    //pn.setValue(t[0]);
                    ret.setInstanceParms(t[0]);

                    ret.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION, cte));
                    ret.getOverviewDoc().addAll(BuildOverviewDocs(MapFilter(map, prefix + index + PostBackConstants.OVERVIEW), prefix + index + PostBackConstants.OVERVIEW, cte));
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    /**
     * client subscription api
     * @param map
     * @param outmsg
     * @param session
     * @return 
     */
    public static Subscription BuildClientSubscription(Map map, AtomicReference<String> outmsg, HttpSession session) {
        Subscription sub = new Subscription();
        if (outmsg == null) {
            outmsg = new AtomicReference<String>();
        }

        try {
            String alertType = ((String[]) map.get("alertType"))[0];
            if (alertType == null) {
                outmsg.set("alertType not defined");
                return null;
            }
            if (alertType.equalsIgnoreCase("specificItem")) {
                sub = BuildClientSubscriptionSpecificItem(map, outmsg);
            } else if (alertType.equalsIgnoreCase("searchResults")) {
                sub = BuildClientSubscriptionSearchResults(map, outmsg);
            } else {
                outmsg.set("alertType invalid");
                return null;
            }
            if (sub == null) {
                return null;
            }

            String alertTransport = ((String[]) map.get("alertTransport"))[0];
            if (alertTransport == null) {
            } else {
                if (alertTransport.equalsIgnoreCase("bindingTemplate")) {
                    sub.setBindingKey(((String[]) map.get("bindingKey"))[0]);
                } else {
                    sub.setBindingKey(null);
                }
            }
            if (map.get("subkey") != null) {
                String subkey = ((String[]) map.get("subkey"))[0];
                if (subkey != null && !subkey.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
                    sub.setSubscriptionKey(subkey);
                }
            }
            //options
            sub = BuildSubscriptionOptions(map, sub);
            return sub;
        } catch (Exception ex) {
            outmsg.set(ex.getMessage());
            return null;
        }

    }

    private static Subscription BuildClientSubscriptionSpecificItem(Map map, AtomicReference<String> outmsg) {
        try {
            Subscription sub = new Subscription();
            String alertCritera = ((String[]) map.get("alertCriteraSingleItem"))[0];

            List<String> keys = new ArrayList<String>();
            String ItemKey = ((String[]) map.get("itemKey"))[0];
            if (ItemKey == null) {
                outmsg.set("no item defined");
                return null;
            }
            //TODO this is an issue. Unknown if commas can be included within UDDI keys
            if (ItemKey.contains(",")) {
                String[] k2 = ItemKey.split(",");
                for (int i = 0; i < k2.length; i++) {
                    if (k2[i] == null) {
                        continue;
                    }
                    if (k2[i].trim().isEmpty()) {
                        continue;
                    }
                    keys.add(k2[i].trim());
                }
            } else {
                keys.add(ItemKey);
            }


            sub.setSubscriptionFilter(new SubscriptionFilter());

            if (alertCritera != null) {

                if (alertCritera.equalsIgnoreCase("binding")) {
                    sub.getSubscriptionFilter().setGetBindingDetail(new GetBindingDetail());
                    sub.getSubscriptionFilter().getGetBindingDetail().getBindingKey().addAll(keys);
                } else if (alertCritera.equalsIgnoreCase("service")) {
                    sub.getSubscriptionFilter().setGetServiceDetail(new GetServiceDetail());
                    sub.getSubscriptionFilter().getGetServiceDetail().getServiceKey().addAll(keys);
                } else if (alertCritera.equalsIgnoreCase("business")) {
                    sub.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
                    sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().addAll(keys);
                } else if (alertCritera.equalsIgnoreCase("publisherAssertion")) {
                    //unknow if this will work
                    sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                    sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.valueOf(((String[]) map.get("assertionStatus"))[0]));
                } else if (alertCritera.equalsIgnoreCase("relatedBusiness")) {
                    outmsg.set("relatedBusiness is not supported for single item subscriptions");
                    return null;
                } else if (alertCritera.equalsIgnoreCase("tmodel")) {
                    sub.getSubscriptionFilter().setGetTModelDetail(new GetTModelDetail());
                    sub.getSubscriptionFilter().getGetTModelDetail().getTModelKey().addAll(keys);
                } else {
                    outmsg.set("alert critera invalid");
                    return null;
                }
            } else {
                outmsg.set("alert critera not defined");
                return null;
            }
            return sub;
        } catch (Exception ex) {
            UddiHub.log.warn(null, ex);
            outmsg.set("error parsing");
            return null;
        }
    }

    private static Subscription BuildClientSubscriptionSearchResults(Map map, AtomicReference<String> outmsg) {
        try {
            Subscription sub = new Subscription();
            String alertCritera = ((String[]) map.get("alertCriteraMultipleItem"))[0];

            sub.setSubscriptionFilter(new SubscriptionFilter());
            Name name = new Name();
            name.setValue(((String[]) map.get("searchcontent"))[0]);
            name.setLang(((String[]) map.get("searchlang"))[0]);
            FindQualifiers fq = new FindQualifiers();
            String[] fqs = (String[]) map.get("findqualifier");
            if (fqs != null) {
                for (int i = 0; i < fqs.length; i++) {
                    fq.getFindQualifier().add(fqs[i]);
                }
            }
            if (fq.getFindQualifier().isEmpty()) {
                fq = null;
            }
            if (alertCritera != null) {
                if (alertCritera.equalsIgnoreCase("binding")) {
                    //sub.getSubscriptionFilter().setFindBinding(new FindBinding());
                    //sub.getSubscriptionFilter().getFindBinding().
                } else if (alertCritera.equalsIgnoreCase("service")) {
                    sub.getSubscriptionFilter().setFindService(new FindService());
                    sub.getSubscriptionFilter().getFindService().getName().add(name);
                    sub.getSubscriptionFilter().getFindService().setFindQualifiers(fq);
                } else if (alertCritera.equalsIgnoreCase("business")) {
                    sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                    sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(fq);
                    sub.getSubscriptionFilter().getFindBusiness().getName().add(name);
                    //              sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().addAll(keys);
                } else if (alertCritera.equalsIgnoreCase("publisherAssertion")) {
                    //unknow if this will work
                    sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                    sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.valueOf(((String[]) map.get("assertionStatus"))[0]));
                } else if (alertCritera.equalsIgnoreCase("relatedBusiness")) {
                    sub.getSubscriptionFilter().setFindRelatedBusinesses(new FindRelatedBusinesses());
                    sub.getSubscriptionFilter().getFindRelatedBusinesses().setFindQualifiers(fq);
                    sub.getSubscriptionFilter().getFindRelatedBusinesses().setBusinessKey(((String[]) map.get("searchcontent"))[0]);
                } else if (alertCritera.equalsIgnoreCase("tmodel")) {
                    sub.getSubscriptionFilter().setFindTModel(new FindTModel());
                    sub.getSubscriptionFilter().getFindTModel().setFindQualifiers(fq);
                    sub.getSubscriptionFilter().getFindTModel().setName(name);
                } else {
                    outmsg.set("alert critera invalid");
                    return null;
                }
            } else {
                outmsg.set("alert critera not defined");
                return null;
            }
            return sub;
        } catch (Exception ex) {
            UddiHub.log.warn(null, ex);
            outmsg.set("error parsing");
            return null;
        }
    }

    private static Subscription BuildSubscriptionOptions(Map map, Subscription sub) {
        if (sub == null) {
            return null;
        }
        try {
            sub.setBrief(Boolean.parseBoolean(((String[]) map.get("brief"))[0]));
        } catch (Exception x) {
            sub.setBrief(false);
        }

        try {
            sub.setMaxEntities(Integer.parseInt(((String[]) map.get("maxRecords"))[0]));
        } catch (Exception x) {
            sub.setBrief(false);
        }

        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            DateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            Date parsed = dformat.parse(((String[]) map.get("expires"))[0]);

            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(parsed);
            sub.setExpiresAfter(df.newXMLGregorianCalendar(gcal));
        } catch (Exception ex) {
            UddiHub.log.warn("Unexpected parsing error ", ex);
        }


        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            DateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            Date parsed = dformat.parse(((String[]) map.get("expires"))[0]);

            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(parsed);
            sub.setExpiresAfter(df.newXMLGregorianCalendar(gcal));
        } catch (Exception ex) {
            UddiHub.log.warn("Unexpected parsing error ", ex);
        }

        try {
            long durationInMilliSeconds = 0;
            DatatypeFactory df = DatatypeFactory.newInstance();
            String interval = ((String[]) map.get("interval"))[0];
            String[] tokens = interval.split(":");
            durationInMilliSeconds += Integer.parseInt(tokens[0]) * 60 * 60 * 1000;
            durationInMilliSeconds += Integer.parseInt(tokens[1]) * 60 * 1000;
            durationInMilliSeconds += Integer.parseInt(tokens[2]) * 1000;


            sub.setNotificationInterval(df.newDuration(durationInMilliSeconds));

        } catch (Exception ex) {
            UddiHub.log.warn("Unexpected parsing error ", ex);
        }


        return sub;
    }
}
