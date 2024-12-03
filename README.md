# fineractDemo
fineractDemo
Apache Fineract Setup:
How to build Apache Fineract® with IntelliJ IDEA
Prerequisites
Java: Ensure you have Java Development Kit (JDK) installed. Apache Fineract® typically requires Java 17 (Java 21 is not yet supported).
Git: You’ll need Git to clone the Apache Fineract® source code from the repository.
IntelliJ IDEA: Install IntelliJ IDEA (Community or Ultimate edition).
Steps
Clone the Apache Fineract® Repository:
Open a terminal or command prompt and navigate to the directory where you want to store the Apache Fineract® source code.
Run the following command to clone the repository:
git clone https://github.com/apache/fineract.git

Import the Project into IntelliJ IDEA:
Open IntelliJ IDEA.
Click on File > Open... and select the fineract directory you cloned in step 1. IntelliJ will recognize it as a Gradle project.
IntelliJ IDEA will analyze the project and download its dependencies.
Configure JDK:
Verify that IntelliJ IDEA is using the correct Java SDK. Go to File > Project Structure.
In the Project Structure dialog, under Project, make sure the Project SDK is set to the appropriate Java version (Java 17 is the latest supported).
Gradle Configuration:
Open the build.gradle file in the project root directory.
IntelliJ IDEA will automatically start downloading the dependencies, if not you can do it manually as well by opening the “Gradle” window and click on Reload All Gradle Projects.
Further configurations can be found in the settings.gradle file
Set Up Run Configurations:
Go to Run > Edit Configurations.
Click the + button and select Gradle.
In the configuration settings:
Set the Name (e.g., “Fineract”).
Choose the fineract directory as Gradle project.
Set bootRun as the Run command.
Create two databases
Create fineract_tenants and fineract_default databases
MySql
Execute createMySQLDB gradle task with the following params:
./gradlew createMySQLDB -PdbName=fineract_tenants
./gradlew createMySQLDB -PdbName=fineract_default

Add Depedency:
1.Add depedency in fineract-provider : build.gradle
dependencies {
    driver 'mysql:mysql-connector-java:8.0.33'
}
2.Use the following driver to connect.
Com.mysql.cj.jdbc.Driver
3.User your local db username and password
Build the Project:
Go to Run > Run 'Fineract'.
IntelliJ IDEA will build and run the Apache Fineract® server. You can access it using a web browser at https://localhost:8443/fineract-provider.
This should get you started with building and running Apache Fineract® using IntelliJ IDEA.
Default configuration
By default the application will be using a self-signed TLS protocol (https) and port: 8443
By default the application will connect to MariaDB on localhost (port:3306).
Default credentials: root/mysql
Default tenants storage database: fineract_tenants
This database will store the tenant details and connection information to the tenant databases
Database driver: org.mariadb.jdbc.Driver (by default)
To override set the FINERACT_HIKARI_DRIVER_SOURCE_CLASS_NAME environment variable
JDBC url: jdbc:mariadb://localhost:3306/fineract_tenants
To override set the FINERACT_HIKARI_JDBC_URL environment variable
Database username: root
To override set the FINERACT_HIKARI_USERNAME environment variable
Database password: password
To override set the FINERACT_HIKARI_PASSWORD environment variable
By default the liquibase scripts will create a new tenant with the below configurations
Default tenant identifier: default
To override set the FINERACT_DEFAULT_TENANTDB_IDENTIFIER environment variable
Default tenant database host: localhost
To override set the FINERACT_DEFAULT_TENANTDB_HOSTNAME environment variable
Default tenant database port: 3306
To override set the FINERACT_DEFAULT_TENANTDB_PORT environment variable
Default tenant database username: root
To override set the FINERACT_DEFAULT_TENANTDB_UID environment variable
Default tenant database password: mysql
To override set the FINERACT_DEFAULT_TENANTDB_PWD environment variable
Default tenant timezone: Asia/Kolkata
To override set the FINERACT_DEFAULT_TENANTDB_TIMEZONE environment variable
Default tenant name: fineract_default
To override set the FINERACT_DEFAULT_TENANTDB_NAME environment variable
Default tenant description: Default Demo Tenant
To override set the FINERACT_DEFAULT_TENANTDB_DESCRIPTION environment variable
Default tenant master password: fineract
To override set the FINERACT_DEFAULT_TENANTDB_MASTER_PASSWORD environment variable
IMPORTANT: During the first time the liquibase executed the plain text password got encrypted with the provided master password
Default tenant encryption: AES/CBC/PKCS5Padding
To override set the FINERACT_DEFAULT_TENANTDB_ENCRYPTION environment variable
The list of configuration is not complete!
Optional extra configurations
Use http instead of https and use 8080 port instead of 8443
Set the following Environment variables:
FINERACT_SERVER_SSL_ENABLED=false
 server.port=8080


