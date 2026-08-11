output "db_url_parameter_name" {
  value = aws_ssm_parameter.db_url.name
}

output "ai_api_key_parameter_name" {
  value = aws_ssm_parameter.ai_api_key.name
}

