using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.mapping
{
    public static class MapUDDIv3to2
    {
        public readonly static String VERSION = "2.0";
        public static uddi.apiv2.find_binding MapFindBinding(uddi.apiv3.find_binding find_binding1)
        {
            if (find_binding1 == null) return null;
            uddi.apiv2.find_binding r = new uddi.apiv2.find_binding();

            r.generic = VERSION;
            r.findQualifiers = MapFindQualifiers(find_binding1.findQualifiers);
            r.maxRows = find_binding1.maxRows;
            r.maxRowsSpecified = find_binding1.maxRowsSpecified;
            r.serviceKey = find_binding1.serviceKey;

            if (String.IsNullOrEmpty(r.serviceKey))
                r.serviceKey = "";
            r.tModelBag = find_binding1.tModelBag;
            return r;

        }

        private static string[] MapFindQualifiers(string[] findQualifiers)
        {
            List<String> r = new List<string>();
            for (int i = 0; i < findQualifiers.Length; i++)
            {
                if (findQualifiers[i].Equals(UDDIConstants.TRANSPORT_HTTP, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:68DE9E80-AD09-469D-8A37-088422BFBC36");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.TRANSPORT_EMAIL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:93335D49-3EFB-48A0-ACEA-EA102B60DDC6");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.TRANSPORT_FTP, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.TRANSPORT_FAX, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:1A2B00BE-6E2C-42F5-875B-56F32686E0E7");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.TRANSPORT_POTS, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:38E12427-5536-4260-A6F9-B5B530E63A07");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.IS_REPLACED_BY, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.OWNING_BUSINESS, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:4064C064-6D14-4F35-8953-9652106476A9");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.RELATIONSHIPS, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03");
                }
                else if (findQualifiers[i].Equals("uddi:uddi.org:categorization:nodes", StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:327A56F0-3299-4461-BC23-5CD513E95C55");
                }
                else if (findQualifiers[i].Equals("uddi:uddi.org:categorization:general_keywords", StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:A035A07C-F362-44dd-8F95-E2B134BF43B4");
                }
                else if (findQualifiers[i].Equals("uddi:uddi.org:categorization:types", StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.EXACT_MATCH, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.EXACT_MATCH_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("exactNameMatch");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.CASE_SENSITIVE_MATCH, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("caseSensitiveMatch");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.OR_ALL_KEYS, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.OR_ALL_KEYS_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("orAllKeys");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.OR_LIKE_KEYS, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.OR_LIKE_KEYS_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("orLikeKeys");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.AND_ALL_KEYS, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.AND_ALL_KEYS_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add("andAllKeys");
                }
                else if (findQualifiers[i].Equals(UDDIConstants.SORT_BY_DATE_ASC, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.SORT_BY_DATE_ASC_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add(UDDIConstants.SORT_BY_DATE_ASC);
                }
                else if (findQualifiers[i].Equals(UDDIConstants.SORT_BY_DATE_DESC, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.SORT_BY_DATE_DESC_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add(UDDIConstants.SORT_BY_DATE_DESC);
                }
                else if (findQualifiers[i].Equals(UDDIConstants.SORT_BY_NAME_ASC, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.SORT_BY_NAME_ASC_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add(UDDIConstants.SORT_BY_NAME_ASC);
                }
                else if (findQualifiers[i].Equals(UDDIConstants.SORT_BY_NAME_DESC, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.SORT_BY_NAME_DESC_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add(UDDIConstants.SORT_BY_NAME_DESC);
                }
                else if (findQualifiers[i].Equals(UDDIConstants.SERVICE_SUBSET, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.SERVICE_SUBSET_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add(UDDIConstants.SERVICE_SUBSET);
                }
                else if (findQualifiers[i].Equals(UDDIConstants.COMBINE_CATEGORY_BAGS, StringComparison.CurrentCultureIgnoreCase)
                   || findQualifiers[i].Equals(UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL, StringComparison.CurrentCultureIgnoreCase))
                {
                    r.Add(UDDIConstants.COMBINE_CATEGORY_BAGS);
                }
                else
                {
                    r.Add(findQualifiers[i]);
                }
            }
            return r.ToArray();
        }

        public static uddi.apiv2.find_business MapFindBusiness(uddi.apiv3.find_business fb)
        {
            if (fb == null) return null;
            uddi.apiv2.find_business r = new uddi.apiv2.find_business();
            r.generic = VERSION;
            r.findQualifiers = MapFindQualifiers(fb.findQualifiers);
            r.categoryBag = MapCategoryBag(fb.categoryBag);
            r.discoveryURLs = MapDiscoURL(fb.discoveryURLs);
            r.maxRows = fb.maxRows;
            r.maxRowsSpecified = fb.maxRowsSpecified;
            r.name = MapName(fb.name);
            r.tModelBag = fb.tModelBag;
            return r;
        }

        private static uddi.apiv2.name[] MapName(uddi.apiv3.name[] name)
        {
            if (name == null) return null;
            List<uddi.apiv2.name> names = new List<uddi.apiv2.name>();
            for (int i = 0; i < name.Length; i++)
            {
                uddi.apiv2.name x = new uddi.apiv2.name();
                x.lang = name[i].lang;
                x.Value = name[i].Value;
                names.Add(x);
            }
            return names.ToArray();
        }

        private static uddi.apiv2.discoveryURL[] MapDiscoURL(uddi.apiv3.discoveryURL[] discoveryURL)
        {
            if (discoveryURL == null) return null;
            List<uddi.apiv2.discoveryURL> names = new List<uddi.apiv2.discoveryURL>();
            for (int i = 0; i < discoveryURL.Length; i++)
            {
                uddi.apiv2.discoveryURL x = new uddi.apiv2.discoveryURL();
                x.useType = discoveryURL[i].useType;
                x.Value = discoveryURL[i].Value;
                names.Add(x);
            }
            return names.ToArray();
        }

        private static uddi.apiv2.keyedReference[] MapCategoryBag(uddi.apiv3.categoryBag categoryBag)
        {
            if (categoryBag == null) return null;
            List<uddi.apiv2.keyedReference> names = new List<uddi.apiv2.keyedReference>();
            for (int i = 0; i < categoryBag.Items.Length; i++)
            {
                if (categoryBag.Items[i] is uddi.apiv3.keyedReference)
                {
                    uddi.apiv3.keyedReference kr = categoryBag.Items[i] as uddi.apiv3.keyedReference;
                    uddi.apiv2.keyedReference x = new uddi.apiv2.keyedReference();
                    x.keyName = kr.keyName;
                    x.keyValue = kr.keyValue;
                    x.tModelKey = kr.tModelKey;
                    names.Add(x);
                }
            }
            return names.ToArray();
        }

        public static uddi.apiv2.delete_business MapDeleteBusiness(uddi.apiv3.delete_business delete_business1)
        {
            uddi.apiv2.delete_business r = new uddi.apiv2.delete_business();
            r.generic = VERSION;
            r.businessKey = delete_business1.businessKey;
            r.authInfo = delete_business1.authInfo;
            return r;
        }

        public static uddi.apiv2.delete_service MapDeleteService(uddi.apiv3.delete_service delete_service1)
        {
            uddi.apiv2.delete_service r = new uddi.apiv2.delete_service();
            r.generic = VERSION;
            r.authInfo = delete_service1.authInfo;
            r.serviceKey = delete_service1.serviceKey;
            return r;
        }

        public static uddi.apiv2.delete_publisherAssertions MapDeletePublisherAssertion(uddi.apiv3.delete_publisherAssertions delete_publisherAssertions1)
        {
            uddi.apiv2.delete_publisherAssertions r = new uddi.apiv2.delete_publisherAssertions();
            r.generic = VERSION;
            r.authInfo = delete_publisherAssertions1.authInfo;
            r.publisherAssertion = MapPublisherAssertions(delete_publisherAssertions1.publisherAssertion);
            return r;
        }

        private static uddi.apiv2.publisherAssertion[] MapPublisherAssertions(uddi.apiv3.publisherAssertion[] publisherAssertion)
        {
            if (publisherAssertion == null) return null;
            List<uddi.apiv2.publisherAssertion> r = new List<uddi.apiv2.publisherAssertion>();
            for (int i = 0; i < publisherAssertion.Length; i++)
            {
                uddi.apiv2.publisherAssertion x = new uddi.apiv2.publisherAssertion();
                x.fromKey = publisherAssertion[i].fromKey;
                x.toKey = publisherAssertion[i].toKey;
                if (publisherAssertion[i].keyedReference != null)
                {
                    x.keyedReference = new uddi.apiv2.keyedReference();
                    x.keyedReference.tModelKey = publisherAssertion[i].keyedReference.tModelKey;
                    x.keyedReference.keyName = publisherAssertion[i].keyedReference.keyName;
                    x.keyedReference.keyValue = publisherAssertion[i].keyedReference.keyValue;
                }
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv2.delete_tModel MapDeleteTModel(uddi.apiv3.delete_tModel delete_tModel1)
        {
            uddi.apiv2.delete_tModel r = new uddi.apiv2.delete_tModel();
            r.generic = VERSION;
            r.authInfo = delete_tModel1.authInfo;
            r.tModelKey = delete_tModel1.tModelKey;
            return r;
        }

        public static uddi.apiv2.get_assertionStatusReport MapGetAssertionStatusReport(uddi.apiv3.get_assertionStatusReport get_assertionStatusReport1)
        {
            if (get_assertionStatusReport1 == null) return null;
            uddi.apiv2.get_assertionStatusReport r = new uddi.apiv2.get_assertionStatusReport();
            r.authInfo = get_assertionStatusReport1.authInfo;
            r.generic = VERSION;
            if (get_assertionStatusReport1.completionStatus!=null);
            switch (get_assertionStatusReport1.completionStatus)
            {
                case uddi.apiv3.completionStatus.statusboth_incomplete:
                    r.completionStatus=(null);
                    break;
                case uddi.apiv3.completionStatus.statuscomplete:
                    r.completionStatus=("status:complete");
                    break;
                case uddi.apiv3.completionStatus.statusfromKey_incomplete:
                    r.completionStatus=("status:fromKey_incomplete");
                    break;
                case uddi.apiv3.completionStatus.statustoKey_incomplete:
                    r.completionStatus=("status:toKey_incomplete");
                    break;
            }

            return r;
        }

        public static uddi.apiv2.get_publisherAssertions MapGetPublisherAssertions(uddi.apiv3.get_publisherAssertions get_publisherAssertions1)
        {
            if (get_publisherAssertions1 == null) return null;
            uddi.apiv2.get_publisherAssertions r = new uddi.apiv2.get_publisherAssertions();

            r.authInfo = get_publisherAssertions1.authInfo;
            r.generic = VERSION;
            return r;
        }

        public static uddi.apiv2.find_service MapFindService(uddi.apiv3.find_service find_service1)
        {
            uddi.apiv2.find_service r = new uddi.apiv2.find_service();
            r.businessKey = find_service1.businessKey;
            r.categoryBag = MapCategoryBag(find_service1.categoryBag);
            r.findQualifiers = MapFindQualifiers(find_service1.findQualifiers);
            r.generic = VERSION;
            r.maxRows = find_service1.maxRows;
            r.maxRowsSpecified = find_service1.maxRowsSpecified;
            r.name = MapName(find_service1.name);
            r.tModelBag = find_service1.tModelBag;
            return r;
        }

        public static uddi.apiv2.find_relatedBusinesses MapFindRelatedBusinesses(uddi.apiv3.find_relatedBusinesses find_relatedBusinesses1)
        {
            uddi.apiv2.find_relatedBusinesses r = new uddi.apiv2.find_relatedBusinesses();
            r.businessKey = find_relatedBusinesses1.ItemElementName == uddi.apiv3.ItemChoiceType.businessKey ? find_relatedBusinesses1.Item : "";
            r.findQualifiers = MapFindQualifiers(find_relatedBusinesses1.findQualifiers);
            r.generic = VERSION;
            r.keyedReference = MapKeyRef(find_relatedBusinesses1.keyedReference);
            r.maxRows = find_relatedBusinesses1.maxRows;
            r.maxRowsSpecified = find_relatedBusinesses1.maxRowsSpecified;
            return r;
        }

        private static uddi.apiv2.keyedReference MapKeyRef(uddi.apiv3.keyedReference keyedReference)
        {
            if (keyedReference == null) return null;
            uddi.apiv2.keyedReference r = new uddi.apiv2.keyedReference();
            r.keyName = keyedReference.keyName;
            r.keyValue = keyedReference.keyValue;
            r.tModelKey = keyedReference.tModelKey;
            return r;
        }

        public static uddi.apiv2.find_tModel MapFindTModel(uddi.apiv3.find_tModel find_tModel1)
        {
            uddi.apiv2.find_tModel r = new uddi.apiv2.find_tModel();
            r.generic = VERSION;
            r.categoryBag = MapCategoryBag(find_tModel1.categoryBag);
            r.findQualifiers = MapFindQualifiers(find_tModel1.findQualifiers);
            r.identifierBag = MapIdentBag(find_tModel1.identifierBag);
            r.maxRows = find_tModel1.maxRows;
            r.maxRowsSpecified = find_tModel1.maxRowsSpecified;
            if (find_tModel1.name != null)
            {
                r.name = new uddi.apiv2.name();
                r.name.Value = find_tModel1.name.Value;
                r.name.lang = find_tModel1.name.lang;
            }
            return r;
        }

        private static uddi.apiv2.keyedReference[] MapIdentBag(uddi.apiv3.keyedReference[] keyedReference)
        {
            if (keyedReference == null) return null;
            List<uddi.apiv2.keyedReference> names = new List<uddi.apiv2.keyedReference>();
            for (int i = 0; i < keyedReference.Length; i++)
            {
                uddi.apiv2.keyedReference x = new uddi.apiv2.keyedReference();
                x.keyName = keyedReference[i].keyName;
                x.keyValue = keyedReference[i].keyValue;
                x.tModelKey = keyedReference[i].tModelKey;
                names.Add(x);
            }
            return names.ToArray();
        }

        public static uddi.apiv2.get_bindingDetail MapGetBindingDetail(uddi.apiv3.get_bindingDetail get_bindingDetail1)
        {
            uddi.apiv2.get_bindingDetail r = new uddi.apiv2.get_bindingDetail();
            r.generic = VERSION;
            r.bindingKey = get_bindingDetail1.bindingKey;
            return r;
        }

        public static uddi.apiv2.get_serviceDetail MapGetServiceDetail(uddi.apiv3.get_serviceDetail get_serviceDetail1)
        {
            uddi.apiv2.get_serviceDetail r = new uddi.apiv2.get_serviceDetail();
            r.generic = VERSION;
            r.serviceKey = get_serviceDetail1.serviceKey;
            return r;
        }

        public static uddi.apiv2.get_tModelDetail MapGetTModelDetail(uddi.apiv3.get_tModelDetail get_tModelDetail1)
        {
            uddi.apiv2.get_tModelDetail r = new uddi.apiv2.get_tModelDetail();
            r.generic = VERSION;
            r.tModelKey = get_tModelDetail1.tModelKey;
            return r;
        }

        public static uddi.apiv2.get_registeredInfo MapRegisteredInfo(uddi.apiv3.get_registeredInfo get_registeredInfo1)
        {
            if (get_registeredInfo1 == null) return null;
            uddi.apiv2.get_registeredInfo r = new uddi.apiv2.get_registeredInfo();
            r.generic = VERSION;
            r.authInfo = get_registeredInfo1.authInfo;
            return r;
        }
    }
}
