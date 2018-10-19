#!groovy

import jenkins.model.*

Jenkins.instance.setNumExecutors(5)

// Jenkins itself recommends disabling the CLI over the remoting connection.
jenkins.model.Jenkins.instance.getDescriptor("jenkins.CLI").get().setEnabled(false)