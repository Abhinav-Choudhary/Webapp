#!/bin/bash

cat <<EOF | sudo tee -a /etc/google-cloud-ops-agent/config.yaml
logging:
  receivers:
    webapp-logger:
      type: files
      include_paths:
      - /var/log/csye6225/errors.log
      - /var/log/csye6225/info.log
  processors:
    webapp-processor:
      type: parse_json
      time_key: instant.epochSecond
      time_format: "%s"
    move_severity:
      type: modify_fields
      fields:
        severity:
          move_from: jsonPayload.level
  service:
    pipelines:
      pipeline_webapp:
        receivers: [ webapp-logger ]
        processors: [webapp-processor, move_severity]
EOF