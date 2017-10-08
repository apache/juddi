## Using the jUDDI REST Services

jUDDI includes a Inquiry API adapter that exposes some of the basic functionality of UDDI via REST. Data can be retrieved in both XML and JSON encoding for all methods.

### URL Patterns and methods

. All jUDDI Inquiry REST service are accessible via HTTP GET. 
. Authentication is not yet supported. This also implies that the Inquiry API must be configured for anonymous access (i.e. do not turn on Inquiry Authentication Required).
. The jUDDI Inquiry REST service is not currently portable as an adapter to other UDDI instances (but it could be adapted to it in the future)

#### Endpoints

All endpoints must be prefixed with http(s)://server:port/juddicontext/ where 'juddicontext' is typically 'juddiv3'.

WADL Document: http://localhost:8080/juddiv3/services/inquiryRest?_wadl

TIP: All of the examples in this document reference JSON encoded messages. To switch to XML messages, just replace the 'JSON' with 'XML' in the URL. That's it!

#### Methods

Each method is accessible using the following pattern:

````
http://localhost:8080/juddiv3/services/inquiryRest/{encoding}/{method}/{parameters}
//or
http://localhost:8080/juddiv3/services/inquiryRest/{encoding}/{method}?{name=value}
````
Notes

- Encoding - Can be 'XML' or 'JSON'
- Methods - See below
- Parameters - usually a unique UDDI key 

##### xxxList

Returns up to 100 items within a KeyBag object, containing a list of all keys for the given object type.

- serviceList - http://localhost:8080/juddiv3/services/inquiryRest/JSON/serviceList
- businessList - http://localhost:8080/juddiv3/services/inquiryRest/JSON/businessList
- tModelList - http://localhost:8080/juddiv3/services/inquiryRest/JSON/tModelList

##### endpointsByService/key

Returns all executable endpoints for a given service key, including all binding Templates. This also resolves hosting redirector and a number of other accessPoint useType specifics.

Example: 

````
http://localhost:8080/juddiv3/services/inquiryRest/JSON/endpointsByService/uddi:juddi.apache.org:services-custodytransfer
````

##### getDetail

Return the details of a specific item using query parameters. This implements the UDDI recommendation for HTTP GET services for UDDI. See http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908158 for further information.

Example: 

````
http://localhost:8080/juddiv3/services/inquiryRest/XML/getDetail?businessKey=uddi:juddi.apache.org:businesses-asf
````

The following query parameters are supported. Only one can be specified at a time

- businessKey/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/getDetail?businessKey=uddi:juddi.apache.org:businesses-asf
- tModelKey/(key) -  http://localhost:8080/juddiv3/services/inquiryRest/JSON/getDetail?tModelKey=uddi:uddi.org:categorization:types
- bindingKey/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/getDetail?bindingKey=uddi:juddi.apache.org:servicebindings-inquiry-ws
- serviceKey/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/getDetail?serviceKey=uddi:juddi.apache.org:services-inquiry

##### xxxKey

Return the details of a specific item. This is similar to getDetail except that it is not based on query parameters. The underlying code of this function is the same as getDetail.

Example:

- businessKey/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/businessKey/uddi:juddi.apache.org:businesses-asf
- tModelKey/(key) -  http://localhost:8080/juddiv3/services/inquiryRest/JSON/tModelKey/uddi:uddi.org:categorization:types
- bindingKey/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/bindingKey/uddi:juddi.apache.org:servicebindings-inquiry-ws
- serviceKey/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/serviceKey/uddi:juddi.apache.org:services-inquiry
- opInfo/(key) - http://localhost:8080/juddiv3/services/inquiryRest/JSON/opInfo/uddi:juddi.apache.org:businesses-asf

##### xxxSearch

Returns the search results for registered entities in XML or JSON using a number of query parameters.

Supported entities:

- searchService
- searchBusiness
- searchTModel

Supported query parameters
 
- name - Filters by the name element. If not specified, the wildcard symbol is used '%'.
- lang - Filters by language. If not specified, null is used.
- findQualifiers - Adds sorting or additional find parameters. comma delimited. If not specified, 'approximateMatch' is used
- maxrows - Maximum rows returned. If not specified, 100 is used.
- offset - Offset for paging operations. If not specified, 0 is used.

### Example Output


#### XML

The output of all XML encoded messages is identical to the UDDI schema specifications. There should be no surprises.

