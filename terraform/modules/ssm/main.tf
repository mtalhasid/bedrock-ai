terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}
resource "aws_ssm_parameter" "db_url" {
  name  = "/${var.project_name}/${var.environment}/DB_URL"
  type  = "SecureString"
  value = var.db_url
  tags = {
    Name        = "${var.project_name}-db-url"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "ai_api_key" {
  name  = "/${var.project_name}/${var.environment}/AI_API_KEY"
  type  = "SecureString"
  value = var.ai_api_key

  tags = {
    Name        = "${var.project_name}-ai-api-key"
    Environment = var.environment
  }
}
