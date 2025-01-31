# !/bin/bash

kind delete cluster --name my-cluster
#docker-compose down
#rm -rf ./volumes/data

helm uninstall ingress-nginx -n ingress-nginx

helm uninstall argocd -n argocd
kubectl delete crd applications.argoproj.io appprojects.argoproj.io argocdextensions.argoproj.io
