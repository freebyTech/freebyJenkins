def label = "worker-${UUID.randomUUID().toString()}"
// Change this when major / minor functionality changes.
def version_prefix = '1.0'
def version=''
def tag=''
def agent_tag = ''
def registry = ''
def repository = 'freebytech'    
def image = 'jenkins'
def docker_build_arguments=''


def date = new Date()
version = "${version_prefix}.${env.BUILD_NUMBER}.${date.format('MMdd')}"
      
// Standard Docker Registry or custom docker registry?
if('index.docker.io'.equalsIgnoreCase(env.REGISTRY_URL)) 
{
  echo 'Publishing to standard docker registry.'
  tag = "${repository}/${image}:${version}"
  agent_tag = "${repository}/jenkins-agent:latest"
  regsitry = ''
}
else 
{
  echo "Publishing to registry ${env.REGISTRY_URL}"
  tag = "${env.REGISTRY_URL}/${repository}/${image}:${version}"
  agent_tag = "${env.REGISTRY_URL}/${repository}/jenkins-agent:latest"
  registry = "https://${env.REGISTRY_URL}"
}  
    
podTemplate( label: label,
  containers: 
  [
    containerTemplate(name: 'docker', image: 'docker:18.06', ttyEnabled: true, command: 'cat')
  ], 
  volumes: 
  [
    hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
  ])
{
  node(label) 
  {
    stage('Setup Build Settings') 
    {
      echo '--------------------------------------------------'
      echo "Building version ${version} for branch ${env.BRANCH_NAME}"
      echo '--------------------------------------------------'          
      currentBuild.displayName = "# " + version
    }
    
    stage('Build Image and Publish') 
    {
      container('docker') 
      {
        checkout scm
        
        // Use guid of known user for registry security
        docker.withRegistry(registry, "5eb3385d-b03c-4802-a2b8-7f6df51f3209") 
        {
          def app
          if(docker_build_arguments=='') 
          {
            app = docker.build(tag, "./docker")
          }
          else 
          {
            app = docker.build(tag,"--build-arg ${docker_build_arguments} ./docker")
          }
          app.push()
          if("develop".equalsIgnoreCase(env.BRANCH_NAME)) 
          {
            app.push('latest')
          }          
        }
      }
    }

    stage("Get Approval for Deployment")
    {
        // we need a first milestone step so that all jobs entering this stage are tracked an can be aborted if needed
        milestone 1
      
        timeout(time: 10, unit: 'MINUTES') 
        {
          script 
          {
            env.OVERWRITE_JENKINS = input message: 'Overwrite current jenkins server?', ok: 'Select',
              parameters: [choice(name: 'OVERWRITE_JENKINS', choices: 'No\nYes', description: 'Whether or not to overwrite current jenkins')]
          }
        }
        // this will kill any job which is still in the input step
        milestone 2
    }
  }
}

podTemplate( label: label,
  containers: 
  [
    containerTemplate(name: 'freeby-agent', image: agent_tag,  ttyEnabled: true, command: 'cat')
  ], 
  volumes: 
  [
    hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
  ],
  imagePullSecrets: [ 'freebytech-regcred' ])
{
  node(label)
  {
    stage("Overwrite Jenkins")
    {      
      container('freeby-agent') 
      {
        sh 'kubectl version'
        sh 'helm init --client-only'
        sh 'helm version'
      }
    }
  }
}