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
  envVars = newEnvVarsNodeProperty.getEnvVars()
} else {
  envVars = envVarsNodePropertyList.get(0).getEnvVars()
}

def val = System.getenv('AD_DOMAIN')

// Null or empty check.
if(val?.trim()) {
  envVars.put('AD_DOMAIN', val)
}

val = System.getenv('REGISTRY_URL')

// Null or empty check.
if(val?.trim()) {
  envVars.put('REGISTRY_URL', val)
}

val = System.getenv('GIT_REPO_URL')

// Null or empty check.
if(val?.trim()) {
  envVars.put('GIT_REPO_URL', val)
}

instance.save()