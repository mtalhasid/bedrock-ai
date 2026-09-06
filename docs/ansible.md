# Ansible

Playbook: `ansible/deploy.yml`. Role: `ansible/roles/app_deploy`. GitHub Actions runs this at the end of the pipeline. Run it locally only if you are debugging a host.

The inventory file `ansible/inventory/aws_ec2.yml` asks AWS for running instances in `eu-north-1` that have the tag `Environment=dev`. It then SSHs in as `ec2-user` using `~/.ssh/bedrock-ai-key` (in CI that file is written from the `ANSIBLE_SSH_PRIVATE_KEY` secret).

## What the playbook does on each host

It installs Docker and the Python Docker library, starts the Docker service, and adds `ec2-user` to the docker group. It logs into ECR using the instance IAM role, pulls `ecr_repository_url:latest`, and reads SSM parameters under `/bedrock-ai/dev/` for the database, Redis, JWT, and Gemini key. It then removes any old `bedrock-ai` container and starts a new one on port 8080 with those values as environment variables.

In GitHub Actions, `ecr_repository_url` is passed in from the `ECR_REGISTRY` secret so the playbook does not depend on a hardcoded account id.

## Running it on your machine

```
cd ansible
ansible-playbook -i inventory/aws_ec2.yml deploy.yml
```

Your machine needs AWS credentials, the collections `amazon.aws` and `community.docker`, and the private key at `~/.ssh/bedrock-ai-key`.
