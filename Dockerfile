FROM ubuntu:latest

MAINTAINER "Daniel Vargas" <dlvargas@stanford.edu>

# Install build tools
RUN apt-get update
RUN apt-get -y install git subversion ant gettext openjdk-8-jdk-headless maven locales

# Set LANG (needed for msginit -- called by lockss-daemon build.xml)
ENV LANG en_US.UTF-8
RUN locale-gen ${LANG}

# Get laaws-metadataextractor source 
#RUN git clone https://gitlab.lockss.org/laaws/laaws-metadataextractor.git --recursive

# Add LAAWS Metadata Extractor source
ADD . /laaws-metadataextractor

# Build LOCKSS daemon JARs
WORKDIR /laaws-metadataextractor
RUN ./initBuild

# XXX Isolate only what's needed to run
#RUN mkdir /laaws-metadataextractor
#RUN mv target /laaws-metadataextractor
#RUN mv runLaawsmetadataextractor /laaws-metadataextractor
#RUN mv src /laaws-metadataextractor

# XXX Clean up 
RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
#RUN rm -rf ~/.m2 ~/.subversion

# XXX Ask fergaloy to fix
RUN mkdir logs
RUN touch logs/laawsmdx.log

# XXX Overlay needed for demo
#ADD lockss.opt /laaws-metadataextractor

CMD ["/bin/sh", "/laaws-metadataextractor/buildAndRunLaawsMdx", "-Dswarm.http.port=8888", "-Djava.net.preferIPv4Stack=true"]
