variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "db_url" {
  type      = string
  sensitive = true
}

variable "db_user" {
  type      = string
  sensitive = true
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "redis_host" {
  type      = string
  sensitive = true
}

variable "redis_port" {
  type    = string
  default = "6379"
}

variable "redis_password" {
  type      = string
  sensitive = true
}

variable "jwt_secret" {
  type      = string
  sensitive = true
}

variable "ai_api_key" {
  type      = string
  sensitive = true
}
