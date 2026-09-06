# Infrastructure (Terraform)

The `terraform/` folder creates the AWS resources. It does not install Docker or start the app. That happens in Ansible after a push to `main`.

## What Terraform creates

It creates a VPC with two public subnets in two availability zones, an Application Load Balancer on port 80, and two EC2 instances registered as targets. It also creates an ECR repository for the Docker image, an IAM instance profile so those machines can read ECR and SSM, SSM parameters for database, Redis, JWT, and Gemini values, a CloudWatch log group, and an SSH key pair.

The EC2 security group allows port 8080 only from the ALB. Port 22 is open so Ansible can SSH in from GitHub Actions.

## Before you apply

Copy `terraform/terraform.tfvars.example` to `terraform/terraform.tfvars`. Set `public_key` to your SSH public key. That value is a single line and usually starts with `ssh-rsa` or `ssh-ed25519`. Fill in the database, Redis, JWT, and Gemini fields as well. Terraform writes those into SSM.

Then run:

```
cd terraform
terraform init
terraform apply
```

## Outputs you will actually use

| Output | What you do with it |
| --- | --- |
| `ecr_repository_url` | Paste this into GitHub as the secret `ECR_REGISTRY` |
| `alb_dns_name` | This is the public hostname of the API |
| `instance_1_public_ip` and `instance_2_public_ip` | Useful if you need to SSH in by hand |

```
terraform output ecr_repository_url
terraform output alb_dns_name
```

`ECR_REGISTRY` must be a GitHub **repository secret**. The workflow reads `${{ secrets.ECR_REGISTRY }}`, so an Actions variable will not work. Do not prefix the URL with `https://` and do not append `:latest`.
