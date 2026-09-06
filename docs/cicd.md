# CI/CD (GitHub Actions)

Workflow file: `.github/workflows/docker-deploy.yml`. It runs on every push to `main`.

## What the jobs do

The first job runs `AuthControllerTest` and `ChatControllerTest`. If those fail, nothing is published.

If tests pass, GitHub Actions builds the multi-stage Docker image and pushes it to Docker Hub as `bedrock-ai:latest`. The next job pulls that image, retags it, and pushes it to Amazon ECR. The last job installs Ansible on the runner, discovers EC2 instances tagged `Environment=dev`, SSHs in with `ANSIBLE_SSH_PRIVATE_KEY`, and runs the playbook so each host pulls `:latest` from ECR and starts the container on port 8080.

## Secrets the workflow expects

Open the GitHub repo, then Settings, Secrets and variables, Actions, and **Repository secrets**.

| Secret | What it is |
| --- | --- |
| `DOCKER_HUB_USERNAME` | Your Docker Hub username |
| `DOCKER_HUB_SECRET` | A Docker Hub personal access token |
| `AWS_ACCESS_KEY_ID` | Access key for an IAM user the runner uses |
| `AWS_SECRET_ACCESS_KEY` | Secret for that same IAM user. AWS shows it only when you create the key. |
| `ECR_REGISTRY` | The value of `terraform output ecr_repository_url` |
| `ANSIBLE_SSH_PRIVATE_KEY` | The private key file, including the BEGIN and END lines |

If you also stored `GEMINI_API_KEY` in GitHub, the workflow does not read it. Terraform already puts the Gemini key in SSM as `AI_API_KEY`, and Ansible injects it when it starts the container.

## ECR_REGISTRY

This must be a secret, not a variable. A typical value looks like:

`123456789012.dkr.ecr.eu-north-1.amazonaws.com/bedrock-ai-dev`

There should be no `https://` and no `:latest`.

## ANSIBLE_SSH_PRIVATE_KEY

This is the private key that matches the public key you put in Terraform as `public_key`. On Windows you can print it with `Get-Content $env:USERPROFILE\.ssh\bedrock-ai-key`. Paste the entire file into the secret, from BEGIN through END, with no extra quotes.

## Order if AWS is not set up yet

Run `terraform apply` first so ECR and EC2 exist. Add `ECR_REGISTRY` (and any other missing secrets) in GitHub. Then push to `main`.
