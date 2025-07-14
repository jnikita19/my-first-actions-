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

## Table 1: CI Wrapper Inputs

| Input Name       | Required | Type     | Default       | Description                                                                  |
|------------------|----------|----------|---------------|------------------------------------------------------------------------------|
| trigger_cd       | No       | string   | "false"       | Controls whether to trigger CD pipeline after CI ("true" or "false")        |
| sonar_enabled    | No       | boolean  | true          | Whether to run SonarQube code analysis                                       |
| push_to_ecr      | No       | boolean  | true          | If true, image will be pushed to ECR after scanning                          |
| slack_notify     | No       | boolean  | true          | Enables Slack notifications if SLACK_WEBHOOK_URL is provided                |
| project_path     | No       | string   | "."           | Path to the Dockerfile and source code                                       |
| dockerfile_name  | No       | string   | "Dockerfile"  | Name of the Dockerfile to use                                                |

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
