variable "aws_region" {
  description = "Region of AWS"
  type        = string
}

variable "environment" {
  type = string
}

variable "project_name" {
  type = string
}

variable "vpc_cidr" {
  type = string
}
variable "public_subnet_1_cidr" {
  type = string
}

variable "public_subnet_2_cidr" {
  type = string
}

variable "az_1" {
  type = string
}

variable "az_2" {
  type = string
}

variable "db_url" {
  description = "Neon PostgreSQL connection string"
  type        = string
  sensitive   = true
}

variable "ai_api_key" {
  description = "AI service API key"
  type        = string
  sensitive   = true
}

variable "instance_type" {
  type = string
}
