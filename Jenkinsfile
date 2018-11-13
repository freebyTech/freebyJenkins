def label = "worker-${UUID.randomUUID().toString()}"
// Change this when major / minor functionality changes.
def version_prefix = '1.0'

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
    def image = 'jenkins'
    def tag=''
    def version=''
    def registry = ''
    def repository = 'freebytech'    
    def docker_build_arguments=''

	//////////////////////////////////////////////////////////////////////////
    stage('Setup Build Vars') 
    {
      def date = new Date()
      version = "${version_prefix}.${env.BUILD_NUMBER}.${date.format('MMdd')}"

      // Standard Docker Registry?
      if('index.docker.io'.equalsIgnoreCase(registry)) 
      {
        echo 'Publishing to standard docker registry.'
        tag = "${repository}/${image}:${version}"
        regsitry = ''
      }
      else 
      {
        echo "Publishing to registry ${env.REGISTRY_URL}"
        tag = "${env.REGISTRY_URL}/${repository}/${image}:${version}"
        registry = "https://${env.REGISTRY_URL}"
      }      
      currentBuild.displayName = "# " + version
    }
    //////////////////////////////////////////////////////////////////////////
    stage('Build Image') 
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
        }
      }
    }
    //////////////////////////////////////////////////////////////////////////
    stage('Test')
    {
      sh 'echo testing' 
    }


    //////////////////////////////////////////////////////////////////////////
  } // node
  
  //////////////////////////////////////////////////////////////////////////
  stage("approval")
  {
      // we need a first milestone step so that all jobs entering this stage are tracked an can be aborted if needed
      milestone 1
    
      timeout(time: 10, unit: 'MINUTES') 
      {
        script 
        {
          env.ENVIRONMENT = input message: 'User input required', ok: 'Select',
            parameters: [choice(name: 'ENVIRONMENT', choices: 'Dev\nQA\nProd', description: 'Choose an environment')]
        }
      }
      // this will kill any job which is still in the input step
      milestone 2
  }
  //////////////////////////////////////////////////////////////////////////
  node(label)
  {
    stage("deploy")
    {
      sh "echo deploying to... ${env.ENVIRONMENT}"
    }
  }// node
}