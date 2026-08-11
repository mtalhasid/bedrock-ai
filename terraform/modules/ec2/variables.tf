variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "instance_type" {
  type = string
}

variable "public_subnet_1_id" {
  type = string
}
variable "public_subnet_2_id" {
  type = string
}
variable "ec2_sg_id" {
  type = string
}


variable "instance_profile_name" {
  type = string
}

variable "target_group_arn" {
  type = string
}

variable "ecr_repository_url" {
  type = string
}

variable "ssm_parameter_prefix" {
  type = string
}

variable "aws_region" {
  type = string
}

variable "log_group_name" {
  type        = string
  description = "CloudWatch log group name for Docker awslogs driver"
}
