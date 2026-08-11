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
  ssm_prefix = var.ssm_parameter_prefix

  user_data = <<-EOF
    #!/bin/bash
    set -euo pipefail

    SSM_PREFIX="${local.ssm_prefix}"
    REGION="${var.aws_region}"

    fetch_param() {
      aws ssm get-parameter \
        --name "$1" \
        --with-decryption \
        --query "Parameter.Value" \
        --output text \
        --region "$REGION"
    }

    yum update -y
    yum install -y docker
    systemctl start docker
    systemctl enable docker

    aws ecr get-login-password --region "$REGION" | \
      docker login --username AWS --password-stdin ${var.ecr_repository_url}

    DB_URL=$(fetch_param "$SSM_PREFIX/DB_URL")
    DB_USER=$(fetch_param "$SSM_PREFIX/DB_USER")
    DB_PASSWORD=$(fetch_param "$SSM_PREFIX/DB_PASSWORD")
    REDIS_HOST=$(fetch_param "$SSM_PREFIX/REDIS_HOST")
    REDIS_PORT=$(fetch_param "$SSM_PREFIX/REDIS_PORT")
    REDIS_PASSWORD=$(fetch_param "$SSM_PREFIX/REDIS_PASSWORD")
    JWT_SECRET=$(fetch_param "$SSM_PREFIX/JWT_SECRET")
    AI_API_KEY=$(fetch_param "$SSM_PREFIX/AI_API_KEY")

    docker pull ${var.ecr_repository_url}:latest

    docker run -d \
      --name bedrock-ai \
      -p 8080:8080 \
      -e DB_URL="$DB_URL" \
      -e DB_USER="$DB_USER" \
      -e DB_PASSWORD="$DB_PASSWORD" \
      -e REDIS_HOST="$REDIS_HOST" \
      -e REDIS_PORT="$REDIS_PORT" \
      -e REDIS_PASSWORD="$REDIS_PASSWORD" \
      -e JWT_SECRET="$JWT_SECRET" \
      -e AI_API_KEY="$AI_API_KEY" \
      --restart always \
      --log-driver awslogs \
      --log-opt awslogs-region=$REGION \
      --log-opt awslogs-group=${var.log_group_name} \
      ${var.ecr_repository_url}:latest
  EOF
}

resource "aws_instance" "app_1" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = var.public_subnet_1_id
  vpc_security_group_ids      = [var.ec2_sg_id]
  iam_instance_profile        = var.instance_profile_name
  user_data                   = local.user_data
  user_data_replace_on_change = true

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name        = "${var.project_name}-ec2-1"
    Environment = var.environment
  }
}

resource "aws_instance" "app_2" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = var.public_subnet_2_id
  vpc_security_group_ids      = [var.ec2_sg_id]
  iam_instance_profile        = var.instance_profile_name
  user_data                   = local.user_data
  user_data_replace_on_change = true

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
