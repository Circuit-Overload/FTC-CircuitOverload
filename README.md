# Circuit Overload — FTC Robot Code

Team repository for **Circuit Overload** (*FIRST* Tech Challenge). Built on the public **FTC SDK** for the **BIOBUZZ (2026–2027)** season.

---

## Table of contents

1. [About this repository](#about-this-repository)
2. [Branches](#branches)
3. [Repository layout](#repository-layout)
4. [Team code (Autonomous branch)](#team-code-autonomous-branch)
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
- Team OpModes, utilities, and experiments under `TeamCode` and the repo root (branch-dependent)

For official FTC onboarding (Blocks, OnBot Java, Android Studio), start with the [FTC Blocks Tutorial](https://ftc-docs.firstinspires.org/programming_resources/blocks/Blocks-Tutorial.html), then move to [OnBot Java](https://ftc-docs.firstinspires.org/programming_resources/onbot_java/OnBot-Java-Tutorial.html) or [Android Studio](https://ftc-docs.firstinspires.org/programming_resources/android_studio_java/Android-Studio-Tutorial.html).

---

## Branches

| Branch | Purpose |
|--------|---------|
| **`Autonomous`** | Autonomous + flywheel tuning + vision experiments; primary TeleOp is `ProtoTest1.java` |
| **`TeleOp`** | TeleOp-focused work (see branch for current OpModes) |
| **`master`** | TeleOp / main robot code snapshot (e.g. `Proto1code.java FF` on that branch) |
| **`old-code`** | Archived merge of earlier team branches and experiments |

Clone the branch you need:

```bash
git clone -b Autonomous https://github.com/Circuit-Overload/FTC-CircuitOverload.git
```

---

## Repository layout

| Path | Description |
|------|-------------|
| `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/` | **Primary team Java** (OpModes, subsystems) |
| `TeamCode/.../FlywheelAdvanced/` | Flywheel helper + kV / kS / kP tuner OpModes |
| `FtcRobotController/` | FTC SDK robot controller app and **sample OpModes** |
| `AprilTagDetection.java`, `BlobDetection.java` | Root-level vision pipeline starters (Autonomous branch) |
| `RobotCameraOpMode.java` | Autonomous vision + drive starter OpMode |
| `doc/FTC-SDK-RELEASE-NOTES.md` | Full upstream FTC SDK version history |

More detail on sample naming: [TeamCode readme](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/readme.md).

---

## Team code (Autonomous branch)

### TeleOp

| Item | Value |
|------|--------|
| File | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/ProtoTest1.java` |
| OpMode name on Driver Station | `TeleOp_Main` |
| Drive | Mecanum (left stick strafe/forward, right stick turn) |
| Deadzone | 0.05 on sticks |
| Speed | Base 75% + up to 25% from right trigger |

### Flywheel tuning

| File | Role |
|------|------|
| `FlywheelAdvanced/Flywheel.java` | Maps `motor_flywheel`; 28 counts per motor revolution |
| `FlywheelAdvanced/kVTuner.java` | Feedforward **kV** tuning |
| `FlywheelAdvanced/kSTuner.java` | **kS** tuning |
| `FlywheelAdvanced/kPTuner.java` | **kP** tuning |

Use tuners only after the flywheel encoder cable is connected to the hub.

### Autonomous and vision

| File | Role |
|------|------|
| `RobotCameraOpMode.java` | OpMode **Basic Vision OpMode Starter** — AprilTag + base movement |
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

### Known issues (Autonomous branch)

- **Intake power:** `ProtoTest1` uses `motor_intake.setPower(intakeOn ? -100.0 : 0.0)`. Valid range is **-1.0 to 1.0** — use `-1.0` (or your chosen power) instead of `-100.0`.
- **Flywheel encoder:** Confirm the 4-pin encoder cable is plugged in before relying on velocity tuning or closed-loop flywheel code.

---

## Setup and requirements

| Task | Requirement |
|------|-------------|
| Android Studio | **Narwhal 3 Feature Drop** or later (for full Gradle sync) |
| Blocks / OnBot Java only | Android Studio **not** required |
| Import project | Android Studio → *Import project* → select repo root |

Download options for the upstream SDK (if starting fresh): [FtcRobotController releases](https://github.com/FIRST-Tech-Challenge/FtcRobotController/releases).

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

### Autonomous branch (2026-10-26)

#### Breaking changes

- None. FTC SDK layout is unchanged; team code lives under `TeamCode`.

#### Enhancements

- TeleOp `ProtoTest1` (`TeleOp_Main`): mecanum drive, trigger boost, intake and dual-servo toggles
- `FlywheelAdvanced` tuning OpModes for flywheel gains
- `RobotCameraOpMode` autonomous vision starter
- Root-level AprilTag and blob detection starters

#### Known issues

- Intake power set to `-100.0` in `ProtoTest1` (should be `-1.0` max magnitude)
- Flywheel encoder wiring still being validated on the robot

### FTC SDK baseline

This repo tracks **FTC SDK 12.0** (20260907-090034) for BIOBUZZ. Notable upstream change: AprilTag **clusters** vs singletons — legacy OpModes must cast detections correctly. See [AprilTag clusters doc](https://ftc-docs.firstinspires.org/apriltag-clusters).

---

## Upstream SDK changelog

The full version-by-version FTC SDK release notes (Versions 12.0 back through early releases) are kept in:

**[doc/FTC-SDK-RELEASE-NOTES.md](doc/FTC-SDK-RELEASE-NOTES.md)**

That file is the long-form changelog from the official SDK README; this README stays focused on team usage and branch-specific notes.
