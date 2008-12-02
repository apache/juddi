Notes on generation the UDDI web service stubs and schema bindings
------------------------------------------------------------------

- The files in the resource directory were retrieved from the following link:
http://www.oasis-open.org/committees/uddi-spec/doc/tcspecs.htm#uddiv3
All Schemas and Service Interface WSDL files were added to this directory.

- The file uddi_v3_service.wsdl was created from the various interfaces.  This is the file that is keyed on by the 'wsimport' utility.  All other files
are involved via inclusion.

- The Policy and Policy Instance Parms schemas are not included in any of the Service Interface WSDLs.  Therefore, they were not getting generated.
As a result, the uddi_api_v3_portType.wsdl file was MODIFIED to include those schemas for the sole purpose of generating the artifacts.  The following 
import lines were added to the schema tag in this file:

<xsd:import namespace="urn:uddi-org:policy_v3" schemaLocation="uddi_v3policy.xsd"/>
<xsd:import namespace="urn:uddi-org:policy_v3_instanceParms" schemaLocation="uddi_v3policy_instanceParms.xsd"/>

- In util\copylic\ there is a DOS batch file, copylic.bat that will copy the lic.txt file on top of each java file in the generated target directory and
move them over to the appropriate source folder under uddi-ws.

- JAX-WS produced an error when generating the WSDL for the publication SEI.  This is because setPublisherAssertions and getPublisherAsertions both were
generated with a ResponseWrapper attribute with the same localName and className.  To fix this, the return type of getPublisherAssertions was changed
slightly.  Basically, the word "Response" was appended to message and type for this operation.