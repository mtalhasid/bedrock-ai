terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

module "vpc" {
  source               = "./modules/vpc"
  vpc_cidr             = var.vpc_cidr
  public_subnet_1_cidr = var.public_subnet_1_cidr
  public_subnet_2_cidr = var.public_subnet_2_cidr
  az_1                 = var.az_1
  az_2                 = var.az_2
  environment          = var.environment
  project_name         = var.project_name
}

module "security_groups" {
  source       = "./modules/security_groups"
  environment  = var.environment
  project_name = var.project_name
  vpc_id       = module.vpc.vpc_id
}

module "iam" {
  source       = "./modules/iam"
  environment  = var.environment
  project_name = var.project_name
}

module "ecr" {
  source       = "./modules/ecr"
  environment  = var.environment
  project_name = var.project_name
}

module "alb" {
  source             = "./modules/alb"
  project_name       = var.project_name
  environment        = var.environment
  vpc_id             = module.vpc.vpc_id
  public_subnet_1_id = module.vpc.public_subnet_1_id
  public_subnet_2_id = module.vpc.public_subnet_2_id
  alb_sg_id          = module.security_groups.alb_sg_id
}

module "ssm" {
  source         = "./modules/ssm"
  ai_api_key     = var.ai_api_key
  db_url         = var.db_url
  db_user        = var.db_user
  db_password    = var.db_password
  redis_host     = var.redis_host
  redis_port     = var.redis_port
  redis_password = var.redis_password
  jwt_secret     = var.jwt_secret
  environment    = var.environment
  project_name   = var.project_name
}

module "ec2" {
  source                = "./modules/ec2"
  project_name          = var.project_name
  environment           = var.environment
  instance_type         = var.instance_type
  public_subnet_1_id    = module.vpc.public_subnet_1_id
  public_subnet_2_id    = module.vpc.public_subnet_2_id
  ec2_sg_id             = module.security_groups.ec2_sg_id
  instance_profile_name = module.iam.ec2_instance_profile_name
  target_group_arn      = module.alb.target_group_arn
  ecr_repository_url    = module.ecr.repository_url
  ssm_parameter_prefix  = module.ssm.parameter_prefix
  aws_region            = var.aws_region
  log_group_name        = module.cloudwatch.log_group_name
}

module "cloudwatch" {
  source       = "./modules/cloudwatch"
  project_name = var.project_name
  environment  = var.environment
}
