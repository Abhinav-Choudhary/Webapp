#!/bin/bash

echo "---------------------------------------Running daemon reload----------------------------------------------"
sudo systemctl daemon-reload

echo "---------------------------------------Setting mysqld to run by default-----------------------------------"
sudo systemctl enable mysqld.service

echo "--------------------------------------Setting csye6225.service to run by default--------------------------"
sudo systemctl enable csye6225.service