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

System.getenv().forEach((k, v) -> {
  // For any environment variable starting with "addenv_" add it to Jenkins without the "addenv_"
  if(k.startsWith("addenv_")) {
    def trueKey = k.substring(6)
    println "Adding ${trueKey}"
    envVars.put(trueKey, v)
  }
});

instance.save()