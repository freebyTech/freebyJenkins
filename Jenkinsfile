def image = 'jenkins'
def version_prefix = '1.0'

def tag=''
def version=''
def label = "worker-${UUID.randomUUID().toString()}"
def docker_regsitry = "" // $REGISTRY_URL
def repository = "freebytech"

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
	for(e in env){
        echo e + " is " + ${e}
    }

    //////////////////////////////////////////////////////////////////////////
    stage('Prepare') 
    {
      def date = new Date()
      version = "${version_prefix}.${date.format('MMdd')}.${env.BUILD_NUMBER}"
      tag = "${docker_registry}/${repository}/${image}:${version}"
      currentBuild.displayName = "# " + version
    }
    //////////////////////////////////////////////////////////////////////////
    stage('Build') 
    {
      container('docker') 
      {
        checkout scm
              
        docker.withRegistry("https://${docker_registry}","5eb3385d-b03c-4802-a2b8-7f6df51f3209") 
        {
          def app
          if(docker_build_arguments=='') 
          {
            app = docker.build(tag, "./src")
          }
          else 
          {
            app = docker.build(tag,"--build-arg ${docker_build_arguments} ./src")
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