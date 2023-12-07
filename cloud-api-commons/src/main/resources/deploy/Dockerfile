FROM containers.cisco.com/rhel8/osaas-nonfips:1.24.202310

ARG WORKDIR=/opt/webex/mats
WORKDIR $WORKDIR
RUN mkdir -p /opt/webex/mats
#USER root
#WORKDIR $WORKDIR
#RUN mkdir -p /opt/webex/wapmats
#RUN touch /opt/webex/wapmats/whoami.txt
#RUN chmod 777 /opt/webex/wapmats/whoami.txt
#RUN echo `whoami` >> /opt/webex/wapmats/whoami.txt

#RUN groupadd slimtty && useradd -g slimtty -m slimttyagent
#RUN groupadd matsgroup --gid 12001 && useradd --uid 12001 -g 12001 -m matsuser

RUN yum install sudo -y
RUN yum install lsof -y
# ifconfig
RUN yum install net-tools -y
# ping
RUN yum install iputils -y
#RUN yum install iproute -y
# nslookup
RUN yum install bind-utils -y
#RUN #yum install telnet -y
RUN yum install java-1.8.0-openjdk --nobest -y
RUN yum install java-1.8.0-openjdk-headless --nobest -y
#RUN #yum install java-1.8.0-openjdk-devel -y
RUN yum install WBXjava-1.8.0-openjdk-hardening --nobest -y


## maven
#FROM maven:3.5.2 AS build
#ARG WORKDIR=/opt/webex/wapmats
#WORKDIR $WORKDIR
#RUN mkdir -p /opt/webex/wapmats/code_src
#COPY settings.xml /root/.m2/
#COPY ../advanced-diagnostic-common code_src/advanced-diagnostic-common
#RUN cd /opt/webex/wapmats/code_src/advanced-diagnostic-common && mvn dependency:go-offline -B
##RUN --mount=type=cache,target=/root/.m2,rw mvn -B package -Dmaven.test.skip=true
#RUN cd /opt/webex/wapmats/code_src \
#    && pwd \
#    && echo `ls .` \
#    && mvn clean \
#    && mvn install -pl advanced-diagnostic-common -Dmaven.test.skip=true \
#    && mvn install -pl advanced-diagnostic-data -Dmaven.test.skip=true \
#    && cd advanced-diagnostic-meeting \
#    && mvn package -Dmaven.test.skip=true


RUN mkdir -p /opt/webex/mats/WBXmatsfraudbackendservice
RUN mkdir -p /opt/webex/mats/WBXmatsfraudbackendservice/config
RUN mkdir -p /opt/webex/mats/WBXmatsfraudbackendservice/data
RUN mkdir -p /opt/webex/mats/WBXmatsfraudbackendservice/logs
RUN mkdir -p /opt/webex/mats/WBXmatsfraudbackendservice/logs
RUN mkdir -p /opt/webex/conf/webapps

COPY target/WBXmatsfraudbackendservice-*.jar $WORKDIR/WBXmatsfraudbackendservice/
COPY src/main/resources/shell/ctrl.sh $WORKDIR/WBXmatsfraudbackendservice/
COPY src/main/resources/shell/dockercmd.sh $WORKDIR/WBXmatsfraudbackendservice/
COPY src/main/resources/conf/IngrianNAE.properties /opt/webex/conf/webapps/
COPY src/main/resources/conf/apptokenconfig.xml /opt/webex/conf/webapps/
COPY src/main/resources/conf/kmconfig.properties /opt/webex/conf/webapps/
COPY src/main/resources/logback-spring.xml $WORKDIR/WBXmatsfraudbackendservice/config
COPY src/main/resources/application.properties $WORKDIR/WBXmatsfraudbackendservice/config
COPY src/main/resources/conf/secret.properties $WORKDIR/WBXmatsfraudbackendservice/config
COPY src/main/resources/data $WORKDIR/WBXmatsfraudbackendservice/data
RUN /bin/mv $WORKDIR/WBXmatsfraudbackendservice/WBXmatsfraudbackendservice-*.jar $WORKDIR/WBXmatsfraudbackendservice/WBXmatsfraudbackendservice.jar


