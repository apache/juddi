using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.mapping
{
    class MapUDDIv2to3
    {
        public static uddi.apiv3.bindingDetail MapBindingDetail(uddi.apiv2.bindingDetail bindingDetail)
        {
            if (bindingDetail == null) return null;
            uddi.apiv3.bindingDetail r = new uddi.apiv3.bindingDetail();

            r.bindingTemplate = MapBinding(bindingDetail.bindingTemplate);
            r.truncated = bindingDetail.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = bindingDetail.truncatedSpecified;
            return r;
        }

        public static uddi.apiv3.businessList MapBusinessList(uddi.apiv2.businessList businessList)
        {
            if (businessList == null) return null;
            uddi.apiv3.businessList r = new uddi.apiv3.businessList();
            r.truncated = (businessList.truncated != null && businessList.truncated == uddi.apiv2.truncated.@true);
            r.truncatedSpecified = businessList.truncatedSpecified;
            r.listDescription = new uddi.apiv3.listDescription();

            r.businessInfos = MapBusinessInfo(businessList.businessInfos);
            return r;
        }

        public static uddi.apiv3.businessInfo[] MapBusinessInfo(uddi.apiv2.businessInfo[] businessInfo)
        {
            if (businessInfo == null)
                return null;
            List<uddi.apiv3.businessInfo> r = new List<uddi.apiv3.businessInfo>();
            for (int i = 0; i < businessInfo.Length; i++)
            {
                uddi.apiv3.businessInfo x = new uddi.apiv3.businessInfo();
                x.businessKey = businessInfo[i].businessKey;
                x.description = MapDescription(businessInfo[i].description);
                x.name = MapNames(businessInfo[i].name);
                x.serviceInfos = MapServiceInfos(businessInfo[i].serviceInfos);
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv3.serviceInfo[] MapServiceInfos(uddi.apiv2.serviceInfo[] serviceInfo)
        {
            if (serviceInfo == null) return null;
            List<uddi.apiv3.serviceInfo> r = new List<uddi.apiv3.serviceInfo>();
            for (int i = 0; i < serviceInfo.Length; i++)
            {
                uddi.apiv3.serviceInfo x = new uddi.apiv3.serviceInfo();
                x.businessKey = serviceInfo[i].businessKey;
                x.serviceKey = serviceInfo[i].serviceKey;
                x.name = MapNames(serviceInfo[i].name);
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv3.name[] MapNames(uddi.apiv2.name[] name)
        {
            if (name == null) return null;
            List<uddi.apiv3.name> r = new List<uddi.apiv3.name>();
            for (int i = 0; i < name.Length; i++)
            {
                uddi.apiv3.name x = new uddi.apiv3.name();
                x.Value = name[i].Value;
                x.lang = name[i].lang;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv3.description[] MapDescription(uddi.apiv2.description[] description)
        {
            if (description == null) return null;
            List<uddi.apiv3.description> r = new List<uddi.apiv3.description>();
            for (int i = 0; i < description.Length; i++)
            {
                uddi.apiv3.description x = new uddi.apiv3.description();
                x.Value = description[i].Value;
                x.lang = description[i].lang;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv3.assertionStatusItem[] MapAssertionStatusItems(uddi.apiv2.assertionStatusReport assertionStatusReport)
        {
            if (assertionStatusReport == null || assertionStatusReport.assertionStatusItem == null)
                return null;
            List<uddi.apiv3.assertionStatusItem> r = new List<uddi.apiv3.assertionStatusItem>();
            for (int i = 0; i < assertionStatusReport.assertionStatusItem.Length; i++)
            {
                uddi.apiv3.assertionStatusItem x = new assertionStatusItem();
                x.fromKey = assertionStatusReport.assertionStatusItem[i].fromKey;
                x.toKey = assertionStatusReport.assertionStatusItem[i].toKey;
                x.keysOwned = MapKeysOwned(assertionStatusReport.assertionStatusItem[i].keysOwned);
                if (assertionStatusReport.assertionStatusItem[i].keyedReference != null)
                {
                    x.keyedReference = new keyedReference(assertionStatusReport.assertionStatusItem[i].keyedReference.tModelKey,
                        assertionStatusReport.assertionStatusItem[i].keyedReference.keyName,
                        assertionStatusReport.assertionStatusItem[i].keyedReference.keyValue);

                }
                switch (assertionStatusReport.assertionStatusItem[i].completionStatus.ToLower())
                {
                    case "status:complete":
                        x.completionStatus = completionStatus.statuscomplete;
                        break;
                    case "status:toKey_incomplete":
                        x.completionStatus = completionStatus.statustoKey_incomplete;
                        break;
                    case "status:fromKey_incomplete":
                        x.completionStatus = completionStatus.statusfromKey_incomplete;
                        break;
                    default:
                        x.completionStatus = completionStatus.statusboth_incomplete;
                        break;
                }
                r.Add(x);

            }
            return r.ToArray();
        }

        private static keysOwned MapKeysOwned(uddi.apiv2.keysOwned keysOwned)
        {
            if (keysOwned == null) return null;
            keysOwned r = new uddi.apiv3.keysOwned();
            r.Items = new string[] { keysOwned.fromKey, keysOwned.toKey };
            r.ItemsElementName = new ItemsChoiceType3[] { ItemsChoiceType3.fromKey, ItemsChoiceType3.toKey };
            return r;
        }

        public static uddi.apiv3.publisherAssertion[] MapPublisherAssertions(uddi.apiv2.publisherAssertions publisherAssertions)
        {
            if (publisherAssertions == null) return null;
            return MapPublisherAssertions(publisherAssertions.publisherAssertion);
        }

        public static uddi.apiv3.publisherAssertion[] MapPublisherAssertions(uddi.apiv2.publisherAssertion[] publisherAssertions)
        {
            if (publisherAssertions == null) return null;
            List<uddi.apiv3.publisherAssertion> r = new List<publisherAssertion>();
            for (int i = 0; i < publisherAssertions.Length; i++)
            {
                uddi.apiv3.publisherAssertion x = new publisherAssertion();
                x.fromKey = publisherAssertions[i].fromKey;
                x.toKey = publisherAssertions[i].toKey;
                if (publisherAssertions[i].keyedReference != null)
                    x.keyedReference = new keyedReference(publisherAssertions[i].keyedReference.tModelKey,
                        publisherAssertions[i].keyedReference.keyName, publisherAssertions[i].keyedReference.keyValue);
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv3.serviceList MapServiceList(uddi.apiv2.serviceList serviceList)
        {
            if (serviceList == null)
                return null;
            uddi.apiv3.serviceList r = new serviceList();
            r.listDescription = new listDescription();
            r.listDescription.actualCount = serviceList.serviceInfos.Length;
            r.truncated = serviceList.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = serviceList.truncatedSpecified;
            r.serviceInfos = MapServiceInfos(serviceList.serviceInfos);
            return r;
        }

        public static uddi.apiv3.relatedBusinessesList MapRelatedBusinessList(uddi.apiv2.relatedBusinessesList relatedBusinessesList)
        {
            if (relatedBusinessesList == null)
                return null;
            uddi.apiv3.relatedBusinessesList r = new relatedBusinessesList();
            r.businessKey = relatedBusinessesList.businessKey;
            List<relatedBusinessInfo> x = new List<relatedBusinessInfo>();
            for (int i = 0; i < relatedBusinessesList.relatedBusinessInfos.Length; i++)
            {
                relatedBusinessInfo s = new relatedBusinessInfo();
                s.businessKey = relatedBusinessesList.relatedBusinessInfos[i].businessKey;
                s.description = MapDescription(relatedBusinessesList.relatedBusinessInfos[i].description);
                s.name = MapNames(relatedBusinessesList.relatedBusinessInfos[i].name);
                s.sharedRelationships = MapSharedRelationships(relatedBusinessesList.relatedBusinessInfos[i].sharedRelationships);
                x.Add(s);
            }
            r.relatedBusinessInfos = x.ToArray();
            r.listDescription = new listDescription();

            return r;
        }

        private static sharedRelationships[] MapSharedRelationships(uddi.apiv2.sharedRelationships[] sharedRelationships)
        {
            if (sharedRelationships == null) return null;
            List<sharedRelationships> r = new List<sharedRelationships>();
            for (int i = 0; i < sharedRelationships.Length; i++)
            {
                sharedRelationships x = new sharedRelationships();
                switch (sharedRelationships[i].direction)
                {
                    case uddi.apiv2.direction.fromKey:
                        x.direction = direction.fromKey;
                        break;
                    case uddi.apiv2.direction.toKey:
                        x.direction = direction.toKey;
                        break;
                }
                x.keyedReference = MapIdentifierBag(sharedRelationships[i].keyedReference);
                r.Add(x);
            }
            return r.ToArray();
        }

        public static uddi.apiv3.tModelList MapTModelList(uddi.apiv2.tModelList tModelList)
        {
            if (tModelList == null) return null;
            uddi.apiv3.tModelList r = new tModelList();
            List<tModelInfo> x = new List<tModelInfo>();
            if (tModelList.tModelInfos != null)
                for (int i = 0; i < tModelList.tModelInfos.Length; i++)
                {
                    tModelInfo s = new tModelInfo();
                    s.tModelKey = tModelList.tModelInfos[i].tModelKey;
                    if (tModelList.tModelInfos[i].name != null)
                        s.name = new name(tModelList.tModelInfos[i].name.Value, tModelList.tModelInfos[i].name.lang);
                    x.Add(s);
                }
            r.tModelInfos = x.ToArray();
            r.listDescription = new listDescription();
            r.listDescription.actualCount = r.tModelInfos.Length;
            r.listDescription.includeCount = r.tModelInfos.Length;
            r.truncated = tModelList.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = tModelList.truncatedSpecified;
            return r;
        }

        public static uddi.apiv2.get_businessDetail MapGetBusinessDetail(uddi.apiv3.get_businessDetail get_businessDetail1)
        {
            if (get_businessDetail1 == null) return null;
            uddi.apiv2.get_businessDetail r = new uddi.apiv2.get_businessDetail();

            r.businessKey = get_businessDetail1.businessKey;
            r.generic = MapUDDIv3to2.VERSION;
            return r;
        }

        public static uddi.apiv3.businessDetail MapBusinessDetail(uddi.apiv2.businessDetail businessDetail)
        {
            if (businessDetail == null) return null;
            uddi.apiv3.businessDetail r = new uddi.apiv3.businessDetail();
            List<businessEntity> tx = new List<businessEntity>();
            if (businessDetail.businessEntity != null)
                for (int i = 0; i < businessDetail.businessEntity.Length; i++)
                {
                    tx.Add(MapBusinessEntity(businessDetail.businessEntity[i]));
                }
            r.businessEntity = tx.ToArray();
            r.truncated = businessDetail.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = businessDetail.truncatedSpecified;
            return r;
        }

        public static businessEntity MapBusinessEntity(uddi.apiv2.businessEntity businessEntity)
        {
            if (businessEntity == null) return null;
            businessEntity be = new businessEntity();
            be.businessKey = businessEntity.businessKey;
            be.categoryBag = MapCategoryBag(businessEntity.categoryBag);
            be.contacts = MapContacts(businessEntity.contacts);
            be.description = MapDescription(businessEntity.description);
            be.discoveryURLs = MapDiscoUrls(businessEntity.discoveryURLs);
            be.name = MapNames(businessEntity.name);
            be.identifierBag = MapIdentifierBag(businessEntity.identifierBag);
            be.businessServices = MapBusinessServices(businessEntity.businessServices);
            return be;
        }

        public static businessService[] MapBusinessServices(uddi.apiv2.businessService[] businessService)
        {
            if (businessService == null) return null;
            List<businessService> r = new List<businessService>();
            for (int i = 0; i < businessService.Length; i++)
            {
                businessService x = MapService(businessService[i]);
                r.Add(x);
            }
            return r.ToArray();
        }

        public static businessService MapService(uddi.apiv2.businessService businessService)
        {
            if (businessService == null) return null;
            businessService r = new businessService();
            r.bindingTemplates = MapBinding(businessService.bindingTemplates);
            r.businessKey = businessService.businessKey;
            r.categoryBag = MapCategoryBag(businessService.categoryBag);
            r.description = MapDescription(businessService.description);
            r.name = MapNames(businessService.name);
            r.serviceKey = businessService.serviceKey;
            return r;
        }

        public static bindingTemplate[] MapBinding(uddi.apiv2.bindingTemplate[] bindingTemplate)
        {
            if (bindingTemplate == null) return null;
            List<bindingTemplate> r = new List<bindingTemplate>();
            for (int i = 0; i < bindingTemplate.Length; i++)
            {
                r.Add(MapBindingTemplate(bindingTemplate[i]));
            }
            return r.ToArray();
        }

        public static bindingTemplate MapBindingTemplate(uddi.apiv2.bindingTemplate bindingTemplate)
        {
            if (bindingTemplate == null) return null;
            bindingTemplate r = new bindingTemplate();
            r.bindingKey = bindingTemplate.bindingKey;
            r.description = MapDescription(bindingTemplate.description);
            r.serviceKey = bindingTemplate.serviceKey;
            if (bindingTemplate.Item is org.uddi.apiv2.accessPoint)
            {
                org.uddi.apiv2.accessPoint old = new uddi.apiv2.accessPoint();
                accessPoint a = new accessPoint();
                a.Value = old.Value;
                a.useType = old.URLType.ToString();
                r.Item = a;
            }
            r.tModelInstanceDetails = MapTmodelInstanceDetail(bindingTemplate.tModelInstanceDetails);
            return r;
        }

        public static tModelInstanceInfo[] MapTmodelInstanceDetail(uddi.apiv2.tModelInstanceInfo[] tModelInstanceInfo)
        {
            if (tModelInstanceInfo == null) return null;
            List<tModelInstanceInfo> r = new List<tModelInstanceInfo>();
            for (int i = 0; i < tModelInstanceInfo.Length; i++)
            {
                tModelInstanceInfo x = new tModelInstanceInfo();
                x.description = MapDescription(tModelInstanceInfo[i].description);
                x.tModelKey = tModelInstanceInfo[i].tModelKey;
                x.instanceDetails = MapInstanceDetails(tModelInstanceInfo[i].instanceDetails);
                r.Add(x);
            }
            return r.ToArray();
        }

        public static instanceDetails MapInstanceDetails(uddi.apiv2.instanceDetails instanceDetails)
        {
            if (instanceDetails == null) return null;
            instanceDetails r = new instanceDetails();
            r.description = MapDescription(instanceDetails.description);
            r.instanceParms = instanceDetails.instanceParms;
            r.Items = MapOverviewDoc(instanceDetails.overviewDoc);
            return r;
        }

        public static overviewDoc[] MapOverviewDoc(uddi.apiv2.overviewDoc overviewDoc)
        {

            if (overviewDoc == null) return null;
            overviewDoc x = new overviewDoc();
            x.descriptions = MapDescription(overviewDoc.description);
            x.overviewURLs = MapOverviewUrl(overviewDoc.overviewURL);
            return new overviewDoc[] { x };
        }

        public static overviewURL[] MapOverviewUrl(string p)
        {
            if (p == null) return null;
            overviewURL xx = new overviewURL();
            xx.Value = p;
            return new overviewURL[] { xx };
        }

        public static contact[] MapContacts(uddi.apiv2.contact[] contact)
        {
            if (contact == null) return null;
            List<contact> r = new List<contact>();
            for (int i = 0; i < contact.Length; i++)
            {
                contact x = new uddi.apiv3.contact();
                x.personName = new personName[] { new personName(contact[i].personName, null) };
                x.useType = contact[i].useType;
                x.description = MapDescription(contact[i].description);
                x.email = MapEmail(contact[i].email);
                x.address = MapAddress(contact[i].address);
                x.phone = MapPhone(contact[i].phone);
                r.Add(x);
            }

            return r.ToArray();
        }

        public static phone[] MapPhone(uddi.apiv2.phone[] phone)
        {
            if (phone == null) return null;
            List<phone> r = new List<phone>();
            for (int i = 0; i < phone.Length; i++)
            {
                phone x = new phone();
                x.Value = phone[i].Value;
                x.useType = phone[i].useType;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static address[] MapAddress(uddi.apiv2.address[] address)
        {
            if (address == null) return null;
            List<address> r = new List<address>();
            for (int i = 0; i < address.Length; i++)
            {
                address x = new address();
                x.sortCode = address[i].sortCode;
                x.tModelKey = address[i].tModelKey;
                x.addressLine = MapAddressLine(address[i].addressLine);
                x.useType = address[i].useType;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static addressLine[] MapAddressLine(uddi.apiv2.addressLine[] addressLine)
        {
            if (addressLine == null) return null;
            List<addressLine> r = new List<addressLine>();
            for (int i = 0; i < addressLine.Length; i++)
            {
                addressLine x = new addressLine();
                x.Value = addressLine[i].Value;
                x.keyValue = addressLine[i].keyValue;
                x.keyName = addressLine[i].keyName;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static email[] MapEmail(uddi.apiv2.email[] email)
        {
            if (email == null) return null;
            List<email> r = new List<email>();
            for (int i = 0; i < email.Length; i++)
            {
                email x = new email();
                x.Value = email[i].Value;
                x.useType = email[i].useType;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static discoveryURL[] MapDiscoUrls(uddi.apiv2.discoveryURL[] discoveryURL)
        {
            if (discoveryURL == null) return null;
            List<discoveryURL> r = new List<discoveryURL>();
            for (int i = 0; i < discoveryURL.Length; i++)
            {
                discoveryURL x = new discoveryURL();
                x.Value = discoveryURL[i].Value;
                x.useType = discoveryURL[i].useType;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static keyedReference[] MapIdentifierBag(uddi.apiv2.keyedReference[] keyedReference)
        {
            if (keyedReference == null)
                return null;
            List<keyedReference> r = new List<keyedReference>();
            for (int i = 0; i < keyedReference.Length; i++)
            {
                keyedReference x = new keyedReference();
                x.tModelKey = keyedReference[i].tModelKey;
                x.keyValue = keyedReference[i].keyValue;
                x.keyName = keyedReference[i].keyName;
                r.Add(x);
            }
            return r.ToArray();
        }

        public static categoryBag MapCategoryBag(uddi.apiv2.keyedReference[] keyedReference)
        {
            if (keyedReference == null || keyedReference.Length == 0)
                return null;
            categoryBag c = new categoryBag();
            c.Items = MapIdentifierBag(keyedReference);
            return c;
        }

        public static uddi.apiv3.serviceDetail MapServiceDetail(uddi.apiv2.serviceDetail serviceDetail)
        {
            if (serviceDetail == null) return null;
            uddi.apiv3.serviceDetail r = new serviceDetail();
            r.businessService = MapBusinessServices(serviceDetail.businessService);
            r.truncated = serviceDetail.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = serviceDetail.truncatedSpecified;
            return r;
        }

        public static uddi.apiv3.tModelDetail MapTModelDetail(uddi.apiv2.tModelDetail tModelDetail)
        {
            if (tModelDetail == null) return null;
            uddi.apiv3.tModelDetail r = new tModelDetail();
            r.truncated = tModelDetail.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = tModelDetail.truncatedSpecified;
            r.tModel = MapTModels(tModelDetail.tModel);
            return r;
        }

        public static tModel[] MapTModels(uddi.apiv2.tModel[] tModel)
        {
            if (tModel == null) return null;
            List<tModel> r = new List<tModel>();
            for (int i = 0; i < tModel.Length; i++)
            {
                tModel x = new tModel();
                x.categoryBag = MapCategoryBag(tModel[i].categoryBag);
                x.deleted = false;
                x.description = MapDescription(tModel[i].description);
                x.identifierBag = MapIdentifierBag(tModel[i].identifierBag);
                x.name = new name(tModel[i].name.Value, tModel[i].name.lang);
                x.tModelKey = tModel[i].tModelKey;
                r.Add(x);
            }

            return r.ToArray();
        }

        public static registeredInfo MapRegisteredInfo(uddi.apiv2.registeredInfo registeredInfo)
        {
            if (registeredInfo == null) return null;
            registeredInfo r = new registeredInfo();
            r.businessInfos = MapBusinessInfo(registeredInfo.businessInfos);
            r.truncated = registeredInfo.truncated == uddi.apiv2.truncated.@true;
            r.truncatedSpecified = registeredInfo.truncatedSpecified;

            List<tModelInfo> x = new List<tModelInfo>();
            if (registeredInfo.tModelInfos != null)
            {
                for (int i = 0; i < registeredInfo.tModelInfos.Length; i++)
                {
                    tModelInfo t = new tModelInfo();
                    t.tModelKey = registeredInfo.tModelInfos[i].tModelKey;
                    if (registeredInfo.tModelInfos[i].name != null)
                        t.name = new name(
                            registeredInfo.tModelInfos[i].name.Value, registeredInfo.tModelInfos[i].name.lang);
                    x.Add(t);
                }
            }
            r.tModelInfos = x.ToArray();
            return r;
        }
    }
}
