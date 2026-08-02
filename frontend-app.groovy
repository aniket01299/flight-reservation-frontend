pipeline {
    agent any

    environment {
        AWS_REGION = "ap-south-1"
        S3_BUCKET  = "vanraj-flight-reservation"
    }

    stages {

        stage('Code Checkout') {
            steps {
                git branch: 'main',
                credentialsId: 'github-credentials',
                url: 'https://github.com/aniket01299/flight-reservation-frontend.git'
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'npm install'
            }
        }

        stage('Build Frontend') {
            steps {
                sh 'npm run build'
            }
        }

        stage('Deploy to S3') {
            steps {
                withCredentials([
                    aws(
                        credentialsId: 'AWS-Cred',
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {

                    sh '''
                    aws s3 sync dist/ s3://${S3_BUCKET} --delete
                    aws s3 ls s3://${S3_BUCKET}
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
