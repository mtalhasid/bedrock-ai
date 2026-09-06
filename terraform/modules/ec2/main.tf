terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

resource "aws_key_pair" "ansible" {
  key_name   = "${var.project_name}-ansible-key"
  public_key = var.public_key
}

resource "aws_instance" "app_1" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = var.public_subnet_1_id
  key_name                    = aws_key_pair.ansible.key_name
  vpc_security_group_ids      = [var.ec2_sg_id]
  iam_instance_profile        = var.instance_profile_name

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name        = "${var.project_name}-ec2-1"
    Environment = var.environment
  }
}

resource "aws_instance" "app_2" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = var.public_subnet_2_id
  key_name                    = aws_key_pair.ansible.key_name
  vpc_security_group_ids      = [var.ec2_sg_id]
  iam_instance_profile        = var.instance_profile_name
  
  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name        = "${var.project_name}-ec2-2"
    Environment = var.environment
  }
}

resource "aws_lb_target_group_attachment" "app_1" {
  target_group_arn = var.target_group_arn
  target_id        = aws_instance.app_1.id
  port             = 8080
}

resource "aws_lb_target_group_attachment" "app_2" {
  target_group_arn = var.target_group_arn
  target_id        = aws_instance.app_2.id
  port             = 8080
}
