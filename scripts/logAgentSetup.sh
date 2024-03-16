#!/bin/bash

cat <<EOF | sudo tee -a /etc/google-cloud-ops-agent/config.yaml
logging:
  receivers:
    webapp-logger:
      type: files
      include_paths:
      - /var/log/csye6225/app.log
  service:
    pipelines:
      default_pipeline:
        receivers: [webapp-logger]
EOF