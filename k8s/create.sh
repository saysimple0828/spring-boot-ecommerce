#!/bin/bash

echo "Kind 클러스터 생성..."
kind create cluster --name my-cluster --config kind-config.yaml

# Helm 저장소 추가 및 업데이트
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo add argo https://argoproj.github.io/argo-helm
helm repo add gitlab https://charts.gitlab.io/
helm repo add harbor https://helm.goharbor.io
helm repo add bitnami https://charts.bitnami.com/bitnami

helm repo update

echo "Metrics server 설치"
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl get pods -n kube-system

echo "ingress-nginx 설치..."
helm install ingress-nginx ingress-nginx/ingress-nginx \
    --namespace ingress-nginx \
    --create-namespace \
    --set controller.service.type=NodePort \
    --set controller.service.nodePorts.http=30080 \
    --set controller.service.nodePorts.https=30443

kubectl get all -n ingress-nginx

echo "Harbor 설치..."
kubectl create namespace harbor
kubectl create secret tls harbor-tls -n harbor \
  --cert=tls.crt --key=tls.key
helm install harbor harbor/harbor -n harbor -f ./harbor/values.yaml

echo "GitLab 설치..."
kubectl create namespace gitlab

helm uninstall gitlab -n gitlab
helm install gitlab gitlab/gitlab \
  -n gitlab \
  --create-namespace \
  -f ./gitlab/values.yaml

kubectl create secret tls gitlab-tls \
  -n gitlab \
  --cert=tls.crt --key=tls.key

kubectl get all -n gitlab

echo "CoreDNS에 호스트 추가"
cp coredns_org.yaml coredns.yaml

INGRESS_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.spec.clusterIP}')
if [ -z "$INGRESS_IP" ]; then
  echo "ingress-nginx-controller 서비스의 IP를 조회하지 못했습니다. 확인하세요."
  exit 1
fi
echo "조회된 Ingress IP: $INGRESS_IP"
sed -i '' 's/\$INGRESS_NGINX/'"$INGRESS_IP"'/g' coredns.yaml
kubectl apply --force -f coredns.yaml

kubectl logs -n kube-system -l k8s-app=kube-dns

echo "GitLab Runner 설치..."
helm install --namespace gitlab gitlab-runner -f ./gitlab/runner/values.yaml gitlab/gitlab-runner
helm uninstall --namespace gitlab gitlab-runner

k get pod -n gitlab | grep runner

echo "pwd 초기화"
> pwd.txt

echo "GitLab 초기 관리자 아이디: root, 비밀번호:"
kubectl get secret gitlab-gitlab-initial-root-password -n gitlab -ojsonpath='{.data.password}' | base64 --decode >> pwd.txt
echo "" >> pwd.txt

echo
echo "ArgoCD 설치..."
kubectl create namespace argocd
kubectl create secret tls argocd-tls -n argocd \
  --cert=tls.crt --key=tls.key
helm install argocd argo/argo-cd --namespace argocd --create-namespace
kubectl apply -f ./argocd/argocd-ing.yaml

kubectl get secret -n argocd
kubectl get ingress -n argocd
kubectl get all -n argocd

echo "ArgoCD 초기 관리자 아이디: admin, 비밀번호:"
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 --decode >> pwd.txt
echo "" >> pwd.txt

# Redis Cluster 설치
helm install my-redis-cluster bitnami/redis-cluster --namespace redis --create-namespace

10.96.96.139

# 서비스 호스트 추가
grep -qxF "127.0.0.1 gitlab.gitlab.local" /etc/hosts || echo "127.0.0.1 gitlab.gitlab.local" | sudo tee -a /etc/hosts
grep -qxF "127.0.0.1 argocd.local" /etc/hosts || echo "127.0.0.1 argocd.local" | sudo tee -a /etc/hosts
grep -qxF "127.0.0.1 harbor.domain" /etc/hosts || echo "127.0.0.1 harbor.domain" | sudo tee -a /etc/hosts

echo "이커머스 설치 완료."
