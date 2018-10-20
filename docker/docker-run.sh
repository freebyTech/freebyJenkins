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
fi

docker run -p 8080:8080 --env-file env.list freebytech/jenkins