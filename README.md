# snafl-metadata [![Build Status](https://travis-ci.org/lockss/snafl-metadata.svg?branch=master)](https://travis-ci.org/lockss/snafl-metadata)
The wrapper around the metadata extracter and reporter.

### Set up the TDB tree:
Edit ./runSnafl and set the TDB_DIR variable properly.

### Build and install the required LOCKSS daemon jar files:
cd ${dir_with_lockss-daemon_snafl-changes_branch_code}
ant jar-all
cp lib/lockss.jar ${to_maven_repository}
cp lib/lockss-plugins.jar ${to_maven_repository}

### Build and run:
./runSnafl

The log is at ./snafl.log

### Stop:
./stopSnafl

### API is documented at:
#### localhost:8888/docs/
