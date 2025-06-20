pipeline {
  agent any

  parameters {
    string(name: 'SPRING_PROFILE', defaultValue: 'dev', description: 'Spring Boot profile')
    string(name: 'DB_HOST', defaultValue: 'one4all.cximqo2u6zu2.ap-south-1.rds.amazonaws.com', description: 'RDS DB hostname')
    string(name: 'DB_PORT', defaultValue: '3306', description: 'RDS port')
    string(name: 'DB_NAME', defaultValue: 'one4all', description: 'Database name')
    string(name: 'DB_USER', defaultValue: 'admin', description: 'Database username')
    password(name: 'DB_PASSWORD', defaultValue: '', description: 'Database password (hidden)')

    string(name: 'AWS_SES_ACCESS_KEY', defaultValue: '', description: 'AWS SES access key')
    password(name: 'AWS_SES_SECRET_KEY', defaultValue: '', description: 'AWS SES secret key')
    string(name: 'AWS_SES_VERIFIED_SENDER', defaultValue: '', description: 'AWS SES verified sender email')

    string(name: 'EC2_HOST', defaultValue: 'ubuntu@13.202.212.226', description: 'EC2-B Host (user@IP)')
    string(name: 'APP_DIR', defaultValue: '/home/ubuntu/backend', description: 'Remote path to deploy backend')
    string(name: 'JAR_NAME', defaultValue: 'one4all-all4one-0.0.1-SNAPSHOT.jar', description: 'Built JAR filename')
    string(name: 'GIT_REPO_URL', defaultValue: 'https://github.com/Ramanjaneyulu-booragadda/one4all-all4one.git', description: 'Git repo URL')
    string(name: 'GIT_BRANCH', defaultValue: 'master', description: 'Branch to deploy')
    string(name: 'MICROSERVICE_URL', defaultValue: '', description: 'URL of the second microservice')
  }

  environment {
    SSH_CRED_ID = "ec2-b-private-key"
  }

  stages {

    stage('📥 Clone Repository') {
      steps {
        echo 'Cloning Git repository...'
        git branch: "${params.GIT_BRANCH}", url: "${params.GIT_REPO_URL}"
      }
    }

    stage('🏗️ Build JAR') {
      steps {
        echo 'Building the JAR file using Maven...'
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('📦 Prepare Deployment Files') {
      steps {
        echo 'Creating .env file and backend.service...'
        writeFile file: 'temp.env', text: """
SPRING_PROFILE=${params.SPRING_PROFILE}
DB_HOST=${params.DB_HOST}
DB_PORT=${params.DB_PORT}
DB_NAME=${params.DB_NAME}
DB_USER=${params.DB_USER}
DB_PASSWORD=${params.DB_PASSWORD}
AWS_SES_ACCESS_KEY=${params.AWS_SES_ACCESS_KEY}
AWS_SES_SECRET_KEY=${params.AWS_SES_SECRET_KEY}
AWS_SES_VERIFIED_SENDER=${params.AWS_SES_VERIFIED_SENDER}
MICROSERVICE_URL=${params.MICROSERVICE_URL}
"""

        writeFile file: 'backend.service', text: """
[Unit]
Description=Spring Boot Backend Service
After=network.target

[Service]
User=ubuntu
WorkingDirectory=${params.APP_DIR}
ExecStart=/usr/bin/env bash -c 'set -a && source ${params.APP_DIR}/.env && exec /usr/bin/java -jar ${params.APP_DIR}/app.jar --spring.profiles.active=${params.SPRING_PROFILE}'
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
"""
      }
    }

    stage('📤 Upload Files to EC2') {
      steps {
        sshagent([env.SSH_CRED_ID]) {
          sh """
            echo '📁 Creating app directory on EC2...'
            ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} 'mkdir -p ${params.APP_DIR}'

            echo '⬆️ Uploading JAR...'
            scp -o StrictHostKeyChecking=no target/${params.JAR_NAME} ${params.EC2_HOST}:${params.APP_DIR}/app.jar

            echo '⬆️ Uploading .env...'
            scp -o StrictHostKeyChecking=no temp.env ${params.EC2_HOST}:${params.APP_DIR}/.env

            echo '⬆️ Uploading backend.service file...'
            scp -o StrictHostKeyChecking=no backend.service ${params.EC2_HOST}:/tmp/backend.service

            echo '📦 Installing backend.service...'
            ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} '
              sudo mv /tmp/backend.service /etc/systemd/system/backend.service && \
              sudo chmod 644 /etc/systemd/system/backend.service && \
              sudo systemctl daemon-reexec && \
              sudo systemctl daemon-reload
            '

            echo '🧹 Cleaning temp files...'
            rm -f temp.env backend.service
          """
        }
      }
    }

    stage('🚀 Restart Backend Service') {
  steps {
    sshagent([env.SSH_CRED_ID]) {
      echo 'Restarting backend systemd service on EC2...'
      sh """
        ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} \\
          'sudo systemctl daemon-reexec && \\
           sudo systemctl daemon-reload && \\
           sudo systemctl restart backend'
      """
    }
  }
}

    stage('🔍 Verify Deployment') {
      steps {
        sshagent([env.SSH_CRED_ID]) {
          echo '🔎 Checking backend service status...'
          sh """
            ssh -o StrictHostKeyChecking=no ${params.EC2_HOST} '
              sudo systemctl status backend || echo "⚠️ Backend service is not running!"'
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
      echo "❌ Deployment failed! Check logs above."
    }
  }
}