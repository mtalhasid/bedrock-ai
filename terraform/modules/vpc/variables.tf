variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
}

variable "public_subnet_1_cidr" {
  description = "CIDR block for public subnet 1"
  type        = string
}

variable "public_subnet_2_cidr" {
  description = "CIDR block for public subnet 2"
  type        = string
}

variable "az_1" {
  description = "First AZ"
  type        = string
}

variable "az_2" {
  description = "Second AZ"
  type        = string
}

variable "environment" {
  description = "Deployment Environment"
  type        = string
}

variable "project_name" {
  description = "Project name for tagging"
  type        = string
}
