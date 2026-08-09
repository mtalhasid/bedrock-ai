output "vpc_id" {
  value = module.vpc.vpc_id
}
output "public_subnet_1_id" {
  value = module.vpc.public_subnet_1_id
}

output "public_subnet_2_id" {
  value = module.vpc.public_subnet_2_id
}

output "alb_sg_id" {
  value = module.security_groups.alb_sg_id
}

output "ec2_sg_id" {
  value = module.security_groups.ec2_sg_id
}

output "ec2_instance_profile_name" {
  value = module.iam.ec2_instance_profile_name
}
