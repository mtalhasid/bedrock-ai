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

output "ecr_repository_url" {
  value = module.ecr.repository_url
}

output "instance_1_public_ip" {
  value = module.ec2.instance_1_public_ip
}

output "instance_2_public_ip" {
  value = module.ec2.instance_2_public_ip
}

output "alb_dns_name" {
  value       = module.alb.alb_dns_name
  description = "DNS name of the Application Load Balancer — use this to reach your app"
}