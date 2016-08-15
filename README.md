# laaws-metadata-extractor [![Build Status](https://travis-ci.org/lockss/laaws-metadata-extractor.svg?branch=master)](https://travis-ci.org/lockss/laaws-metadata-extractor)
The wrapper around the metadata extractor and reporter.

### Clone the repo
`git clone --recursive https://github.com/lockss/laaws-metadata.git`

### Set up the TDB tree:
Edit ./runLaawsMdx and set the TDB_DIR variable properly.

### Build and install the required LOCKSS daemon jar files:
run `initBuild`

### Build and run:
`./runLaawsMdx`

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
