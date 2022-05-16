## Digital Signatures

Users of UDDI can use digital signatures to ensure that no unauthorized users alter the content of UDDI. We're sure that one of the first questions one would ask is "can't access control rules handle this problem for us?" The answer is yes, however it does not mitigate the risk of a number of opportunities for electronic attack.

### Requirements

UDDI supports both the XML Digital Signature specification, which effectively means that you can use PGP Keys and X509 certificates. jUDDI provides out of the box support for X509 certificates and the Public Key Infrastructure (PKI). If you require direct PGP signing support, please open a JIRA ticket.

### Using Digital Signatures using the jUDDI GUI

Please see the jUDDI Client Guide.

### Frequently Asked Questions

Doesn't UDDI access control rules prevent alteration of the content?::
Yes, however it does not mitigate the man in the middle attack vectors. Since UDDI is used to determine the location of the thing you want, it's possible that falsified endpoints can be interjected in transport. The target service requires authentication, then the end user's credentials could be compromised without their knowledge.

How can I sign a business, service, tModel or binding?::
 Use the juddi-gui's digital signature applet by first located the item in the juddi-gui interface, then click on the "Sign" button. You need write access to the entity.

The digital signature applet doesn't run. Now what?::
 The applet requires the Java browser plugin. Unfortunately, due to recent (2013) security vulnerabilities, many places of business have heeded Oracle's advice and have disabled the browser plugin. There are other options, however.

What other tools can I use to sign a UDDI entity?::

TBD

What is a signature?::
 It's basically a cryptographic (a fancy math equation) using a set a keys (one is public and everyone can see/know it, the other only is held by the owner) that proves that the owner signed a piece of data.

How is a signature verified?::
There's a few ways, we can prove mathematically that the signature is valid (the content hasn't been modified). From there we can also verify that the signing key is valid.

How do we know the signing key is valid?::
Most certificates (key pairs) have some kind of mechanism in it to verify if the certificate has been revoked. If your certificate has it, it will be labeled with something like OCSP or CRL. Both of these are supported in both .NET and Java juddi-clients as well as via the juddi-gui.