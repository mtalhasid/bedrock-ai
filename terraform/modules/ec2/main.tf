terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

locals {
  user_data = <<-EOF
    #!/bin/bash
    set -euo pipefail

    # Install Docker
    yum update -y
    yum install -y docker
    systemctl start docker
    systemctl enable docker

    # Login to ECR
    aws ecr get-login-password --region ${var.aws_region} | \
    docker login --username AWS --password-stdin ${var.ecr_repository_url}

    # Fetch secrets from SSM
    DB_URL=$(aws ssm get-parameter \
      --name "${var.db_url_parameter_name}" \
      --with-decryption \
      --query "Parameter.Value" \
      --output text \
      --region ${var.aws_region})

    AI_API_KEY=$(aws ssm get-parameter \
      --name "${var.ai_api_key_parameter_name}" \
      --with-decryption \
      --query "Parameter.Value" \
      --output text \
      --region ${var.aws_region})

    # Pull and run the app
    docker pull ${var.ecr_repository_url}:latest

    docker run -d \
      -p 8080:8080 \
      -e DB_URL=$DB_URL \
      -e AI_API_KEY=$AI_API_KEY \
      --restart always \
      --log-driver awslogs \
      --log-opt awslogs-region=${var.aws_region} \
      --log-opt awslogs-group=${var.log_group_name} \
      ${var.ecr_repository_url}:latest
  EOF
}

resource "aws_instance" "app_1" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.instance_type
  subnet_id              = var.public_subnet_1_id
  vpc_security_group_ids = [var.ec2_sg_id]
  iam_instance_profile   = var.instance_profile_name
  user_data              = local.user_data

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name        = "${var.project_name}-ec2-1"
    Environment = var.environment
  }
}

resource "aws_instance" "app_2" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.instance_type
  subnet_id              = var.public_subnet_2_id
  vpc_security_group_ids = [var.ec2_sg_id]
  iam_instance_profile   = var.instance_profile_name
  user_data              = local.user_data

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name        = "${var.project_name}-ec2-2"
    Environment = var.environment
  }
}

resource "aws_lb_target_group_attachment" "app_1" {
  target_group_arn = var.target_group_arn
  target_id        = aws_instance.app_1.id
  port             = 8080
}

resource "aws_lb_target_group_attachment" "app_2" {
  target_group_arn = var.target_group_arn
  target_id        = aws_instance.app_2.id
  port             = 8080
}
