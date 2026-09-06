# Bedrock-ai

Bedrock-ai is a Spring Boot backend that demonstrates backend engineering and cloud deployment on a real LLM chat API. Users can register, log in with JWT, and send messages to Google Gemini. Chat sessions are stored in PostgreSQL, frequent reads are cached in Redis, and Resilience4j retries and a circuit breaker sit in front of Gemini so a bad upstream call does not take the whole service down.

On AWS the API runs in Docker on two EC2 instances behind an Application Load Balancer. The `terraform/` folder defines that layout. A push to `main` runs tests, publishes the image to Docker Hub and ECR, then Ansible deploys the new container and injects SSM secrets. Apply Terraform when infrastructure changes. Push code when the application changes.

## Stack

| Area | Choice |
| --- | --- |
| API | Java 21, Spring Boot 4, Spring Security, JPA |
| Auth | JWT, refresh tokens, optional Google OAuth2 |
| Database | Neon PostgreSQL |
| Cache | Upstash Redis |
| LLM | Google Gemini |
| AWS | VPC, ALB, EC2, ECR, IAM, SSM, CloudWatch |
| IaC and deploy | Terraform, Ansible, GitHub Actions |

## Request path in AWS

Client → ALB (port 80) → EC2 containers (port 8080) → Postgres, Redis, and Gemini. Secrets stay in SSM and are injected when Ansible starts the container.

## Docs

| Doc | Contents |
| --- | --- |
| [docs/local-setup.md](docs/local-setup.md) | Run the API locally |
| [docs/api.md](docs/api.md) | Endpoints |
| [docs/infrastructure.md](docs/infrastructure.md) | Terraform apply and outputs |
| [docs/cicd.md](docs/cicd.md) | GitHub secrets and pipeline jobs |
| [docs/ansible.md](docs/ansible.md) | Deploy playbook |

## First AWS deploy

1. Copy `terraform/terraform.tfvars.example` to `terraform/terraform.tfvars`. Set `public_key` to your SSH public key. Keep the private key at `~/.ssh/bedrock-ai-key`. Fill DB, Redis, JWT, and Gemini values.
2. From `terraform/`, run `terraform init` then `terraform apply`.
3. Put `terraform output ecr_repository_url` in GitHub as repository secret `ECR_REGISTRY` (no `:latest`, not an Actions variable).
4. Confirm the other secrets in [docs/cicd.md](docs/cicd.md), including `ANSIBLE_SSH_PRIVATE_KEY`.
5. Push to `main`.
