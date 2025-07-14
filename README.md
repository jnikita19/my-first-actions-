# 🚀 GitHub Actions CI/CD Wrapper Workflows

Welcome! This repository provides **wrapper workflows** that trigger standardized CI and CD reusable workflows hosted in [ci-templates](https://github.com/AmanjotSinghSaini-int/ci-templates). These wrappers make it easy for your teams to implement secure, consistent CI/CD pipelines with minimal setup.

---

## 📦 What’s Inside

- `ci-wrapper.yml` — triggers Docker Build + Security Scans (CI)
- `cd-wrapper.yml` — triggers deployment to EKS using kubectl (CD)

---

## 🛠️ Prerequisites

Before using the workflows, ensure the following:

1. ✅ GitHub Secrets:
   - `AWS_ACCESS_KEY_ID` – for ECR/EKS access
   - `AWS_SECRET_ACCESS_KEY`
   - `SLACK_WEBHOOK_URL` – for Slack notifications
   - `SONAR_TOKEN` – for SonarQube scanning (optional but recommended)
   - `ECR_REPO_URL` – your ECR repo (e.g., `1234567890.dkr.ecr.ap-south-1.amazonaws.com/my-app`)

2. ✅ Your repo must have:
   - A `Dockerfile` in the root or specified path
   - Kubernetes manifests (deployment/service/ingress) in a `k8s/` directory (for CD)
   - SonarQube project setup (if enabled)
   - Python/Node/Java/etc. dependencies declared (e.g., `requirements.txt`, `package.json`, `pom.xml`)

---

# Inputs

## CI Wrapper Inputs

| Input Name                  | Required | Type     | Default         | Description                                                                 |
|-----------------------------|----------|----------|-----------------|-----------------------------------------------------------------------------|
| image_name                  | Yes      | string   | -               | Full image name (e.g., `ghcr.io/my-org/my-app`)                            |
| image_tag                   | Yes      | string   | -               | Tag for the built image (e.g., `latest`, `v1.0.0`)                          |
| dockerfile_path             | No       | string   | ./Dockerfile    | Path to the Dockerfile                                                     |
| context                     | No       | string   | .               | Docker build context directory                                              |
| build_args                  | No       | string   | --no-cache      | Extra arguments to pass to Docker build                                    |

| run_gitleaks                | No       | boolean  | true            | Enable Gitleaks scan                                                       |
| gitleaks_fetch_depth        | No       | number   | 1               | Git fetch depth for Gitleaks (**0 = all commits**, **1 = most recent**)   |
| continue_on_gitleaks_error | No       | boolean  | false           | Whether to continue pipeline if Gitleaks scan fails                        |

| run_owasp                   | No       | boolean  | true            | Enable OWASP dependency check                                              |
| continue_on_owasp_error     | No       | boolean  | false           | Whether to continue pipeline if OWASP scan fails                           |

| run_pytest                  | No       | boolean  | true            | Run tests using Pytest                                                     |
| continue_on_pytest_error    | No       | boolean  | true            | Whether to continue pipeline if tests fail                                 |

| run_trivy                   | No       | boolean  | true            | Enable Trivy scan for vulnerabilities                                      |
| continue_on_trivy_error     | No       | boolean  | false           | Whether to continue pipeline if Trivy scan fails                           |

| image_size_threshold_mb     | No       | number   | 200             | Maximum allowed image size in MB                                           |
| continue_on_size_check_error| No       | boolean  | false           | Whether to continue pipeline if image exceeds size                         |

| push_to_ecr                 | No       | boolean  | true            | Whether to push the image to AWS ECR                                       |
| ecr_repository              | Yes*     | string   | -               | ECR repository name (required if `push_to_ecr` is true)                    |

| run_sonar                   | No       | boolean  | true            | Whether to run SonarQube static analysis                                   |
| sonar_host_url              | Yes*     | string   | -               | SonarQube server URL (required if `run_sonar` is true)                     |
| sonar_project_key           | Yes*     | string   | -               | SonarQube project key                                                      |
| sonar_project_name          | Yes*     | string   | -               | SonarQube project name                                                     |

| slack-enabled               | No       | boolean  | true            | Whether to send Slack notifications                                        |
| slack-channel               | No       | string   | "#github-actions-notification" | Slack channel to send notifications to                           |

## Table 2: CD Wrapper Inputs

| Input Name       | Required | Type     | Default                         | Description                                                                  |
|------------------|----------|----------|----------------------------------|------------------------------------------------------------------------------|
| image_tag        | Yes      | string   | -                                | Docker image tag to deploy to the Kubernetes cluster                         |
| k8s_namespace    | No       | string   | "default"                        | Kubernetes namespace to deploy into                                          |
| deployment_file  | No       | string   | "k8s/deployment.template.yaml"   | Path to deployment manifest template                                         |
| slack_notify     | No       | boolean  | true                             | Enables Slack notification after successful deployment                       |
| validate_deploy  | No       | boolean  | true                             | Runs post-deployment validation to ensure pods are running correctly         |

## Table 3: Required GitHub Secrets

| Secret Name           | Required | Description                                                          |
|------------------------|----------|----------------------------------------------------------------------|
| AWS_ACCESS_KEY_ID      | Yes      | AWS key for ECR and Kubernetes access                                |
| AWS_SECRET_ACCESS_KEY  | Yes      | AWS secret for ECR and Kubernetes access                             |
| ECR_REPO_URL           | Yes      | Full ECR repo URL where Docker images are pushed or pulled from      |
| SLACK_WEBHOOK_URL      | No       | Slack webhook for pipeline alerts                                    |
| SONAR_TOKEN            | No       | Token for SonarQube static code analysis (if `sonar_enabled` is true)|


---

## 🚀 Usage

### ✅ CI Wrapper

```yaml
# .github/workflows/ci-wrapper.yml

name: Docker CI Pipeline

on:
  push:
    branches: [main]
  workflow_dispatch:
    inputs:
      trigger_cd:
        description: 'Do you want to run CD after CI?'
        required: false
        default: 'false'
  schedule:
    - cron: '20 8 * * *' # daily CI run

jobs:
  build-and-scan:
    uses: AmanjotSinghSaini-int/ci-templates/.github/workflows/docker-build.yml@main
    with:
      sonar_enabled: true
      push_to_ecr: true
    secrets:
      AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
      AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
      ECR_REPO_URL: ${{ secrets.ECR_REPO_URL }}
