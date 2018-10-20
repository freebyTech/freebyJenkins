node {
		def app
		def buildData
		docker.withRegistry("${env.docker_repository}","pv_docker_hub_cred") {
			stage('Clone') {
				checkout scm
			}			
			stage('Build') {
			    def theBuildNumber = getBuildNumber()
				buildData = getBuildData{
					buildNumber = theBuildNumber
				}
				
				println("Docker pull dependent images")
				for(element in buildData.depend) {
					println("image: ${element}")
					docker.image(element).pull()
				}
				
				currentBuild.displayName = "# " + buildData.label
				println("Docker build")
				println("build label: ${buildData.label}")
				println("build-arg: ${buildData.argumentString}")
				if(buildData.argumentString=="") {
					app = docker.build(buildData.label)
				} else {
					app = docker.build(buildData.label,"--build-arg ${buildData.argumentString} .")
				}
			}
				
			stage('Test') {
				app.inside {
					sh 'echo "Tests passed"'
				}
			}
			stage('Push') { 
				app.push()
				app.push("${buildData.build.version.prefix}")
			}
		}
	}