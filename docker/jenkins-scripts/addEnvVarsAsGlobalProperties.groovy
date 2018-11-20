import hudson.slaves.EnvironmentVariablesNodeProperty
import jenkins.model.Jenkins

def instance = Jenkins.getInstance()
def globalNodeProperties = instance.getGlobalNodeProperties()
def envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class)

def newEnvVarsNodeProperty = null
def envVars = null

if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
  newEnvVarsNodeProperty = new EnvironmentVariablesNodeProperty();
  globalNodeProperties.add(newEnvVarsNodeProperty)
  println 'No environment variable node properties existed, creating new.'
  envVars = newEnvVarsNodeProperty.getEnvVars()
} else {
  println 'Environment variable node properties exist, adding to list.'
  envVars = envVarsNodePropertyList.get(0).getEnvVars()
}

def val = System.getenv('AD_DOMAIN')

// Null or empty check.
if(val?.trim()) {
  println 'Adding AD_DOMAIN'
  envVars.put('AD_DOMAIN', val)
}

val = System.getenv('REGISTRY_URL')

// Null or empty check.
if(val?.trim()) {
  println 'Adding REGISTRY_URL'
  envVars.put('REGISTRY_URL', val)
}

val = System.getenv('GIT_REPO_URL')

// Null or empty check.
if(val?.trim()) {
  println 'Adding GIT_REPO_URL'
  envVars.put('GIT_REPO_URL', val)
}

instance.save()