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
    def version=''
    def tag=''
    def registry = ''
    def repository = 'freebytech'    
    def image = 'jenkins'
    def docker_build_arguments=''

    stage('Setup Build Settings') 
    {
      def date = new Date()
      version = "${version_prefix}.${env.BUILD_NUMBER}.${date.format('MMdd')}"

      // Standard Docker Registry?
      if('index.docker.io'.equalsIgnoreCase(env.REGISTRY_URL)) 
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
          if("develop".equalsIgnoreCase(env.GIT_BRANCH)) 
          {
            app.push('latest')
          }          
        }
      }
    }
  }  
}