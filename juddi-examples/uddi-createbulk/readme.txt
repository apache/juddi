UDDI-Bulk Creator

This project has a wide variety of sample projects. Here's a rough outline of what's available.
requires the Apache CXF Runtime, uddi-ws and juddi-client

ant jar
java -cp dist/*;dist/lib/* uddi.createbulk.<class goes here>

UddiCreatebulk - makes 15 businesses, 5 services each
UddiCustodyTransfer - an example of how to transfer a business intra-node
UddiDigitalSignatureBusiness - how to sign a business entity and all child elements and validate it
UddiDigitalSignatureSearch - how to search for all signed items
UddiDigitalSignatureService
UddiDigitalSignatureTmodel
UddiFindBidning
UddiRelatedBusinesses - creates two businesses and sets up a relationship between the two (publisher assertion)
UddiSubscribe - how to setup a Subscription Callback API service. Requires Apache CXF + Jetty libraries
UddiSubscribeValidate - how to setup a subscription for manual pulls
