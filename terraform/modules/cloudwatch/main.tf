resource "aws_cloudwatch_log_group" "app" {
  name              = "/${var.project_name}/${var.environment}"
  retention_in_days = 7

  tags = {
    Name        = "${var.project_name}-logs"
    Environment = var.environment
  }
}