#### JSON

The output of JSON encoded messages is obviously different than XML. The following is an example of what it looks like.

````json
{
    "businessEntity": {
        "@businessKey": "uddi:juddi.apache.org:businesses-asf",
        "discoveryURLs": {
            "discoveryURL": {
                "@useType": "homepage",
                "$": "http://localhost:8080/juddiv3"
            }
        },
        "name": {
            "@xml.lang": "en",
            "$": "An Apache jUDDI Node"
        },
        "description": {
            "@xml.lang": "en",
            "$": "This is a UDDI v3 registry node as implemented by Apache jUDDI."
        },
        "businessServices": {
            "businessService": [
                {
                    "@serviceKey": "uddi:juddi.apache.org:services-custodytransfer",
                    "@businessKey": "uddi:juddi.apache.org:businesses-asf",
                    "name": {
                        "@xml.lang": "en",
                        "$": "UDDI Custody and Ownership Transfer Service"
                    },
                    "description": {
                        "@xml.lang": "en",
                        "$": "Web Service supporting UDDI Custody and Ownership Transfer API"
                    },
                    "bindingTemplates": {
                        "bindingTemplate": [
                            {
                                "@bindingKey": "uddi:juddi.apache.org:servicebindings-custodytransfer-ws",
                                "@serviceKey": "uddi:juddi.apache.org:services-custodytransfer",
                                "description": "UDDI Custody and Ownership Transfer API V3",
                                "accessPoint": {
                                    "@useType": "wsdlDeployment",
                                    "$": "http://localhost:8080/juddiv3/services/custody-transfer?wsdl"
                                },
                                "tModelInstanceDetails": {
                                    "tModelInstanceInfo": {
                                        "@tModelKey": "uddi:uddi.org:v3_ownership_transfer",
                                        "instanceDetails": {
                                            "instanceParms": "\n                \n                <?xml version=\"1.0\" encoding=\"utf-8\" ?>\n                <UDDIinstanceParmsContainer\n                 xmlns=\"urn:uddi-org:policy_v3_instanceParms\">\n                  <authInfoUse>required</authInfoUse>\n                </UDDIinstanceParmsContainer>\n                \n                "
                                        }
                                    }
                                },
                                "categoryBag": {
                                    "keyedReference": {
                                        "@tModelKey": "uddi:uddi.org:categorization:types",
                                        "@keyName": "uddi-org:types:wsdl",
                                        "@keyValue": "wsdlDeployment"
                                    }
                                }
                            },
                            {
                                "@bindingKey": "uddi:juddi.apache.org:servicebindings-custodytransfer-ws-ssl",
                                "@serviceKey": "uddi:juddi.apache.org:services-custodytransfer",
                                "description": "UDDI Custody and Ownership Transfer API V3 SSL",
                                "accessPoint": {
                                    "@useType": "wsdlDeployment",
                                    "$": "https://localhost:8443/juddiv3/services/custody-transfer?wsdl"
                                },
                                "tModelInstanceDetails": {
                                    "tModelInstanceInfo": [
                                        {
                                            "@tModelKey": "uddi:uddi.org:v3_ownership_transfer",
                                            "instanceDetails": {
                                                "instanceParms": "\n                \n                <?xml version=\"1.0\" encoding=\"utf-8\" ?>\n                <UDDIinstanceParmsContainer\n                 xmlns=\"urn:uddi-org:policy_v3_instanceParms\">\n                  <authInfoUse>required</authInfoUse>\n                </UDDIinstanceParmsContainer>\n                \n                "
                                            }
                                        },
                                        {
                                            "@tModelKey": "uddi:uddi.org:protocol:serverauthenticatedssl3"
                                        }
                                    ]
                                },
                                "categoryBag": {
                                    "keyedReference": {
                                        "@tModelKey": "uddi:uddi.org:categorization:types",
                                        "@keyName": "uddi-org:types:wsdl",
                                        "@keyValue": "wsdlDeployment"
                                    }
                                }
                            }
                        ]
                    }
                }
            ]
        },
        "categoryBag": {
            "keyedReference": {
                "@tModelKey": "uddi:uddi.org:categorization:nodes",
                "@keyName": "",
                "@keyValue": "node"
            }
        }
    }
}

````

### More information

For more information, please check out the source code: http://svn.apache.org/repos/asf/juddi/trunk/juddi-rest-cxf/
