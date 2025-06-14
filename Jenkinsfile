pipeline {
  agent any

  parameters {
    string(name: 'SPRING_PROFILE', defaultValue: 'local', description: 'Spring Boot profile')
    string(name: 'DB_HOST', defaultValue: 'one4all.cximqo2u6zu2.ap-south-1.rds.amazonaws.com', description: 'RDS DB hostname')
    string(name: 'DB_PORT', defaultValue: '3306', description: 'RDS port')
    string(name: 'DB_NAME', defaultValue: 'one4all', description: 'Database name')
    string(name: 'DB_USER', defaultValue: 'admin', description: 'Database username')
    password(name: 'DB_PASSWORD', defaultValue: 'Oldisgold$2025', description: 'Database password (hidden)')

    string(name: 'EC2_HOST', defaultValue: 'ubuntu@13.202.212.226', description: 'EC2-B Host (user@IP)')
    string(name: 'APP_DIR', defaultValue: '/home/ubuntu/backend', description: 'Remote path to deploy backend')
    string(name: 'JAR_NAME', defaultValue: 'one4all-all4one-0.0.1-SNAPSHOT.jar', description: 'Built JAR filename')
    string(name: 'GIT_REPO_URL', defaultValue: 'https://github.com/Ramanjaneyulu-booragadda/one4all-all4one.git', description: 'Git repo URL')
    string(name: 'GIT_BRANCH', defaultValue: 'master', description: 'Branch to deploy')
  }

  environment {
    SSH_CRED_ID = "ec2-b-private-key"
  }

  stages {

    stage('Clone Repository') {
      steps {
        git branch: "${params.GIT_BRANCH}", url: "${params.GIT_REPO_URL}"
      }
    }

    stage('Build JAR') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Upload JAR & .env to EC2-B') {
      steps {
        sshagent([env.SSH_CRED_ID]) {
          sh """
            ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} 'mkdir -p ${params.APP_DIR}'

            scp -o StrictHostKeyChecking=no target/${params.JAR_NAME} ${params.EC2_HOST}:${params.APP_DIR}/app.jar

            echo 'SPRING_PROFILE=${params.SPRING_PROFILE}' > temp.env
            echo 'DB_HOST=${params.DB_HOST}' >> temp.env
            echo 'DB_PORT=${params.DB_PORT}' >> temp.env
            echo 'DB_NAME=${params.DB_NAME}' >> temp.env
            echo 'DB_USER=${params.DB_USER}' >> temp.env
            echo 'DB_PASSWORD=${params.DB_PASSWORD}' >> temp.env

            scp -o StrictHostKeyChecking=no temp.env ${params.EC2_HOST}:${params.APP_DIR}/.env
            rm -f temp.env
          """
        }
      }
    }

    stage('Restart Backend Service') {
      steps {
        sshagent([env.SSH_CRED_ID]) {
          sh """
            ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} 'sudo systemctl daemon-reexec && sudo systemctl daemon-reload && sudo systemctl restart backend'
          """
        }
      }
    }
    stage('Verify Deployment') {
      steps {
        sshagent([env.SSH_CRED_ID]) {
          sh """
            ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} 'sudo systemctl status backend || echo "Backend service is not running!"'
          """
        }
      }
    }
  }

  post {
    success {
      echo "✅ Deployment completed successfully!"
    }
    failure {
      echo "❌ Deployment failed!"
    }
  }
}