Reference:
How to build Apache Fineract® with IntelliJ IDEA | Fineract® Academy

Challenges:
    1. Download timezone table from following url(for windows)
MySQL :: Time zone description tables
    2. Import them into your database 
mysql -u root -p mysql < timezone_posix.sql
    3. Set global variables of db as below
SET GLOBAL time_zone='UTC';
SET session time_zone= ' Asia/Kolkata ';
SET sql_mode = 'NO_ENGINE_SUBSTITUTION';
SET GLOBAL sql_mode = 'NO_ENGINE_SUBSTITUTION';

    4. Change the connection parameters as below in the file 
fineract-provider\src\main\resources\db\changelog\tenant-store\parts\0005_jdbc_connection_string.xml

<column name="schema_connection_parameters" value="serverTimezone=UTC&amp;sessionVariables=time_zone='Asia/Kolkata'"/>
    5. Execute the below query 
update fineract_tenants.tenant_server_connections set schema_password='NfvfQDK235D+70kT95pxgZ40wqW+n7npuY7YWjeTBiQuNP5dvxPoqgg2+7vxJIUd', 
schema_connection_parameters =”serverTimezone=UTC&sessionVariables=time_zone='Asia/Kolkata'” where id=1;


    6. If you get error like below
db/changelog/tenant-store/parts/0005_jdbc_connection_string.xml::1::fineract was: 9:90654ac665c3ab846d618257fe27f518 but is now: 9:1c850086673fb4cae0793ac99d560dea
Execute the query with your value from error.
update fineract_tenants.databasechangelog set MD5SUM='9:1c850086673fb4cae0793ac99d560dea' where 
filename = 'db/changelog/tenant-store/parts/0005_jdbc_connection_string.xml';



Challenges to move our APIs:
Changes done for service module:
1
org.mifosplatform.
org.apache.fineract.
2
javax.persistence.
jakarta.persistence.
3
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;
 
4
Cannot resolve method 'findOne(Long)'
 
Optional<ServiceDetails> serviceDetailsOptional =this.serviceMasterDetailsRepository.findById(id);
if(serviceDetailsOptional.isPresent()){
ServiceDetails serviceDetails = serviceDetailsOptional.get();
 
Optional<ServiceMaster>  serviceMasterOptional = this.serviceMasterRepository.findById(serviceId);
        return serviceMasterOptional.orElseThrow(() ->new CodeNotFoundException(serviceId.toString()));
 
 
5
return new CommandProcessingResult(Long.valueOf(-1));
return new CommandProcessingResultBuilder() //
.withEntityId(Long.valueOf(-1)) //
.build();
 
6
command.stringValueOfParameterName("addressType"));
command.stringValueOfParameterNamed("addressType"));
 
 
7
extends AbstractAuditableCustom<AppUser, Long>{
extends AbstractAuditableCustom{
 
8
extends AbstractPersistable<Long> {
extends AbstractPersistableCustom<Long> {
 
9
SearchParameters instead of   SearchSqlQuery  for new apis
or import import org.apache.fineract.infrastructure.core.service.tenant.SearchSqlQuery;
 
10
import org.apache.fineract.billing.emun.data.EnumValuesData;
 
11
import org.apache.fineract.organisation.mcodevalues.data.MCodeData;
 
org.mifosplatform.organisation.mcodevalues.data.MCodeData
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
 
12
return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(), new Object[]{}, mapper);
examples:
return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), paramList.toArray(), this.clientToDataMapper);
return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), extraCriteria.getArguments(), rm);
return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), new Object[]{}, mapper);
 
13
private final ToApiJsonSerializer apiJsonSerializer;
private final DefaultToApiJsonSerializer<TaxComponentData> toApiJsonSerializer;
 
14
javax.ws.rs.
jakarta.ws.rs.
 
15
need change data in tables
1.m_code
2.m_code_value
need do changes in mcode queries if needed.
 
16. Get dump from tayanaDB and insert the data and tables for respective modules.
 
Optional
17.  Need to create swagger class 
     Need to add swagger parameters for api.

