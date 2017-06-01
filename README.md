<!--
Copyright (c) 2016-2017 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.
--> 
# laaws-metadata-extractor [![Build Status](https://travis-ci.org/lockss/laaws-metadata-extractor.svg?branch=master)](https://travis-ci.org/lockss/laaws-metadata-extractor)
The wrapper around the metadata extractor.

### Clone the repo
`git clone --recursive ssh://git@gitlab.lockss.org/laaws/laaws-metadataextractor.git`

### Create the Eclipse project (if so desired)
File -> Import... -> Maven -> Existing Maven Projects

### Build and install the required LOCKSS daemon jar files:
run `initBuild`

### Set up the TDB tree:
Edit ./buildLaawsMdx and set the TDB_DIR variable properly.

### Build the web service:
`./buildLaawsMdx`

This will use port 8888 during the build. To use, for example, port 8889,
instead, either edit the value of $service_port in ./buildLaawsMdx or run:

`./buildLaawsMdx -Dswarm.http.port=8889`

The result of the build is a so-called "uber JAR" file which includes the
project code plus all its dependencies and which is located at

`./target/laaws-metadata-extraction-service-swarm.jar`

### Run the web service:
`./runLaawsMdx`

This will listen to port 8888. To use, for example, port 8889, instead, either
edit the value of $service_port in ./runLaawsMdx or run:

`./runLaawsMdx -Dswarm.http.port=8889`

The log is at ./logs/laawsmdx.log

### Build and run the web service:
`./buildAndRunLaawsMdx`

This will use port 8888 for both steps. To use, for example, port 8889, instead,
either edit the value of $service_port in ./buildAndRunLaawsMdx or run:

`./buildAndRunLaawsMdx -Dswarm.http.port=8889`

### Stop:
`./stopLaawsMdx`

### API is documented at:
#### localhost:8888/swagger-ui/

Type`http://localhost:8888/swagger.json` in the swagger instance to view
the docs and test against running server.

### Getting Archival Unit contents from a web service, not the repository
In ./lockss.opt add the following option:

org.lockss.plugin.auContentFromWs=true

To specify the properties of the web service (like the classic LOCKSS daemon)
used to get the URLs of an Archival Unit, add in ./lockss.opt the following
options with the appropriate values:

org.lockss.plugin.auContentFromWs.urlListWs.addressLocation=http://localhost:8081/ws/DaemonStatusService?wsdl
org.lockss.plugin.auContentFromWs.urlListWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlListWs.serviceName=DaemonStatusServiceImplService
org.lockss.plugin.auContentFromWs.urlListWs.targetNameSpace=http://status.ws.lockss.org/
org.lockss.plugin.auContentFromWs.urlListWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlListWs.userName=the-correct-user

To specify the properties of the web service (like the classic LOCKSS daemon)
used to get the content linked to a URL of an Archival Unit, add in ./lockss.opt
the following options with the appropriate values:

org.lockss.plugin.auContentFromWs.urlContentWs.addressLocation=http://localhost:8081/ws/ContentService?wsdl
org.lockss.plugin.auContentFromWs.urlContentWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlContentWs.serviceName=ContentServiceImplService
org.lockss.plugin.auContentFromWs.urlContentWs.targetNameSpace=http://content.ws.lockss.org/
org.lockss.plugin.auContentFromWs.urlContentWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlContentWs.userName=the-correct-user

### Using another REST web service for metadata storage
To use another REST web service to store the extracted metadata, instead of
storing it in the configured database, add in ./lockss.opt the following options
with the appropriate values:

org.lockss.metadataManager.mdRest.serviceLocation=http://localhost:8889
org.lockss.metadataManager.mdRest.timeoutValue=600
org.lockss.metadataManager.mdRest.userName=the-correct-user
org.lockss.metadataManager.mdRest.password=the-correct-password
