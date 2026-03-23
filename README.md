# Sport Timer

[![CI](https://github.com/HabelJonas/SportTimer/actions/workflows/ci.yml/badge.svg)](https://github.com/HabelJonas/SportTimer/actions/workflows/ci.yml)

A Kotlin-based sports timing application focused on accurate time tracking, ease of use and not collecting any data.

## ✨ Features

- Precise sport/activity time measurement
- Clean and maintainable Kotlin codebase
- CI pipeline for automated validation

## 🧱 Tech Stack

- **Language:** Kotlin (100%)
- **Build tooling:** Gradle (expected for Kotlin projects)
- **CI/CD:** GitHub Actions

## 🚀 Getting Started

### Prerequisites

- JDK 17+ (or project-required Java version)
- Gradle (or use the included Gradle Wrapper if available)
- IDE: IntelliJ IDEA / Android Studio (depending on project type)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/HabelJonas/SportTimer.git
   cd SportTimer
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run tests:
   ```bash
   ./gradlew test
   ```

## 🔄 CI Pipeline

This repository uses GitHub Actions for continuous integration.

- **Workflow:** `ci.yml`
- **Status badge:** shown at the top of this README
- **Actions page:**  
  https://github.com/HabelJonas/SportTimer/actions

## 🧪 Quality & Testing

Recommended quality gates in CI:

- Compile/build on every push and pull request
- Unit tests execution

## 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a pull request

## 🗺️ Roadmap (Ideas)

- Add export/import for sessions
- Add UI/UX improvements for live timing
- Add selectable sounds
- Rollout to Google Play Store
- Market the app in forums and sport groups
