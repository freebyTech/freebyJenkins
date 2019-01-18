#!groovy

import jenkins.model.*
import hudson.model.*
import hudson.security.*
import hudson.plugins.*
import hudson.plugins.active_directory.*
import hudson.*
import jenkins.*

// Setup proper integration with active directory
def adDomain = System.getenv('env_AD_DOMAIN')

// Null or empty check.
if(adDomain?.trim()) {
    // Adds basic active directory support, will update in the future.
    def instance = Jenkins.getInstance()
    adrealm = new ActiveDirectorySecurityRealm(adDomain, "site", "", null, "")
    instance.setSecurityRealm(adrealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()    
}