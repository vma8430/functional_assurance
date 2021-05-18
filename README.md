
Build Pipeline : [![Build Status](https://dev.azure.com/MYAzureWorkshopExecution/AZURE_WORKSHOP_PROJECT/_apis/build/status/FUNCTIONALASSURANCE-BUILD-RELEASE?branchName=master)](https://dev.azure.com/MYAzureWorkshopExecution/AZURE_WORKSHOP_PROJECT/_build/latest?definitionId=3&branchName=master)

Tests | Status
----|----
UI Tests| ![UI Tests](https://vsrm.dev.azure.com/MYAzureWorkshopExecution/_apis/public/Release/badge/431c5cbe-5f90-4471-925f-560b72caeb89/1/1)
API Tests:| ![API Tests](https://vsrm.dev.azure.com/MYAzureWorkshopExecution/_apis/public/Release/badge/431c5cbe-5f90-4471-925f-560b72caeb89/1/2)
RWD Tests|  ![RWD Tests](https://vsrm.dev.azure.com/MYAzureWorkshopExecution/_apis/public/Release/badge/431c5cbe-5f90-4471-925f-560b72caeb89/1/3)

### How to run the UI and RWD Tests

###### UI & API - Smoke Tests
```
$ mvn clean test -Dcucumber.options="--tags @Smoke" -DExecutionPlatform="AWS_CHROME"
```

###### API Tests :
```
$ mvn clean test -Dcucumber.options="--tags @APITest"
```

###### UI Tests
```
$ mvn clean test -Dcucumber.options="--tags @UITest" -DExecutionPlatform="GRID_CHROME"
$ mvn clean test -Dcucumber.options="--tags @UITest" -DExecutionPlatform="AWS_FIREFOX"
$ mvn clean test -Dcucumber.options="--tags @UITest" -DExecutionPlatform="LOCAL_CHROME"
$ mvn clean test -Dcucumber.options="--tags '@UITest and @Search'" -DExecutionPlatform="LOCAL_CHROME"
$ mvn clean test -Dcucumber.options="--tags @UITest" -DExecutionPlatform="AWS_DEVICEFARM_CHROME"
$ mvn clean test -Dcucumber.options="--tags @UITest" -DExecutionPlatform="AWS_DEVICEFARM_FIREFOX"

```

###### RWD Tests
```
$ mvn clean test -Dcucumber.options="--tags @UIRWDTest" -DExecutionPlatform="AWS_CHROME"
$ mvn clean test -Dcucumber.options="--tags @UIRWDTest" -DExecutionPlatform="AWS_DEVICEFARM_CHROME"

```