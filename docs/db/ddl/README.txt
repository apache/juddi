To generate the ddl, in the juddi-core module run

mvn hibernate3:hbm2ddl

This will create a target/hibernate3/sql/schema.ddl file containing the creation SQL for the
database type specified in the juddi-core/src/main/resources/persistence/hibernate-persistence.xml.

Dialects can be found at https://www.hibernate.org/hib_docs/v3/api/org/hibernate/dialect/package-summary.html

DB2Dialect
DerbyDialect
MySQL5InnoDBDialect
Oracle9Dialect
PostgreSQLDialect
SQLServerDialect



