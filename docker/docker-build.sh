#!/bin/bash

# 
#   Build the base docker image, push in any credentials files from
#   owning OS into the base directory for this build so it can be pushed
#   into the container.
#   
#   Authors: James Eby
#

if [ -d ~/creds ]; then
    echo 'Copying credientials for build.'
    cp -r ~/creds ./creds
fi

# Use the  --no-cache to force a complete rebuild.
docker build -t freebytech/jenkins .