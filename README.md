# Project Title

A simple microservice project in spring-webflux exposes below 2 apis and integrates with
seb-developer-sandbox https://developer.sebgroup.com/products/psd2-account-information

```
1.SEB Get Accounts -(https://api-sandbox.sebgroup.com/ais/v7/identified2/accounts)
2.SEB Get Balances - (https://api-sandbox.sebgroup.com/ais/v7/identified2/accounts/%s/resources)
```

## Getting Started

clone the project from GitHub and install using below command.
The dependencies can download from maven central and project can be compiled with below command.

```
mvn clean install
```

### Executing program

* if project is run on the profile 'local' , GetAccount and GetBalances will mock the response values from
  com.test.integrations.lib.infrastructure.SebRestMock

```
  Profile is located in Integration-system\microservice\microservice-payment\src\main\resources\application.yml ({spring.profiles.active})
```

* if project is run on the profile other than 'local' for eg: 'remote',
  com.test.integrations.lib.infrastructure.SebRestRemote will integrate to sandbox with bearer token value specified in
  application.yml file.
  The token value is manually retrieved from 0Auth using the test bankIds and put the access_token value in application.yml
  file

```
  Microservice will integrate with seb-sandox endpoint
  integrations:
       seb:
         baseUrl: https://api-sandbox.sebgroup.com
         token: "0cNIkM3gRssw9vceUHhL"
         connectTimeout: 5000
         readTimeout: 10000
```

* With right profile and correct token run the main class Application in microservice-payment module,
Below endpoints can be invoked from your localhost 8080 port.
* Note the {resourceId} value is any of the UUID value returned from https://api-sandbox.sebgroup.com/ais/v7/identified2/accounts

```
 http://localhost:8080/api/rest/v1/accounts?requestId=24362672&randomIp=127.0.0.1
 http://localhost:8080/api/rest/v1/accounts/{resourceId}/balances?requestId=24362672&randomIp=127.0.0.1
```



