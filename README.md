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
