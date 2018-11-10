# 
#   Builds an already initialized Jenkins instance with access to certain resources
#   setup by default.
#    
#   Authors: James Eby
#
#   This container expects certain environment variables to be passed to it either through
#   the -e flag or the --env-file flag. A default env-file flag has been created in this
#   directory and simply exports all the values from the host OS to the runtime container
#   environment through a command like this:
#
#   docker run --env-file env.list 
#

FROM jenkins/jenkins:lts
LABEL maintainer="James@freebyTech.com"

ENV JAVA_OPTS=" -Djenkins.install.runSetupWizard=false"
ENV JENKINS_OPTS=" --handlerCountMax=300"

USER root

RUN apt-get update && apt-get install -y iputils-ping

# drop back to the regular jenkins user - good practice
USER jenkins

# The list of plugins was derived from running:
#
# JENKINS_HOST=pluginreader:pluginreader@build02.serenity.dom:8080
# curl -sSL "http://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g'|sed 's/ /:/'
#
# and can be updated in the future by running the same process.
#
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ADD jenkins-scripts /usr/share/jenkins/ref/init.groovy.d


