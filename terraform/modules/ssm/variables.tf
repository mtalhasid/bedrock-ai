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

variable "ai_api_key" {
  type      = string
  sensitive = true
}
