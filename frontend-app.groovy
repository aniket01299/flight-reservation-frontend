pipeline {
    agent any

    environment {
        S3_BUCKET = "vanraj-flight-reservation"
        AWS_REGION = "ap-south-1"
    }

    stages {

        stage('Code-Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/aniket01299/flight-reservation-frontend.git'
            }
        }

        stage('Code-Build') {
            steps {
                sh '''
                    npm install
                    npm run build
                '''
            }
        }

        stage('Deploy-to-S3') {
            steps {
                withCredentials([
                    aws(
                        credentialsId: 'aws_creds',
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {

                    sh '''
                        aws s3 sync dist/ s3://${S3_BUCKET} --delete
                    '''
                }
            }
        }

        stage('Display-Website-URL') {
            steps {
                script {
                    echo "========================================="
                    echo "Frontend deployed successfully!"
                    echo "S3 Website URL:"
                    echo "http://${S3_BUCKET}.s3-website-${AWS_REGION}.amazonaws.com"
                    echo "========================================="
                }
            }
        }
    }

    post {

        success {
            echo "Frontend deployment completed successfully."
        }

        failure {
            echo "Frontend deployment failed."
        }

        always {
            cleanWs()
        }
    }
}
