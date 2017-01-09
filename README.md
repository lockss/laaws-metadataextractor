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
The wrapper around the metadata extractor and reporter.

### Clone the repo
`git clone --recursive https://github.com/lockss/laaws-mdx.git`

### Create the Eclipse project (if so desired)
File -> Import... -> Maven -> Existing Maven Projects

### Set up the TDB tree:
Edit ./runLaawsMdx and set the TDB_DIR variable properly.

### Build and install the required LOCKSS daemon jar files:
run `initBuild`

### Build and run:
`./runLaawsMdx`

This will listen to port 8888. To use, for example, port 8889, instead, either
edit the value of $service_port in ./runLaawsMdx or run:

`./runLaawsMdx 8889`

The log is at ./laawsmdx.log

### Stop:
`./stopLaawsMdx`

### API is documented at:
#### localhost:8888/docs/

### Getting Archival Unit contents from a web service, not the repository
In ./lockss.opt add the following option:

org.lockss.plugin.auContentFromWs=true

To specify the properties of the web service used to get the URLs of an
Archival Unit, in ./lockss.opt add the following options with the appropriate
values:

org.lockss.plugin.auContentFromWs.urlListWs.addressLocation=http://localhost:8081/ws/DaemonStatusService?wsdl
org.lockss.plugin.auContentFromWs.urlListWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlListWs.serviceName=DaemonStatusServiceImplService
org.lockss.plugin.auContentFromWs.urlListWs.targetNameSpace=http://status.ws.lockss.org/
org.lockss.plugin.auContentFromWs.urlListWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlListWs.userName=the-correct-user

To specify the properties of the web service used to get the URLs of an
Archival Unit, in ./lockss.opt add the following options with the appropriate
values:

org.lockss.plugin.auContentFromWs.urlContentWs.addressLocation=http://localhost:8081/ws/ContentService?wsdl
org.lockss.plugin.auContentFromWs.urlContentWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlContentWs.serviceName=ContentServiceImplService
org.lockss.plugin.auContentFromWs.urlContentWs.targetNameSpace=http://content.ws.lockss.org/
org.lockss.plugin.auContentFromWs.urlContentWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlContentWs.userName=the-correct-user

To use another REST web service to store and retrieve the extracted metadata,
in ./lockss.opt add the following options with the appropriate values:

org.lockss.metadataManager.mdRest.serviceLocation=http://localhost:8889
org.lockss.metadataManager.mdRest.timeoutValue=600
org.lockss.metadataManager.mdRest.userName=the-correct-user
org.lockss.metadataManager.mdRest.password=the-correct-password
