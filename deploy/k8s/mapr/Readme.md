## Mapr FlexDriver deployment

1. Set up the node dependencies and kube resources
`kubectl apply -f kdf-*`

2. Spinn off the Mapr cluster and reference it in `mapr-sc.yaml`

3. Set up the Storage Claim class
`kubeclt apply -f mapr-sc.yaml`