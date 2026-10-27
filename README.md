# Circuit Overload — FTC Robot Code

Team repository for **Circuit Overload** (*FIRST* Tech Challenge). Built on the public **FTC SDK** for the **BIOBUZZ (2026–2027)** season.

**This is the `TeleOp` branch** — drive, intake, and servo TeleOp plus flywheel tuning utilities.

---

## Table of contents

1. [About this repository](#about-this-repository)
2. [Branches](#branches)
3. [Repository layout](#repository-layout)
4. [Team code (TeleOp branch)](#team-code-teleop-branch)
5. [TeleOp controls (`ProtoTest1`)](#teleop-controls-prototest1)
6. [Setup and requirements](#setup-and-requirements)
7. [Getting help](#getting-help)
8. [FTC SDK samples](#ftc-sdk-samples)
9. [Release information](#release-information)
10. [Upstream SDK changelog](#upstream-sdk-changelog)

---

## About this repository

This project builds the Android app that runs on the REV Control Hub and Driver Station. It includes:

- The standard **FtcRobotController** and **TeamCode** Gradle modules from the FTC SDK
- Team TeleOp and subsystem code under `TeamCode` (and optional root-level experiments on some branches)

For official FTC onboarding (Blocks, OnBot Java, Android Studio), start with the [FTC Blocks Tutorial](https://ftc-docs.firstinspires.org/programming_resources/blocks/Blocks-Tutorial.html), then move to [OnBot Java](https://ftc-docs.firstinspires.org/programming_resources/onbot_java/OnBot-Java-Tutorial.html) or [Android Studio](https://ftc-docs.firstinspires.org/programming_resources/android_studio_java/Android-Studio-Tutorial.html).

---

## Branches

| Branch | Purpose |
|--------|---------|
| **`TeleOp`** (this branch) | Primary TeleOp `ProtoTest1.java`, mecanum drive, intake/servos, flywheel tuners |
| **`Autonomous`** | Autonomous vision starters + same TeleOp baseline; reorganized README |
| **`master`** | Full match TeleOp snapshot — **`Proto1code.java FF`** (Limelight, shooter, intake, servos) |
| **`old-code`** | Archive of merged experiments and older OpMode copies |

Clone this branch:

```bash
git clone -b TeleOp https://github.com/Circuit-Overload/FTC-CircuitOverload.git
```

For competition TeleOp with shooter and Limelight auto-aim, use **`master`** and deploy **`Proto1code.java FF`** (or copy it into `TeamCode` with a unique class name).

---

## Repository layout

| Path | Description |
|------|-------------|
| `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/ProtoTest1.java` | **Main TeleOp** on this branch (`TeleOp_Main`) |
| `TeamCode/.../FlywheelAdvanced/` | Flywheel helper + kV / kS / kP tuner OpModes |
| `TeamCode/.../rohanCode` | Additional team code (see file on branch) |
| `FtcRobotController/` | FTC SDK robot controller app and **sample OpModes** |
| `AprilTagDetection.java`, `BlobDetection.java` | Vision pipeline starters (repo root) |
| `RobotCameraOpMode.java` | Autonomous vision + drive starter |
| `doc/FTC-SDK-RELEASE-NOTES.md` | Full upstream FTC SDK version history |

Sample naming conventions: [TeamCode readme](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/readme.md).

---

## Team code (TeleOp branch)

### Primary TeleOp

| Item | Value |
|------|--------|
| File | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/ProtoTest1.java` |
| Driver Station name | `TeleOp_Main` |
| Drive | Mecanum — left stick translate, right stick turn |
| Deadzone | 0.05 on left/right stick axes |
| Speed | 75% base + up to 25% from **right trigger** |

This branch does **not** include shooter or Limelight logic in `ProtoTest1`. Those live on **`master`** in `Proto1code.java FF`.

### Flywheel / shooter development

| File | Role |
|------|------|
| `FlywheelAdvanced/Flywheel.java` | Maps `motor_flywheel` (28 encoder counts per revolution) |
| `FlywheelAdvanced/kVTuner.java` | Feedforward **kV** tuning |
| `FlywheelAdvanced/kSTuner.java` | **kS** tuning |
| `FlywheelAdvanced/kPTuner.java` | **kP** tuning |

Connect the flywheel **encoder cable** to the hub before using velocity tuning or closed-loop tests.

### Vision (starters)

| File | Role |
|------|------|
| `RobotCameraOpMode.java` | OpMode **Basic Vision OpMode Starter** |
| `AprilTagDetection.java` | AprilTag pipeline starter |
| `BlobDetection.java` | Color blob pipeline starter |

---

## TeleOp controls (`ProtoTest1`)

| Control | Action |
|---------|--------|
| Left stick | Drive and strafe |
| Right stick | Turn |
| Right trigger | Extra drive speed (on top of 75% base) |
| **A** | Toggle intake motor |
| **B** | Toggle intake servos (`servo_intake_left`, `servo_intake_right`) |

### Known issues (TeleOp branch)

- **Intake power:** `ProtoTest1` uses `motor_intake.setPower(intakeOn ? -100.0 : 0.0)`. Valid range is **-1.0 to 1.0** — use `-1.0` (or your chosen power) instead of `-100.0`.
- **Stick drift:** If the robot veils left/right when pushing forward only, increase stick deadzone (e.g. from `0.05` to `0.1`) or add drive motor telemetry to debug.

---

## Setup and requirements

| Task | Requirement |
|------|-------------|
| Android Studio | **Narwhal 3 Feature Drop** or later (for full Gradle sync) |
| Blocks / OnBot Java only | Android Studio **not** required |
| Import project | Android Studio → *Import project* → select repo root |

Upstream SDK releases: [FtcRobotController releases](https://github.com/FIRST-Tech-Challenge/FtcRobotController/releases).

---

## Getting help

| Resource | Link |
|----------|------|
| FTC documentation | [ftc-docs.firstinspires.org](https://ftc-docs.firstinspires.org/index.html) |
| SDK Javadoc | [javadoc.io/doc/org.firstinspires.ftc](https://javadoc.io/doc/org.firstinspires.ftc) |
| FTC Community | [ftc-community.firstinspires.org](https://ftc-community.firstinspires.org/) |

---

## FTC SDK samples

Sample OpModes ship with the SDK:

[FtcRobotController/.../external/samples](FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples)

Copy samples into `TeamCode` and rename the class and `@TeleOp` / `@Autonomous` name so they do not conflict with existing OpModes.

---

## Release information

### TeleOp branch (2026-10-26)

#### Breaking changes

- None. FTC SDK layout is unchanged; team code lives under `TeamCode`.

#### Enhancements

- `ProtoTest1` (`TeleOp_Main`): mecanum drive, right-trigger speed boost, intake toggle (A), dual intake servos (B)
- `FlywheelAdvanced` OpModes for flywheel gain tuning
- Root-level AprilTag / blob vision starters and `RobotCameraOpMode` autonomous starter

#### Known issues

- Intake power `-100.0` in `ProtoTest1` (should be `-1.0` max magnitude)
- Shooter / Limelight TeleOp not on this branch — use `master` → `Proto1code.java FF` for that configuration

### FTC SDK baseline

This repo tracks **FTC SDK 12.0** (20260907-090034) for BIOBUZZ. Notable upstream change: AprilTag **clusters** vs singletons — legacy OpModes must cast detections correctly. See [AprilTag clusters doc](https://ftc-docs.firstinspires.org/apriltag-clusters).

---

## Upstream SDK changelog

The full version-by-version FTC SDK release notes (Versions 12.0 back through early releases) are kept in:

**[doc/FTC-SDK-RELEASE-NOTES.md](doc/FTC-SDK-RELEASE-NOTES.md)**

That file is the long-form changelog from the official SDK README; this README stays focused on team usage and branch-specific notes.
