using org.apache.juddi.v3.client.mapping;
using org.uddi.apiv2;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.transport.wrapper
{
    public class Publish3to2 : UDDI_Publication_SoapBinding
    {
        public static readonly String VERSION = "2.0";
        private string endpointURL;
        private PublishSoap publish = new PublishSoap();
        public Publish3to2(string endpointURL)
        {
            // TODO: Complete member initialization
            this.endpointURL = endpointURL;
        }

        public Publish3to2()
        {
            // TODO: Complete member initialization
        }
        private void Init()
        {
            publish.Url = this.Url;
        }
        public override void delete_business(uddi.apiv3.delete_business delete_business1)
        {
            Init();
            publish.delete_business(MapUDDIv3to2.MapDeleteBusiness(delete_business1));
            
        }
        public override void delete_service(uddi.apiv3.delete_service delete_service1)
        {
            Init();
            publish.delete_service(MapUDDIv3to2.MapDeleteService(delete_service1));
        }
        public override void delete_publisherAssertions(uddi.apiv3.delete_publisherAssertions delete_publisherAssertions1)
        {
            Init();
            publish.delete_publisherAssertions(MapUDDIv3to2.MapDeletePublisherAssertion(delete_publisherAssertions1));
        }
        public override void delete_tModel(uddi.apiv3.delete_tModel delete_tModel1)
        {
            Init();
            publish.delete_tModel(MapUDDIv3to2.MapDeleteTModel(delete_tModel1));
            
        }
        public override uddi.apiv3.assertionStatusItem[] get_assertionStatusReport(uddi.apiv3.get_assertionStatusReport get_assertionStatusReport1)
        {
            Init();
            return MapUDDIv2to3.MapAssertionStatusItems(publish.get_assertionStatusReport(MapUDDIv3to2.MapGetAssertionStatusReport(get_assertionStatusReport1)));
            
        }
        public override uddi.apiv3.registeredInfo get_registeredInfo(uddi.apiv3.get_registeredInfo get_registeredInfo1)
        {
            Init();
            return MapUDDIv2to3.MapRegisteredInfo(publish.get_registeredInfo(MapUDDIv3to2.MapRegisteredInfo(get_registeredInfo1)));
            
        }
        public override uddi.apiv3.publisherAssertion[] get_publisherAssertions(uddi.apiv3.get_publisherAssertions get_publisherAssertions1)
        {
            Init();
            return MapUDDIv2to3.MapPublisherAssertions(publish.get_publisherAssertions(MapUDDIv3to2.MapGetPublisherAssertions(get_publisherAssertions1)));
        }

    }
}
