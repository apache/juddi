using org.apache.juddi.client.org.apache.juddi.v3.client.mapping;
using org.apache.juddi.v3.client.mapping;
using org.uddi.apiv2;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.transport.wrapper
{
    /// <summary>
    /// This class provides a wrapper to enable UDDIv3 clients to talk to UDDIv2 servers
    ///  via JAXWS Transport. It handles all translations for Inquiry service methods.
    ///  @author <a href="alexoree@apache.org">Alex O'Ree</a>
    ///  @since 3.2.1
    /// </summary>
    public class Inquiry3to2 : UDDI_Inquiry_SoapBinding, IDisposable
    {

        private InquireSoap inquiry = new InquireSoap();
        public Inquiry3to2(string endpointURL)
        {
            // TODO: Complete member initialization
            this.Url = endpointURL;
        }

        public Inquiry3to2()
        {

        }

        public  void Dispose()
        {
            if (inquiry != null)
            {
                inquiry.Dispose();
                inquiry = null;
            }
        }

        private void Init()
        {
            inquiry.Url = this.Url;
        }

        public override org.uddi.apiv3.bindingDetail find_binding(org.uddi.apiv3.find_binding find_binding1)
        {
            Init();
            return MapUDDIv2to3.MapBindingDetail(inquiry.find_binding(MapUDDIv3to2.MapFindBinding(find_binding1)));
        }

        public override org.uddi.apiv3.businessList find_business(org.uddi.apiv3.find_business fb)
        {
            Init();
            return MapUDDIv2to3.MapBusinessList(inquiry.find_business(MapUDDIv3to2.MapFindBusiness(fb)));
        }

        public override uddi.apiv3.serviceList find_service(uddi.apiv3.find_service find_service1)
        {
            Init();
            return MapUDDIv2to3.MapServiceList(inquiry.find_service(MapUDDIv3to2.MapFindService(find_service1)));
        }
        public override uddi.apiv3.relatedBusinessesList find_relatedBusinesses(uddi.apiv3.find_relatedBusinesses find_relatedBusinesses1)
        {
            Init();
            return MapUDDIv2to3.MapRelatedBusinessList(inquiry.find_relatedBusinesses(MapUDDIv3to2.MapFindRelatedBusinesses(find_relatedBusinesses1)));
        }
        public override uddi.apiv3.tModelList find_tModel(uddi.apiv3.find_tModel find_tModel1)
        {
            Init();
            return MapUDDIv2to3.MapTModelList(inquiry.find_tModel(MapUDDIv3to2.MapFindTModel(find_tModel1)));
        }
        public override uddi.apiv3.bindingDetail get_bindingDetail(uddi.apiv3.get_bindingDetail get_bindingDetail1)
        {
            Init();
            return MapUDDIv2to3.MapBindingDetail(inquiry.get_bindingDetail(MapUDDIv3to2.MapGetBindingDetail(get_bindingDetail1)));
        }

        public override uddi.apiv3.businessDetail get_businessDetail(uddi.apiv3.get_businessDetail get_businessDetail1)
        {
            Init();
            return MapUDDIv2to3.MapBusinessDetail(inquiry.get_businessDetail(MapUDDIv2to3.MapGetBusinessDetail(get_businessDetail1)));
        }
        public static readonly String VERSION = "2.0";
        public override operationalInfos get_operationalInfo(get_operationalInfo get_operationalInfo1)
        {
            Init();
            operationalInfos ret = new operationalInfos();
            List<operationalInfo> r = new List<operationalInfo>();
            for (int i = 0; i < get_operationalInfo1.entityKey.Length; i++)
            {
                operationalInfo oi = new operationalInfo();
                oi.entityKey=(get_operationalInfo1.entityKey[i]);
                try
                {
                    org.uddi.apiv2.get_businessDetail businessDetail = new org.uddi.apiv2.get_businessDetail();
                    businessDetail.generic=(VERSION);
                    businessDetail.businessKey = new string[]{get_operationalInfo1.entityKey[i]};
                    org.uddi.apiv2.businessDetail z = inquiry.get_businessDetail(businessDetail);
                    oi.nodeID=(z.@operator);
                    oi.authorizedName=(z.businessEntity[0].authorizedName);
                }
                catch (Exception ex)
                {
                }
                if (oi.authorizedName != null)
                {
                    r.Add(oi);
                    continue;
                }
                try
                {
                    org.uddi.apiv2.get_tModelDetail tModelDetail = new org.uddi.apiv2.get_tModelDetail();
                    tModelDetail.generic=(VERSION);
                    tModelDetail.tModelKey = new string[] { get_operationalInfo1.entityKey[i] };
                    org.uddi.apiv2.tModelDetail z = inquiry.get_tModelDetail(tModelDetail);
                    oi.nodeID=(z.@operator);
                    oi.authorizedName=(z.tModel[0].authorizedName);
                }
                catch (Exception ex)
                {
                }
                if (oi.authorizedName != null)
                {
                    r.Add(oi);
                    continue;
                }
                try
                {
                    //get the service
                    org.uddi.apiv2.get_serviceDetail serviceDetail = new org.uddi.apiv2.get_serviceDetail();
                    serviceDetail.generic=(VERSION);
                    serviceDetail.serviceKey = new string[] { get_operationalInfo1.entityKey[i] };
                    org.uddi.apiv2.serviceDetail z = inquiry.get_serviceDetail(serviceDetail);
                    oi.nodeID=(z.@operator);

                    org.uddi.apiv2.get_businessDetail businessDetail = new org.uddi.apiv2.get_businessDetail();
                    businessDetail.generic=(VERSION);
                    //its owning business
                    businessDetail.businessKey = new string[] { z.businessService[0].businessKey };
                    org.uddi.apiv2.businessDetail z2 = inquiry.get_businessDetail(businessDetail);
                    oi.nodeID = (z2.@operator);
                    oi.authorizedName=(z2.businessEntity[0].authorizedName);
                    r.Add(oi);
                }
                catch (Exception ex)
                {
                }

                
            }
            ret.truncated = (false);
            ret.operationalInfo = r.ToArray();
            return ret;


            
        }
        public override uddi.apiv3.serviceDetail get_serviceDetail(uddi.apiv3.get_serviceDetail get_serviceDetail1)
        {
            Init();
            return MapUDDIv2to3.MapServiceDetail(inquiry.get_serviceDetail(MapUDDIv3to2.MapGetServiceDetail(get_serviceDetail1)));
        }
        public override uddi.apiv3.tModelDetail get_tModelDetail(uddi.apiv3.get_tModelDetail get_tModelDetail1)
        {
            Init();
            return MapUDDIv2to3.MapTModelDetail(inquiry.get_tModelDetail(MapUDDIv3to2.MapGetTModelDetail(get_tModelDetail1)));
        }

    }
}
