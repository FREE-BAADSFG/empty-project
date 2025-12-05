# FrogForce 503 2025 Robot Code

This repository contains the robot code for FrogForce 503's 2025 FRC competition robot. The project is built using WPILib's GradleRIO system and Java 17.

## Key Features
- Command-based robot framework using WPILib
- Custom subsystem architecture based on [`FFSubsystemBase`](src/main/java/org/frogforce503/lib/subsystem/FFSubsystemBase.java)
- Integrated dashboard utilities through [`SBUtil`](src/main/java/org/frogforce503/lib/dashboard/SBUtil.java)
- Advanced logging capabilities using AdvantageKit
- Support for various hardware including:
  - Phoenix 6 motor controllers
  - REV Robotics hardware
  - Custom swerve drive implementation

## Dependencies
- WPILib 2025.3.1
- CTRE Phoenix 6 (25.3.2)
- REVLib (2025.0.3)
- AdvantageKit (4.1.2)
- ChoreoLib (2025.0.3)

## Build & Deploy
Use the standard WPILib Gradle commands to build and deploy:
```bash
./gradlew build
./gradlew deploy