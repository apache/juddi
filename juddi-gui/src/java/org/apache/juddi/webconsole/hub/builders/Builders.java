/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole.hub.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.webconsole.PostBackConstants;
import org.apache.juddi.webconsole.hub.UddiHub;
import org.uddi.api_v3.*;

/**
 *
 * @author Alex O'Ree
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
    public static List<PersonName> BuildContactPersonNames(Map map, String prefix) {
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
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
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

    public static List<OverviewDoc> BuildOverviewDocs(Map map, String prefix) {
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
                    pn.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.DESCRIPTION));
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

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

    public static Contact BuildSingleContact(Map m, String prefix) {
        Contact c = new Contact();
        String[] t = (String[]) m.get(prefix + PostBackConstants.TYPE);
        c.setUseType(t[0]);
        c.getPersonName().addAll(BuildContactPersonNames(MapFilter(m, prefix + PostBackConstants.NAME), prefix + PostBackConstants.NAME));
        c.getDescription().addAll(BuildDescription(MapFilter(m, prefix + PostBackConstants.DESCRIPTION), prefix + PostBackConstants.DESCRIPTION));
        c.getEmail().addAll(BuildEmail(MapFilter(m, prefix + PostBackConstants.EMAIL), prefix + PostBackConstants.EMAIL));
        c.getPhone().addAll(BuildPhone(MapFilter(m, prefix + PostBackConstants.PHONE), prefix + PostBackConstants.PHONE));
        c.getAddress().addAll(BuildAddress(MapFilter(m, prefix + PostBackConstants.ADDRESS), prefix + PostBackConstants.ADDRESS));
        return c;
    }

    public static List<Name> BuildNames(Map map, String prefix) {
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
                    if (t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
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

    public static IdentifierBag BuildIdentBag(Map map, String prefix) {
        IdentifierBag ret = new IdentifierBag();
        ret.getKeyedReference().addAll(BuildKeyedReference(map, prefix));
        if (ret.getKeyedReference().isEmpty()) {
            return null;
        }
        return ret;
    }

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

    public static List<Address> BuildAddress(Map map, String prefix) {
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
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setUseType(null);
                    } else {
                        pn.setUseType(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.SORTCODE);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setSortCode(null);
                    } else {
                        pn.setSortCode(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
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
                    pn.setTModelKey(t[0]);
                    pn.getKeyedReference().addAll(BuildKeyedReference(MapFilter(map, prefix + index + PostBackConstants.KEY_REF), prefix + index + PostBackConstants.KEY_REF));
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
     * contactX
     *
     * @param map
     * @return
     */
    public static Contacts BuildContacts(Map map) {
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
                    cb.getContact().add(BuildSingleContact(MapFilter(contactdata, PostBackConstants.CONTACT_PREFIX + index), PostBackConstants.CONTACT_PREFIX + index));
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

    public static List<Description> BuildDescription(Map map, String prefix) {
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
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
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
                    pn.setTModelKey(t[0]);
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

    public static List<BindingTemplate> BuildBindingTemplates(Map map, String prefix) {
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

                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.BINDINGKEY);
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
                    pn.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.DESCRIPTION));
                    CategoryBag cb = new CategoryBag();
                    cb.getKeyedReference().addAll(BuildKeyedReference(MapFilter(map, prefix + index + PostBackConstants.CATBAG_KEY_REF), prefix + index + PostBackConstants.CATBAG_KEY_REF));
                    cb.getKeyedReferenceGroup().addAll(BuildKeyedReferenceGroup(MapFilter(map, prefix + index + PostBackConstants.CATBAG_KEY_REF_GRP), prefix + index + PostBackConstants.CATBAG_KEY_REF_GRP));
                    if (cb.getKeyedReference().isEmpty() && cb.getKeyedReferenceGroup().isEmpty()) {
                        cb = null;
                    }

                    pn.setCategoryBag(cb);
                    pn.setTModelInstanceDetails(BuildTmodelInstanceDetails(MapFilter(map, prefix + index + PostBackConstants.TMODELINSTANCE), prefix + index + PostBackConstants.TMODELINSTANCE));

                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private static TModelInstanceDetails BuildTmodelInstanceDetails(Map map, String prefix) {
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

                    tmi.setInstanceDetails(BuildInstanceDetails(MapFilter(map, prefix + index + PostBackConstants.INSTANCE), prefix + index + PostBackConstants.INSTANCE));

                    tmi.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.INSTANCE+PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.INSTANCE+PostBackConstants.DESCRIPTION));

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

    private static InstanceDetails BuildInstanceDetails(Map map, String prefix) {
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

                    ret.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION));
                    ret.getOverviewDoc().addAll(BuildOverviewDocs(MapFilter(map, prefix + index + PostBackConstants.OVERVIEW), prefix + index + PostBackConstants.OVERVIEW));
                    processedIndexes.add(index);
                }
            } else {
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }
}
