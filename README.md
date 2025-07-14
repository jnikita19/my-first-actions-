# 🚀 GitHub Actions CI/CD Wrapper Workflows

Welcome! This repository provides **wrapper workflows** that trigger standardized CI and CD reusable workflows hosted in [ci-templates](https://github.com/AmanjotSinghSaini-int/ci-templates). These wrappers make it easy for your teams to implement secure, consistent CI/CD pipelines with minimal setup.

---

## 📦 What’s Inside

- `ci-wrapper.yml` — triggers Docker Build + Security Scans (CI)
- `cd-wrapper.yml` — triggers deployment to EKS using kubectl (CD)

---

## 🛠️ Prerequisites

Before using the workflows, ensure the following:

## Steps to Add a Secret

1. **Go to your repository** on GitHub (e.g., `https://github.com/your-org/your-repo`)

2. Click on the **"Settings"** tab in the top navigation bar

3. In the left sidebar, scroll down and click **"Secrets and variables" → "Actions"**

4. Click the **"New repository secret"** button

5. Enter the **name** of the secret (e.g., `AWS_ACCESS_KEY_ID`) in the **Name** field

6. Enter the **value** of the secret (e.g., your actual AWS access key) in the **Secret** field

7. Click **"Add secret"**

Repeat steps 4–7 for each of the secrets listed below.

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

## CD Wrapper Inputs

| Input Name                  | Required | Type     | Default                         | Description                                                                 |
|-----------------------------|----------|----------|----------------------------------|-----------------------------------------------------------------------------|
| ecr_repo                    | Yes      | string   | -                                | Full ECR image URL to deploy (e.g., `account.dkr.ecr.region.amazonaws.com/repo-name`) |
| eks_cluster_name            | Yes      | string   | -                                | Name of the EKS cluster to connect and deploy into                         |
| deployment_mode             | No       | string   | "singlefile"                     | Deployment mode: `singlefile`, `filenames`, or `recursive`                 |
| deployment_file             | No*      | string   | ./deployment.template.yaml       | Path to single manifest file (used if `deployment_mode` is `singlefile`)   |
| deployment_files            | No*      | string   | -                                | Comma-separated list of files (used if `deployment_mode` is `filenames`)   |
| continue_on_validation_error| No       | boolean  | false                            | Whether to continue if post-deploy validation fails                         |
| delete_old_images           | No       | boolean  | true                             | Whether to delete older unused ECR images after deployment                 |

> *Only one of `deployment_file` or `deployment_files` is required, depending on the deployment mode.

## Required GitHub Secrets

| Secret Name           | Required | Description                                                       |
|------------------------|----------|-------------------------------------------------------------------|
| SLACK_WEBHOOK_URL      | No       | Slack webhook URL used for sending pipeline notifications         |
| AWS_ACCESS_KEY_ID      | Yes      | AWS access key for authenticating with ECR and EKS                |
| AWS_SECRET_ACCESS_KEY  | Yes      | AWS secret key for ECR and EKS authentication                     |
| AWS_REGION             | Yes      | AWS region (e.g., `ap-south-1`) where your resources are hosted    |
| AWS_ACCOUNT_ID         | Yes      | AWS account ID used in constructing ECR repository URLs           |
| SONAR_TOKEN            | No       | Token used to authenticate with SonarQube for static code analysis|

> **Note:**  
> `SLACK_WEBHOOK_URL` and `SONAR_TOKEN` are optional. They are only required if:
> - `slack-enabled: true` is set in your workflow inputs (for Slack notifications), or  
> - `run_sonar: true` is enabled (for SonarQube static analysis).  
> If these secrets are not provided while the respective features are enabled, the pipeline may fail at runtime.


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