#COPY WBXmatsrainierSAPservice/advanced-diagnostic-meeting/target/WBXmatsrainierSAPservice-*.jar $WORKDIR/WBXmatsrainierSAPservice/maven_mlsra/lib/
#COPY WBXmatsrainierSAPservice/advanced-diagnostic-meeting/bin $WORKDIR/WBXmatsrainierSAPservice/maven_mlsra/bin/
#COPY WBXmatsrainierSAPservice/advanced-diagnostic-meeting/lib $WORKDIR/WBXmatsrainierSAPservice/maven_mlsra/lib/
#COPY WBXmatsrainierSAPservice/advanced-diagnostic-meeting/conf $WORKDIR/WBXmatsrainierSAPservice/maven_mlsra/conf/

#RUN echo 'ENCRYPTION_PASSWORD=ccapops' >> /etc/profile
#RUN echo 'export ENCRYPTION_PASSWORD=ccapops' >> /etc/profile
#RUN echo "alias cs='cd /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/; pwd'" >> /etc/profile
#RUN echo "alias cslog='cd /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/logs; tail -f mats.log'" >> /etc/profile
#RUN . /etc/profile

#RUN ip_addr=`ifconfig | grep "inet " | grep -v "127.0.0.1" | awk '{print $2}'`\
#    && sed -i.bak "s/BANGE-M-V2J4/${ip_addr}/g" /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/conf/config.xml \
#    && sed -i.bak "s#/Users/bange/gitrepo/cloudtools-mats-sap_cctg-cloud#/opt/webex/wapmats#g" /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/conf/config.xml

# uid:1000 user: rhel
# RUN #chown -R 65534:65534 /opt/webex/wapmats
#RUN chmod 777 -R /opt/webex/wapmats
RUN chmod 755 /opt/webex/mats/WBXmatsfraudbackendservice/ctrl.sh
#RUN chmod 777 /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/bin/restart.sh
RUN chmod 777 /opt/webex/mats/WBXmatsfraudbackendservice/dockercmd.sh
RUN chmod 777 /opt/webex/mats/WBXmatsfraudbackendservice/config/logback-spring.xml
#RUN #chmod 777 /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/bin/stop.sh
#RUN chmod 777 /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/bin/*.sh
#RUN chmod 400 /opt/webex/wapmats/WBXmatsrainierSAPservice/maven_mlsra/bin/jmxremote.password

# Make sudo dummy replacement, so we don't weaken docker security
#RUN echo "#!/bin/bash\n\$@" > /usr/bin/sudo
#RUN chmod +x /usr/bin/sudo
#
#ENV params=""IngrianNAE.properties
#EXPOSE 28090
#RUN echo `whoami`
#RUN whoami
##USER root
#RUN echo `whoami` >> /opt/webex/wapmats/whoami.txt
#RUN echo '1000 ALL=(ALL) NOPASSWD:/bin/su,/sbin/su,/usr/bin/su,/usr/sbin/su,/bin/bash' > /etc/sudoers.d/1000_rhel
#RUN echo '65534 ALL=(ALL) NOPASSWD:/bin/su,/sbin/su,/usr/bin/su,/usr/sbin/su,/bin/bash' > /etc/sudoers.d/65534_nobody
#RUN echo '12001 ALL=(ALL) NOPASSWD:/bin/su,/sbin/su,/usr/bin/su,/usr/sbin/su,/bin/bash' > /etc/sudoers.d/12001_matsuser

#RUN export TERM=xterm

#RUN #chown -R 1000:1000 /opt/webex/wapmats
#RUN chown -R 65534:65534 /opt/webex/wapmats
RUN chmod -R 777 /opt/webex/mats/WBXmatsfraudbackendservice/logs
#RUN chmod -R 777 /opt/webex/mats/WBXmatsfraudbackendservice/conf
#USER rhel
#USER root

CMD ["/opt/webex/mats/WBXmatsfraudbackendservice/dockercmd.sh"]
#CMD ["/bin/sh", "-c", "$WORKDIR/WBXmatsrainierSAPservice/maven_mlsra/bin/restart.sh"]

EXPOSE 9000