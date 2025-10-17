# CI/CD Pipeline Documentation

This document describes the Continuous Integration and Continuous Deployment (CI/CD) pipeline for the Kubernetes Microservice Deployment project.

## Pipeline Overview

The CI/CD pipeline is built using GitHub Actions and includes the following stages:

### 1. Code Quality & Testing
- **Unit Tests**: Runs all unit tests with PostgreSQL test database
- **Test Coverage**: Generates JaCoCo coverage reports (minimum 80% required)
- **Test Reporting**: Publishes test results to GitHub
- **Code Coverage Upload**: Uploads coverage to Codecov

### 2. Security Scanning
- **Dependency Vulnerability Scan**: OWASP Dependency Check
- **Source Code Security**: Trivy filesystem scanning
- **SARIF Upload**: Security results uploaded to GitHub Security tab

### 3. Build & Package
- **Application Build**: Gradle build with Java 21
- **Docker Image**: Multi-platform image (amd64/arm64)
- **Container Registry**: Pushes to GitHub Container Registry (ghcr.io)
- **Artifact Upload**: JAR files uploaded as build artifacts

### 4. Container Security
- **Image Scanning**: Trivy vulnerability scanning on built Docker images
- **Security Reports**: Results uploaded to GitHub Security tab

### 5. Deployment (Optional)
- **Staging Deployment**: Automatic deployment to staging on main branch
- **Production Deployment**: Manual approval required

## Getting Started

### Prerequisites

1. **GitHub Repository Secrets**:
   ```
   GITHUB_TOKEN (automatically provided)
   SONAR_TOKEN (for SonarCloud integration)
   SLACK_WEBHOOK_URL (for notifications, optional)
   ```

2. **SonarCloud Setup**:
   - Create account at https://sonarcloud.io
   - Import your GitHub repository
   - Get your organization key and project key
   - Add SONAR_TOKEN to repository secrets

3. **Codecov Setup**:
   - Create account at https://codecov.io
   - Connect your GitHub repository

### Local Development

1. **Run Tests Locally**:
   ```bash
   cd microservice-a
   ./gradlew clean test
   ```

2. **Generate Coverage Report**:
   ```bash
   ./gradlew jacocoTestReport
   ```

3. **Run Security Scan**:
   ```bash
   ./gradlew dependencyCheckAnalyze
   ```

4. **Build Docker Image**:
   ```bash
   ./gradlew dockerBuild
   ```

## Pipeline Triggers

The pipeline runs on:
- **Push** to `main` or `develop` branches
- **Pull Requests** to `main` branch
- **Manual trigger** via GitHub Actions UI

## Quality Gates

### Test Coverage
- Minimum 80% line coverage required
- Coverage reports exclude:
  - Application main class
  - Configuration classes
  - DTOs and Entities

### Security
- OWASP Dependency Check fails build on CVSS >= 7.0
- Trivy scans for vulnerabilities in dependencies and container images

### Code Quality
- SonarCloud quality gate must pass
- No critical security hotspots allowed

## Docker Images

Images are tagged with:
- `latest` (main branch only)
- `{branch-name}` (for feature branches)
- `{branch-name}-{commit-sha}` (unique identifier)

Example: `ghcr.io/pranjaysingharch/kubernetes-microservice-deployment/microservice-a:main-abc1234`

## Deployment Environments

### Staging
- **Trigger**: Automatic on main branch
- **URL**: Configure in deployment scripts
- **Database**: Staging PostgreSQL instance

### Production
- **Trigger**: Manual approval required
- **URL**: Configure in deployment scripts
- **Database**: Production PostgreSQL instance

## Monitoring & Notifications

### Slack Notifications
Configure `SLACK_WEBHOOK_URL` secret for pipeline notifications including:
- Build status (success/failure)
- Deployment notifications
- Security alerts

### GitHub Integration
- Test results appear in PR checks
- Security findings in Security tab
- Coverage reports in PR comments (via Codecov)

## Troubleshooting

### Common Issues

1. **Test Failures**:
   - Check test logs in GitHub Actions
   - Verify PostgreSQL connection in tests
   - Ensure test data is properly set up

2. **Coverage Below Threshold**:
   - Add more unit tests
   - Remove exclusions if appropriate
   - Update coverage threshold if needed

3. **Security Vulnerabilities**:
   - Update dependencies to latest versions
   - Add suppressions to `owasp-suppressions.xml` for false positives
   - Review and fix legitimate security issues

4. **Docker Build Failures**:
   - Check Dockerfile syntax
   - Verify base image availability
   - Ensure JAR file is built correctly

### Logs and Debugging

1. **View Pipeline Logs**:
   - Go to Actions tab in GitHub
   - Select the failed workflow
   - Click on the failed job

2. **Local Debugging**:
   ```bash
   # Run the same commands locally
   cd microservice-a
   ./gradlew clean test --info --stacktrace
   ```

## Configuration Files

### GitHub Actions
- `.github/workflows/ci.yml` - Main CI pipeline

### Code Quality
- `sonar-project.properties` - SonarCloud configuration
- `owasp-suppressions.xml` - Security scan suppressions

### Build Configuration
- `build.gradle` - Gradle build with plugins:
  - `jacoco` - Code coverage
  - `org.sonarqube` - Code quality
  - `org.owasp.dependencycheck` - Security scanning

## Best Practices

1. **Branch Protection**:
   - Require PR reviews
   - Require status checks to pass
   - Require branches to be up to date

2. **Security**:
   - Keep dependencies updated
   - Review security scan results
   - Use least privilege for deployment credentials

3. **Testing**:
   - Write comprehensive unit tests
   - Use test containers for integration tests
   - Maintain high code coverage

4. **Documentation**:
   - Update this README for pipeline changes
   - Document any new quality gates
   - Keep deployment instructions current

## Support

For pipeline issues:
1. Check this documentation
2. Review GitHub Actions logs
3. Contact the DevOps team
4. Create an issue in the repository