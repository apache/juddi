## UDDI Migration and Backup Tool

The UDDI Migration and Backup Tool can be used to perform a number of administrative tasks such as

 - Backup the contents of a UDDI server (business, services, binding templates and tModels)
 - Import contents into a UDDI server (business, services, binding templates and tModels)

In addition, the migration tool has a few features that serve as job aids.

 - Ability to remove digital signatures on Import or Export
 - Ability to maintain ownership properties of UDDI entries
 - Ability to export and import Publishers (jUDDI only)
 - Automatically skip an item on import if the entity key already exists

The UDDI Migration and Backup Tool is Command Line Interface program and has a number of use cases such as:

* Copying data from one registry to another
* Migrating from one vendor to another
* Periodic backups
* Upgrades to jUDDI

TIP: The migration tool will not overwrite data when importing.


### Using the tool

There are many configuration options and settings for the migration tool. This tool is distributed with the uddi client distribution package. 

#### Get help

````
>java -jar \
	juddi-migration-tool-<VERSION>-jar-with-dependencies.jar
This tool is used to export and import UDDI data from a UDDI v3 
registry
Random TIP: Without the preserveOwnership flag, all imported data
 will be owned by the username that imported it.

usage: java -jar 
	juddi-migration-tool-(VERSION)-jar-with-dependencies.jar
 -business <arg>      Im/Export option, file to store the business 
					  data, default is 'business-export.xml'
 -config <arg>        Use an alternate config file default is 
					  'uddi.xml'
 -credFile <arg>      Import option with -preserveOwnership, this 
					  is a properties file mapping with user=pass
 -export              Exports data into a UDDIv3 registry
 -import              Imports data into a UDDIv3 registry
 -isJuddi             Is this a jUDDI registry? If so we can 
					  in/export more stuff
 -mappings <arg>      Im/Export option, file that maps keys to 
					  owners, default is 
					  'entityusermappings.properties'
 -myItemsOnly         Export option, Only export items owned by 
				      yourself
 -node <arg>          The node 'name' in the config, default is 
                      'default'
 -pass <arg>          Password, if not defined, those is uddi.xml 
					  will be used
 -preserveOwnership   Im/Export option, saves owership data to the
                      'mappings' file
 -publishers <arg>    jUDDI only - In/Export option, file to store
                      publishers, default is 
					  'publishers-export.xml'
 -stripSignatures     Im/Export option, removes digital signatures 
					  from all signed items, default is false
 -tmodel <arg>        Im/Export for tmodels, file to store tmodel 
				      data, default is 'tmodel-export.xml'
 -user <arg>          Username, if not defined, those is uddi.xml 
					  will be used
````

#### Use case: basic import and export

To export everything without preserving ownership information:

````
java -jar \
	juddi-migration-tool-(VERSION)-jar-with-dependencies.jar \
	-export
````

To import everything without preserving ownership information:

````
java -jar \
	juddi-migration-tool-(VERSION)-jar-with-dependencies.jar \
	-import
````

#### Use case: Import and Export while preserving ownership information

To export everything with preserving ownership information:

````
java -jar \
	juddi-migration-tool-(VERSION)-jar-with-dependencies.jar \
	-export -preserveOwnership
````

To import everything with preserving ownership information, first edit the mappings file which is entityusermappings.properties by default. Once every user has a password, run the following command

````
java -jar \
	juddi-migration-tool-(VERSION)-jar-with-dependencies.jar \
	-import -preserveOwnership
````

TIP: When preserving ownership information, upon import, you'll need every UDDI entity owner's password. If you don't have this and you're using jUDDI, you can temporarily switch jUDDI to the 'DefaultAuthenticator' which doesn't validate passwords (just put anything in the mappings file for each user). Once the import is complete, you can then switch back to whatever authenticator you were using before.
