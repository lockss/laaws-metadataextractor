<!--

Copyright (c) 2000-2017 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--> 
# laaws-metadata-extractor [![Build Status](https://travis-ci.org/lockss/laaws-metadata-extractor.svg?branch=master)](https://travis-ci.org/lockss/laaws-metadata-extractor)
The LAAWS Metadata Extraction REST Web Service.

### Clone the repo
`git clone --recursive ssh://git@gitlab.lockss.org/laaws/laaws-metadataextractor.git`

### Create the Eclipse project (if so desired)
`File` -> `Import...` -> `Maven` -> `Existing Maven Projects`

### Set up the LOCKSS plugins JAR file:
The LOCKSS plugins JAR file needs to be located at `./lockss-plugins.jar`.

### Set up the TDB tree:
The TDB tree needs to be located at `./tdbxml/prod`, matching the definition
in `./runLaawsMdx`.

### Specify the Repository REST web service
This web service requires that an external Repository REST web service is
running so as to provide the contents of Archival Units.

To specify the properties of the external REST web service used to get the URLs
of an Archival Unit, edit in `config/lockss.txt` the following options with
the appropriate values:

org.lockss.plugin.auContentFromWs.urlListWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlListWs.restServiceLocation=http://localhost:the-correct-port/repos/demorepo/artifacts?committed=false&auid={auid}
org.lockss.plugin.auContentFromWs.urlListWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlListWs.userName=the-correct-user

To specify the properties of the external REST web service used to get the
artifact properties of a URL, edit in `config/lockss.txt` the following
options with the appropriate values:

org.lockss.plugin.auContentFromWs.urlArtifactWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlArtifactWs.restServiceLocation=http://localhost:the-correct-port/repos/demorepo/artifacts?committed=false&uri={uri}
org.lockss.plugin.auContentFromWs.urlArtifactWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlArtifactWs.userName=the-correct-user

To specify the properties of the external REST web service used to get the
content linked to a URL of an Archival Unit, edit in `config/lockss.txt` the
following options with the appropriate values:

org.lockss.plugin.auContentFromWs.urlContentWs.password=the-correct-password
org.lockss.plugin.auContentFromWs.urlContentWs.restServiceLocation=http://localhost:the-correct-port/repos/demorepo/artifacts/{artifactid}
org.lockss.plugin.auContentFromWs.urlContentWs.timeoutValue=600
org.lockss.plugin.auContentFromWs.urlContentWs.userName=the-correct-user

### Build the web service:
`./buildLaawsMdx`

This will run the tests as a pre-requisite for the build.

The result of the build is a so-called "uber JAR" file which includes the
project code plus all its dependencies and which is located at

`./target/laaws-metadata-extraction-service-0.0.1-SNAPSHOT.jar`

### Run the web service:
`./runLaawsMdx`

This will use port 28120. To use another port, edit the value of the
`server.port` property in file
`src/main/resources/application.properties`.

The log is at `./logs/mdx.log`

### Build and run the web service:
`./buildAndRunLaawsMdx`

This will use port 28120. To use another port, edit the value of the
`server.port` property in file
`src/main/resources/application.properties`.

### API is documented at:
#### http://localhost:28120/swagger-ui.html

### The status of the web service may be obtained at:
#### http://localhost:28120/status

### Stop the web service:
`./stopLaawsMdx`

### Using another REST web service for metadata storage
To use another REST web service to store the extracted metadata, instead of
storing it in the configured database, edit in `config/lockss.txt` the
following options with the appropriate values:

org.lockss.metadataManager.mdRest.serviceLocation=http://localhost:the-correct-port
org.lockss.metadataManager.mdRest.timeoutValue=600
org.lockss.metadataManager.mdRest.userName=the-correct-user
org.lockss.metadataManager.mdRest.password=the-correct-password
