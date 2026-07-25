pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_IMAGE = "engrshahzaman09/foodflow"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/engrshahzaman09/foodflow-devops.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'wsl bash -c "cd /mnt/c/ProgramData/Jenkins/.jenkins/workspace/FoodFlow-Pipeline && docker build -t %DOCKER_IMAGE%:%IMAGE_TAG% -t %DOCKER_IMAGE%:latest ."'
            }
        }

        stage('Docker Push') {
            steps {
                bat 'wsl bash -c "echo %DOCKERHUB_CREDENTIALS_PSW% | docker login -u %DOCKERHUB_CREDENTIALS_USR% --password-stdin"'
                bat 'wsl bash -c "docker push %DOCKER_IMAGE%:%IMAGE_TAG%"'
                bat 'wsl bash -c "docker push %DOCKER_IMAGE%:latest"'
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                bat 'wsl bash -c "sudo k3s kubectl set image deployment/foodflow-app foodflow-app=%DOCKER_IMAGE%:%IMAGE_TAG% --namespace=foodflow"'
                bat 'wsl bash -c "sudo k3s kubectl rollout status deployment/foodflow-app --namespace=foodflow"'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs above.'
        }
        always {
            bat 'wsl bash -c "docker logout" || exit 0'
        }
    }
}
