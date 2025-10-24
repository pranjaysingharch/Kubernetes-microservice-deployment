#!/bin/bash

# Deployment script for Spring Boot Microservice A on EKS
set -e

echo "🚀 Deploying Microservice A to EKS using Helm"

# Variables (Update these values)
IMAGE_REPOSITORY="your-ecr-uri/microservice-a"
IMAGE_TAG="1.0.0"
DOMAIN_NAME="microservice-a.yourdomain.com"
NAMESPACE="default"

# Step 1: Build and push Docker image
echo "📦 Building Docker image..."
cd ../microservice-a
docker build -t microservice-a:${IMAGE_TAG} .

# Tag for ECR (uncomment and update with your ECR URI)
# docker tag microservice-a:${IMAGE_TAG} ${IMAGE_REPOSITORY}:${IMAGE_TAG}
# docker push ${IMAGE_REPOSITORY}:${IMAGE_TAG}

cd ../microservice-a-chart

# Step 2: Add Helm repositories
echo "📋 Adding Helm repositories..."
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Step 3: Update dependencies 
echo "🔧 Updating Helm dependencies..."
helm dependency update

# Step 4: Validate chart
echo "✅ Validating Helm chart..."
helm lint .

# Step 5: Deploy to EKS
echo "🎯 Deploying to EKS cluster..."
helm upgrade --install microservice-a . \
  --namespace ${NAMESPACE} \
  --set image.repository=${IMAGE_REPOSITORY} \
  --set image.tag=${IMAGE_TAG} \
  --set ingress.hosts[0].host=${DOMAIN_NAME} \
  --create-namespace \
  --wait \
  --timeout=600s

echo "🎉 Deployment completed!"

# Step 6: Show deployment status
echo "📊 Checking deployment status..."
kubectl get pods -n ${NAMESPACE} -l app.kubernetes.io/name=microservice-a-chart
kubectl get svc -n ${NAMESPACE} -l app.kubernetes.io/name=microservice-a-chart
kubectl get ingress -n ${NAMESPACE}
kubectl get hpa -n ${NAMESPACE}

echo "✨ Microservice A is now deployed on EKS!"
echo "🌐 Application will be available at: http://${DOMAIN_NAME}"
echo "📊 Health check: http://${DOMAIN_NAME}/actuator/health"
