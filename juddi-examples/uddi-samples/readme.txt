UDDI-Samples

This project has a wide variety of sample projects. Here's a rough outline of what's available.
requires the Apache CXF Runtime, uddi-ws and juddi-client

ant jar
java -cp dist/*;dist/lib/* uddi.examples.<class goes here>

AES - encryption example 
DES - encryption example 
UddiCreatebulk - makes 15 businesses, 5 services each
UddiCustodyTransfer - an example of how to transfer a business intra-node
UddiDigitalSignatureBusiness - how to sign a business entity and all child elements and validate it
UddiDigitalSignatureSearch - how to search for all signed items
UddiDigitalSignatureService - how to sign a service
UddiDigitalSignatureTmodel - how to sign a tmodel
UddiFindBinding - how to find a binding (pist, there is no find_binding API)
UddiFindEndpoints - how to find and resolve endpoints for a specific service
UddiGetServiceDetails - how to get the details of a service
UddiKeyGenerator - how to make a key generator
UddiRelatedBusinesses - creates two businesses and sets up a relationship between the two (publisher assertion)
UddiSubscribe - how to setup a Subscription Callback API service. Requires Apache CXF + Jetty libraries
UddiSubscribeValidate - how to setup a subscription for manual pulls
UddiSubscribeGet - how to get all of my subscriptions
WsdlImport - how to register a service by wsdl
