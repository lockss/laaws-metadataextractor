<!--

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
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
# LOCKSS Metadata Extraction Service
This is the REST Web Service that extracts metadata from the content of Archival
Units.

## Note on branches
The `master` branch is for stable releases and the `develop` branch is for
ongoing development.

## Standard build and deployment
The LOCKSS cluster, including this project, is normally built and deployed using
the LOCKSS Installer, which uses `docker`.

You can find more information about the installation of the LOCKSS system in the
[LOCKSS system manual](https://lockss.github.io/software/manual).

## Development build and deployment
### Clone the repo
`git clone -b develop ssh://github.com/lockss/laaws-metadataextractor.git`

### Create the Eclipse project (if so desired)
`File` -> `Import...` -> `Maven` -> `Existing Maven Projects`

### Build the web service:
In the home directory of this project, where this `README.md` file resides,
run `mvn clean install`.

This will run the tests as a pre-requisite for the build.

The result of the build is a so-called "uber JAR" file which includes the
project code plus all its dependencies and which can be located via the symbolic
link at

`./target/current-with-deps.jar`

### Run the web service:
Run the
[LOCKSS Development Scripts](https://github.com/lockss/laaws-dev-scripts)
project `bin/runservice` script in the home directory of this project, where
this `README.md` file resides.

The log is at `./logs/app.log`.

The API is documented at <http://127.0.0.1:24640/swagger-ui.html>.

The status of the web service may be obtained at
<http://127.0.0.1:24640/status>.

The administration UI of the web service is at <http://127.0.0.1:24641>.
