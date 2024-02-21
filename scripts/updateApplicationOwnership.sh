#!/bin/bash

# Check if user exists, then update ownership
if id -u csye6225 >/dev/null 2>&1; then
  echo "User 'csye6225' exists, updating application permissions...."
  sudo chown -R csye6225:csye6225 /opt/cloud-0.0.1-SNAPSHOT.jar
else
  echo "User doesn't exists, ownership cannot be updated...exiting"
  exit 1
fi