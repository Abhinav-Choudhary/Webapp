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

echo "----------------Install and setup mysql--------------"
sudo dnf -y install mysql-server
sudo systemctl start mysqld.service
mysqladmin --user=root --password="" password "password"
sudo systemctl stop mysqld.service
echo "----------------Mysql installed-----------------------"

echo "-------------------Install Java 17---------------------"
sudo dnf -y install java-17-openjdk java-17-openjdk-devel
echo "-------------------Installed Java 17---------------------"
