include('./nash-reception-liquibase/Tiltfile')

# GCP pubsub emulator and UI at http://localhost:7200
k8s_yaml('infra/tilt-pubsub.yaml')
k8s_yaml('infra/tilt-pubsub-ui.yaml')
k8s_resource('pubsub', port_forwards = ['8681:8681'])
k8s_resource('pubsub-ui', port_forwards = ['7200:80'])

k8s_yaml('infra/role.yaml')
#k8s_yaml('infra/helpers/local-serviceaccount.yaml')

include('./nash-reception-adapter/Tiltfile')
include('./nash-reception-aggregates/Tiltfile')
include('./nash-reception-projections/Tiltfile')