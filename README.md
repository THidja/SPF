 PVCs=$(kubectl get pvc --namespace="$NAMESPACE" -o json | grep -E "\"name\":\s*\".*\b$NAME" | awk -F'"' '{print $4}')
