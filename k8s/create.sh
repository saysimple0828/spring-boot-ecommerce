#!/bin/bash

# 포트포워딩이 이미 실행 중인지 확인
PORT_FORWARD_PID=$(pgrep -f "kubectl port-forward svc/ingress-nginx-controller -n ingress-nginx 8443:443")

if [ -n "$PORT_FORWARD_PID" ]; then
  echo "포트포워딩이 이미 실행 중입니다. 종료합니다..."
  kill "$PORT_FORWARD_PID"
  sleep 2  # 안전하게 종료될 때까지 대기
fi

echo "Kind 클러스터 생성..."
kind create cluster --name my-cluster --config kind-config.yaml

# Helm 저장소 추가 및 업데이트
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo add argo https://argoproj.github.io/argo-helm
helm repo add gitlab https://charts.gitlab.io/

helm repo update

# Metrics server 설치
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

kubectl get pods -n kube-system

echo "ingress-nginx 설치..."
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --set controller.service.type=NodePort \
  --set controller.service.nodePorts.http=30080
kubectl get all -n ingress-nginx

echo "GitLab 설치..."
kubectl create namespace gitlab
helm install gitlab gitlab/gitlab \
  -n gitlab \
  --create-namespace \
  -f ./gitlab/values.yaml
kubectl create secret tls gitlab-tls \
  -n gitlab \
  --cert=tls.crt --key=tls.key

kubectl get secret -n gitlab
kubectl get all -n gitlab

echo "GitLab 초기 관리자 비밀번호:"
kubectl get secret gitlab-gitlab-initial-root-password -n gitlab -ojsonpath='{.data.password}' | base64 --decode ; echo

echo "ArgoCD 설치..."
kubectl create namespace argocd
kubectl create secret tls argocd-tls -n argocd \
  --cert=tls.crt --key=tls.key
helm install argocd argo/argo-cd --namespace argocd --create-namespace
kubectl apply -f ./argocd/argocd-ing.yaml

kubectl get secret -n argocd
kubectl get all -n argocd

# ArgoCD 초기 관리자 비밀번호 출력
echo "ArgoCD 초기 관리자 비밀번호:"
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d

# 서비스 호스트 추가
grep -qxF "127.0.0.1 gitlab.gitlab.local" /etc/hosts || echo "127.0.0.1 gitlab.gitlab.local" | sudo tee -a /etc/hosts
grep -qxF "127.0.0.1 argocd.local" /etc/hosts || echo "127.0.0.1 argocd.local" | sudo tee -a /etc/hosts

# 포트포워딩을 백그라운드에서 실행 (nohup 사용)
echo "Ingress 포트포워딩을 백그라운드에서 실행합니다..."
nohup kubectl port-forward svc/ingress-nginx-controller -n ingress-nginx 8443:443 > /dev/null 2>&1 &

echo "스크립트 실행 완료!"
