This example contains one class: the SimplePublish.java. When
executed it will obtain an AuthToken and use it to publish
a Publisher, a Business and a Service. 

The easiest way to run execute the main of this class in from
your IDE. If your IDE has maven integration it should set up
the project class path for you, and you can simple run it from 
there.

You should see the following output being written to the console:

root AUTHTOKEN = authtoken:0494e382-1ad3-4c52-8806-ae70a0ed37ad
myPub AUTHTOKEN = authtoken:bf973e5f-7361-4c57-92f7-7b499b886b6d
myBusiness key:  uddi:juddi.apache.org:6f3e4e62-e483-48ff-a1b3-6855310505c6
myService key:  uddi:juddi.apache.org:549a9580-cd7b-4969-9b77-527ab9f8f261

However since the keys are being generated in this case your keys will differ.

