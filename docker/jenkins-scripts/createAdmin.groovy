#!groovy
import hudson.*
import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
 
def instance = Jenkins.get()

//def envVars = Jenkins.instance.getGlobalNodeProperties()[0].getEnvVars()
//def jenkinsUser = EnvVars.masterEnvVars.get('JENKINS_USER')
//def jenkinsUrl = EnvVars.masterEnvVars.get('JENKINS_URL')

def jenkinsUser = System.getenv('JENKINS_USER')
def jenkinsUrl = System.getenv('JENKINS_URL')

// Null or empty check.
if(!jenkinsUser?.trim()) {
    def jenkinsUserPasswordFile = new File("~/creds/${jenkinsUser}@${jenkinsUrl}").text

    println 'Setting up default Jenkins Admin User.'
    def jenkinsUserPassword = jenkinsUserPasswordFile.split(System.getProperty("line.separator"))[0]

    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount(jenkinsUser, jenkinsUserPassword)
    instance.setSecurityRealm(hudsonRealm)
    
    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()
    
    Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
}


