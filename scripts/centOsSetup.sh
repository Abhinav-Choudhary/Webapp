#!/bin/bash

echo "------------Update CentOs 8-------------"
sudo dnf update -y
echo "------------CentOs 8 Updated-------------"

echo "------------Upgrade CentOs 8 Packages-------------"
sudo dnf upgrade -y
echo "------------Upgraded CentOs 8 Packages-------------"

echo "------------Install wget-------------"
sudo dnf -y install wget
echo "------------Installed wget-------------"

echo "------------Move webapp jar-------------------------"
sudo mv /tmp/cloud-0.0.1-SNAPSHOT.jar /opt/cloud-0.0.1-SNAPSHOT.jar
echo "------------Move webapp jar complete----------------"

echo "----------------Install and setup mysql--------------"
sudo dnf -y install mysql-server
export MYSQL_ROOT_PASSWORD="password"
sudo mysql -e 'ALTER USER "root"@"localhost" IDENTIFIED BY "'$MYSQL_ROOT_PASSWORD'";'
echo "----------------Mysql installed-----------------------"

echo "-------------------Install Java 17---------------------"
sudo dnf -y install java-17-openjdk java-17-openjdk-devel
echo "-------------------Installed Java 17---------------------"

echo "--------------------Install and setup maven---------------"
sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar -xvf apache-maven-3.9.6-bin.tar.gz -C /usr/lib
echo "--------------------Installed maven---------------"

echo "-------------------Setup config files for java and maven--------------------"
echo "export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which java)))))
export PATH=$PATH:$JAVA_HOME/bin" | sudo tee /etc/profile.d/java.sh > /dev/null
chmod +x /etc/profile.d/java.sh
source /etc/profile.d/java.sh

echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-17.0.6.0.9-0.3.ea.el8.x86_64
export M2_HOME=/usr/lib/apache-maven-3.9.6
export MAVEN_HOME=/usr/lib/apache-maven-3.9.6
export PATH=${M2_HOME}/bin:${PATH}" | sudo tee /etc/profile.d/maven.sh > /dev/null
chmod +x /etc/profile.d/maven.sh
source /etc/profile.d/maven.sh
echo "-------------------Setup config files for java and maven Complete--------------------"