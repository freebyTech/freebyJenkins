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

// For any environment variable starting with "addenv_" add it to Jenkins without the "addenv_"
Map<String, String> env = System.getenv();
for (String envName : env.keySet()) {
  if(envName.startsWith("addenv_")) {
      def trueKey = envName.substring(7)
      println "Adding ${trueKey}"
      envVars.put(trueKey, env.get(envName))
    }
}

instance.save()