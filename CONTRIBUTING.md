# Contributing to odcs-java-sdk

Thank you for considering contributing to `odcs-java-sdk`! We welcome bug fixes, feature additions, and documentation improvements.

---

## Coding Guidelines
To maintain quality across submodules (odcs-core, odcs-spark, etc.), please adhere to the following standards:
- **Code Style**: We follow the Google Java Style Guide. Ensure your IDE is configured accordingly.
- **Unit Tests**: Every bug fix or new feature must include corresponding JUnit 5 test cases.
- **No Breaking Changes**: Ensure changes to core public APIs in odcs-core remain backward-compatible unless explicitly discussed in an issue first.
- **Minimal Dependencies**: Avoid introducing heavy third-party dependencies to odcs-core unless strictly necessary.

---

## Prerequisites

Before building locally, ensure you have the following installed:
* **Java Development Kit (JDK):** Version 17 or higher
* **Apache Maven:** Version 3.8 or higher
* **Git**

---
## Development Workflow
### 1. Fork and Clone
Fork the repository on GitHub to your personal account, then clone your fork locally:

```bash
git clone https://github.com/**YOUR_USERNAME**/odcs-java-sdk.git
cd odcs-java-sdk
```

### 2. Create a Branch
Create a new branch off main for your feature or bug fix:
```bash
git checkout -b feature/your-feature-name
```
or
```bash
git checkout -b fix/your-fix-name
```

### 3. Make Changes and Verify
Make your code edits and add corresponding JUnit tests. Run a full build from the root directory to verify all tests pass:
```bash
mvn clean verify
```

### 4. Commit and Push
Commit your changes with a clear commit message and push the branch to your fork:
```bash
git add .
git commit -m "added support for custom assertion parsing"
git push origin feature/your-feature-name
```

### 5. Raise a Pull Request
Navigate to data-spec-labs/odcs-java-sdk on GitHub and open a Pull Request (PR) from your branch into main. Include a brief summary of what changed and reference any related issues.
