pipeline{
    agent any
    stages{
        stage('Code-Pull'){
            steps{
                git branch: 'main', url: 'https://github.com/ayushhkamble/flight-reservation-frontend.git'    
            }
        }
        stage('Code-Build'){
            steps{
                sh '''
                    npm install
                    npm run build
                '''
            }
        }
        stage('Deploy'){
            steps{
                withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws_creds', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    sh '''
                       aws s3 sync dist/ s3://cbz-frontend-project-bucket-flight-reservation-app/ 
                    '''
                } 
            }
        }
    }
}