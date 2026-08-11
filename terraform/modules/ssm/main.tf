terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}

locals {
  prefix = "/${var.project_name}/${var.environment}"
}

resource "aws_ssm_parameter" "db_url" {
  name  = "${local.prefix}/DB_URL"
  type  = "SecureString"
  value = var.db_url

  tags = {
    Name        = "${var.project_name}-db-url"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "db_user" {
  name  = "${local.prefix}/DB_USER"
  type  = "SecureString"
  value = var.db_user

  tags = {
    Name        = "${var.project_name}-db-user"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "db_password" {
  name  = "${local.prefix}/DB_PASSWORD"
  type  = "SecureString"
  value = var.db_password

  tags = {
    Name        = "${var.project_name}-db-password"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "redis_host" {
  name  = "${local.prefix}/REDIS_HOST"
  type  = "SecureString"
  value = var.redis_host

  tags = {
    Name        = "${var.project_name}-redis-host"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "redis_port" {
  name  = "${local.prefix}/REDIS_PORT"
  type  = "String"
  value = var.redis_port

  tags = {
    Name        = "${var.project_name}-redis-port"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "redis_password" {
  name  = "${local.prefix}/REDIS_PASSWORD"
  type  = "SecureString"
  value = var.redis_password

  tags = {
    Name        = "${var.project_name}-redis-password"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "jwt_secret" {
  name  = "${local.prefix}/JWT_SECRET"
  type  = "SecureString"
  value = var.jwt_secret

  tags = {
    Name        = "${var.project_name}-jwt-secret"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "ai_api_key" {
  name  = "${local.prefix}/AI_API_KEY"
  type  = "SecureString"
  value = var.ai_api_key

  tags = {
    Name        = "${var.project_name}-ai-api-key"
    Environment = var.environment
  }
}
