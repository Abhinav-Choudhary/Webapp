#!/bin/bash

echo "------------Update CentOs 8-------------"
sudo dnf update -y
echo "------------CentOs 8 Updated-------------"

echo "------------Upgrade CentOs 8 Packages-------------"
sudo dnf upgrade -y
echo "------------Upgraded CentOs 8 Packages-------------"

echo "------------Move webapp jar-------------------------"
sudo mv /tmp/cloud-0.0.1-SNAPSHOT.jar /opt/cloud-0.0.1-SNAPSHOT.jar
echo "------------Move webapp jar complete----------------"

echo "------------Setup Logs--------------------------------"
sudo mkdir /var/log/csye6225
sudo touch /var/log/csye6225/app.log
sudo chown csye6225:csye6225 /var/log/csye6225/app.log
echo "------------Logs ready--------------------------------"

echo "------------Move services file-------------------------"
sudo mv /tmp/csye6225.service /etc/systemd/system/csye6225.service
echo "------------Move services file complete----------------"

echo "-------------------Install Java 17---------------------"
sudo dnf -y install java-17-openjdk java-17-openjdk-devel
echo "-------------------Installed Java 17---------------------"

echo "-------------------Install Google Ops Agent---------------------"
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install \
  --version=latest
echo "-------------------Installed Google Ops Agent---------------------"
