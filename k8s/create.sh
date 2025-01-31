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
helm repo update

# Ingress-Nginx 설치
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --set controller.service.type=NodePort \
  --set controller.service.nodePorts.http=30080

kubectl get all -n ingress-nginx

# Argo CD 설치
helm install argocd argo/argo-cd --namespace argocd --create-namespace

# TLS 인증서 생성 (Self-Signed 또는 미리 준비된 인증서 사용)
kubectl create secret tls argocd-tls -n argocd \
  --cert=tls.crt --key=tls.key

kubectl get secret -n argocd

# ArgoCD Ingress 적용
kubectl apply -f ./ing/argocd-ing.yaml

kubectl get all -n argocd

# ArgoCD 초기 관리자 비밀번호 출력
echo "ArgoCD 초기 관리자 비밀번호:"
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
echo ""

# 포트포워딩을 백그라운드에서 실행 (nohup 사용)
echo "Ingress 포트포워딩을 백그라운드에서 실행합니다..."
nohup kubectl port-forward svc/ingress-nginx-controller -n ingress-nginx 8443:443 > /dev/null 2>&1 &

echo "스크립트 실행 완료!"
