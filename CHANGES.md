# `laaws-metadata-extraction-service` Release Notes
* Remove  Travis CI
* Move to OpenAPI 3
* Move to Java 17
* Move to springdoc
* Supress extraneous messages in tests
* Spring 6.1.x and Spring Boot 3.2.x support
* Set crawlMode = None

## Changes Since 2.0.3.1

*   Switched to a 3-part version numbering scheme.

## 2.0.3.1

### Security

*   Out of an abundance of caution, re-released 2.0.3.0 with Jackson-Databind 2.9.10.8 (CVE-2021-20190).

## 2.0.3.0

### Features

*   ...

### Fixes

*   ...

## 2.0.2.0

### Features

*   Added automatic metadata indexing.
*   REST services authenticate, clients provide credentials.
*   Improved startup coordination and ready waiting of all services and databases.
*   Improved coordination of initial plugin registry crawls.
