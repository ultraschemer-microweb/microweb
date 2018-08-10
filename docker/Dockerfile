FROM ubuntu:16.04

# Create an unprivileged user:
RUN useradd -ms /bin/bash admin

#
# Install the base system:
#

ENV DEBIAN_FRONTEND noninteractive

#
# Java Configurations:
#

ENV JAVA_HOME /usr/lib/jvm/java-10-oracle
# Limiting memory of Java Daemons, to limit total memory size to 2GB, which is
# a small container on Amazon AWS and on Digital Ocean:

# Limits for Cassandra:
ENV JVM_OPTS -Xms128m -Xmx256m

# Limits for Elastic Search:
ENV ES_JAVA_OPTS -Xms128m -Xmx256m

# Limits for Gremlin Server:
ENV JAVA_OPTIONS -Xms128m -Xmx256m

# Update repositories:
RUN apt-get -y update

# Update operating system:
RUN apt-get dist-upgrade -y

# Install the base system programs, services and applications:
RUN apt-get install -y \
    software-properties-common vim less man unzip wget net-tools vim less \
    man unzip wget curl psmisc

# Install Java10 SDK:
RUN apt-get --purge remove openjdk* 
RUN echo "oracle-java10-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections 
RUN echo "deb http://ppa.launchpad.net/linuxuprising/java/ubuntu bionic main" > /etc/apt/sources.list.d/linuxuprising-java.list    
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 73C3DB2A
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN apt-get update -y
RUN apt-get install -y --no-install-recommends oracle-java10-installer oracle-java10-set-default

COPY dev-init /usr/local/bin/dev-init
RUN mkdir /usr/local/lib/microweb
COPY microweb-all.jar /usr/local/lib/microweb/microweb-all.jar
RUN chmod 555 /usr/local/bin/dev-init

# Clean the machine:
RUN apt-get -q -y autoremove
RUN apt-get -q -y clean all

# Finish installation, setting the Image Entry point:
ENTRYPOINT ["/usr/local/bin/dev-init"]


