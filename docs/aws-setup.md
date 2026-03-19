# AWS EC2 Deployment Guide

## 1. Launch EC2 Instance
- Instance Type: **t3.medium** (2 vCPU, 4GB RAM)
- AMI: **Ubuntu 22.04 LTS**
- Storage: **20GB gp3**

## 2. Configure Security Group Rules
- **Port 22 (SSH)**: Allow from your specific IP only
- **Port 80 (HTTP)**: Allow from 0.0.0.0/0 (Angular frontend)
- **Port 443 (HTTPS)**: Allow from 0.0.0.0/0
- **Port 8080 (Java API)**: Allow from 0.0.0.0/0
- **Port 8001 (Python Bridge)**: Internal traffic only 

## 3. Install Docker on EC2
Connect via SSH and run:
`sudo apt update && sudo apt upgrade -y`
`sudo apt install docker.io docker-compose -y`
`sudo usermod -aG docker ubuntu`
`sudo systemctl enable docker`

## 4. Deploy Application
1. Copy the project to EC2: `scp -r`
2. Connect to EC2
3. Run `docker-compose up -d`
4. Verify using `docker ps`
