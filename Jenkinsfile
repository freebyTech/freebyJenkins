def label = "worker-${UUID.randomUUID().toString()}"
// Change this when major / minor functionality changes.
def version_prefix = '1.0'
def version=''
def semVersion=''
def tag=''
def agent_tag = ''
def registry = ''
def repository = 'freebytech'    
def image = 'jenkins'
def docker_build_arguments=''


def date = new Date()
version = "${version_prefix}.${env.BUILD_NUMBER}.${date.format('MMdd')}"
semVersion = "${version_prefix}.${env.BUILD_NUMBER}"

// Standard Docker Registry or custom docker registry?
if('index.docker.io'.equalsIgnoreCase(env.REGISTRY_URL)) 
{
  echo 'Publishing to standard docker registry.'
  tag = "${repository}/${image}:${version}"
  agent_tag = "${repository}/jenkins-agent:1.0.25.1202"
  regsitry = ''
}
else 
{
  echo "Publishing to registry ${env.REGISTRY_URL}"
  tag = "${env.REGISTRY_URL}/${repository}/${image}:${version}"
  agent_tag = "${env.REGISTRY_URL}/${repository}/jenkins-agent:1.0.25.1202"
  registry = "https://${env.REGISTRY_URL}"
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
  serviceAccount: 'jenkins-builder')
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
      container('freeby-agent') 
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

        withEnv(["APPVERSION=${version}", "VERSION=${semVersion}", "REPOSITORY=${repository}"])
        {
          // Need registry credentials for agent build operation to setup chart museum connection.
          withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '5eb3385d-b03c-4802-a2b8-7f6df51f3209',
          usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_USER_PASSWORD']])
          {
            sh '''
            helm init --client-only
            helm plugin install https://github.com/chartmuseum/helm-push
            helm repo add --username ${REGISTRY_USER} --password ${REGISTRY_USER_PASSWORD} $REPOSITORY https://${REGISTRY_URL}/chartrepo/$REPOSITORY
            helm package --app-version $APPVERSION --version $VERSION ./deploy/jenkins
            helm push jenkins-$VERSION.tgz $REPOSITORY
            '''
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

if('Yes'.equalsIgnoreCase(env.OVERWRITE_JENKINS)) 
{
  podTemplate( label: label,
    containers: 
    [
      containerTemplate(name: 'freeby-agent', image: agent_tag,  ttyEnabled: true, command: 'cat')
    ], 
    volumes: 
    [
      hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
    ],
    serviceAccount: 'jenkins-builder')
  {
    node(label)
    {
      stage("Overwrite Jenkins")
      {      
        container('freeby-agent') 
        {
          withEnv(["APPVERSION=${version}", "VERSION=${semVersion}", "IMAGEVERSION=${version}", "REPOSITORY=${repository}"])
          {
            // Need registry credentials for agent build operation to setup chart museum connection.
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '5eb3385d-b03c-4802-a2b8-7f6df51f3209',
            usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_USER_PASSWORD']])
            {
              sh '''
              helm init --client-only
              helm plugin install https://github.com/chartmuseum/helm-push
              helm repo add --username ${REGISTRY_USER} --password ${REGISTRY_USER_PASSWORD} $REPOSITORY https://${REGISTRY_URL}/chartrepo/$REPOSITORY
              helm upgrade --install --version $VERSION --set Master.ImageTag=${IMAGEVERSION} --namespace build jenkins $REPOSITORY/jenkins
              '''
            }
          }
        }
      }
    }
  }
}