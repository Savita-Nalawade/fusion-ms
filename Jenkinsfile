pipeline {

	agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '3', artifactNumToKeepStr: '3'))
    }

    tools {
        maven 'maven_3.9.4'
    }

    stages {
        stage('Code Compilation') {
            steps {
                echo 'Code Compilation is In Progress!'
                sh 'mvn clean compile'
                echo 'Code Compilation is Completed Successfully!'
            }
        }
        stage('Code QA Execution') {
            steps {
                echo 'JUnit Test Case Check in Progress!'
                sh 'mvn clean test'
                echo 'JUnit Test Case Check Completed!'
            }
        }
        stage('SonarQube Code Quality') {
                    environment {
                        scannerHome = tool 'qube'
                    }
                    steps {
                        echo 'Starting SonarQube Code Quality Scan...'
                        withSonarQubeEnv('sonar-server') {
                            sh 'mvn sonar:sonar'
                        }
                        echo 'SonarQube Scan Completed. Checking Quality Gate...'
                        timeout(time: 10, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                        echo 'Quality Gate Check Completed!'
                    }
                }
        stage('Code Package') {
            steps {
                echo 'Creating WAR Artifact'
                sh 'mvn clean package'
                echo 'Artifact Creation Completed'
            }
        }
        stage('Building & Tag Docker Image') {
            steps {
                echo "Starting Building Docker Image"
                sh "docker build -t savitanalawade/fusion-ms ."
                sh "docker build -t fusion-ms ."
                echo 'Docker Image Build Completed'
            }
        }
        stage('Docker Image Scanning') {
                    steps {
                        echo 'Scanning Docker Image with Trivy...'
                        sh 'trivy image ${DOCKER_IMAGE}:latest || echo "Scan Failed - Proceeding with Caution"'
                        echo 'Docker Image Scanning Completed!'
                    }
                }
        stage(' Docker push to Docker Hub') {
           steps {
              script {
                 withCredentials([string(credentialsId: 'dockerhubCred', variable: 'dockerhubCred')]){
                 sh 'docker login docker.io -u savitanalawade -p ${dockerhubCred}'
                 echo "Push Docker Image to DockerHub : In Progress"
                 sh 'docker push savitanalawade/fusion-ms:latest'
                 echo "Push Docker Image to DockerHub : In Progress"
                 }
              }
            }
        }
        stage(' Docker Image Push to Amazon ECR') {
           steps {
              script {
                 withDockerRegistry([credentialsId:'ecr:ap-south-1:ecr-credentials', url:"https://664418982701.dkr.ecr.ap-south-1.amazonaws.com"]){
                 sh """
                 echo "List the docker images present in local"
                 docker images
                 echo "Tagging the Docker Image: In Progress"
                 docker tag fusion-ms:latest 664418982701.dkr.ecr.ap-south-1.amazonaws.com/fusion-ms:latest
                 echo "Tagging the Docker Image: Completed"
                 echo "Push Docker Image to ECR : In Progress"
                 docker push 664418982701.dkr.ecr.ap-south-1.amazonaws.com/fusion-ms:latest
                 echo "Push Docker Image to ECR : Completed"
                 """
                 }
              }
           }
        }
        stage('Upload Docker Image to Nexus') {
                    steps {
                        script {
                            withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                                sh 'docker login http://43.205.228.9:8085/repository/fusion-ms/ -u admin -p ${PASSWORD}'
                                echo "Push Docker Image to Nexus : In Progress"
                                sh 'docker tag fusion-ms 43.205.228.9:8085/fusion-ms:latest'
                                sh 'docker push 43.205.228.9:8085/fusion-ms'
                                echo "Push Docker Image to Nexus : Completed"
                            }
                        }
                    }
                }
        stage('Cleanup Docker Images') {
                    steps {
                        echo 'Cleaning up local Docker images...'
                        sh "docker rmi -f ${DOCKER_IMAGE}:latest || true"
                        sh "docker rmi -f ${ECR_REPO}:latest || true"
                        echo 'Local Docker images deleted successfully!'
                    }
                }

    }
}