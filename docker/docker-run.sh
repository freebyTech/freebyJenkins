#!/bin/bash

# 
#   An example of running this docker container with the proper environment variables
#   setup so that jenkins will be able to establish connections with necessary resources.
#   
#   Authors: James Eby
#

if [ -d ../../freebysecrets ]; then
    echo 'Setting up environment variables for a docker container run.'
    . ../../freebysecrets/env/setUserEnvironmentVariables-Session.sh
    export JENKINS_USER_PASSWORD=$(cat ../../freebysecrets/creds/$JENKINS_USER@$JENKINS_URL)
    #REGISTRY_USER_PASSWORD=$(cat ../../freebysecrets/creds/$REGISTRY_USER@$REGISTRY_URL)
    #GIT_REPO_USER_PASSWORD=$(cat ../../freebysecrets/creds/$GIT_REPO_USER@$GIT_REPO_URL)
fi

for container_id in $(docker ps  --filter="name=jenkins" -q);do docker stop $container_id && docker rm $container_id;done
docker run -p 8080:8080 --env-file env.list --name "jenkins" --dns=192.168.1.20 freebytech/jenkins