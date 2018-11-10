#!groovy

import jenkins.model.*
import hudson.model.*
import hudson.security.*
import hudson.plugins.*
import hudson.plugins.active_directory.*
import hudson.*
import jenkins.*

// Setup proper integration with active directory
def adDomain = System.getenv('AD_DOMAIN')
def adDomainController = System.getenv("AD_DOMAIN_CONTROLLER")

// Null or empty check.
if(adDomain?.trim()) {
    //printLn "   Setting up AD Domain ${adDomain}"
    def instance = Jenkins.getInstance()
    adrealm = new ActiveDirectorySecurityRealm(adDomain, "site", "", null, "")
    instance.setSecurityRealm(adrealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()    
}