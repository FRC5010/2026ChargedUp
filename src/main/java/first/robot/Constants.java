// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.signals.InvertedValue;

import org.wpilib.framework.RobotBase;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.LinearVelocity;

public final class Constants {
  public enum Mode { REAL, SIM, REPLAY }

  /** Change kSimMode to Mode.REPLAY to re-run a log file instead of simulating fresh. */
  public static final Mode kSimMode = Mode.SIM;
  public static final Mode kCurrentMode = RobotBase.isReal() ? Mode.REAL : kSimMode;

  // One CAN ID + one magnet offset per corner, all named, instead of
  // literals baked into the array in Drivetrain.
  public static final class DriveConstants {
    public static final int kFrontLeftDrivePort = 1;     // CAN IDs — change to yours
    public static final int kFrontLeftSteerPort = 2;
    public static final int kFrontLeftCancoderPort = 9;
    public static final int kFrontRightDrivePort = 3;
    public static final int kFrontRightSteerPort = 4;
    public static final int kFrontRightCancoderPort = 10;
    public static final int kBackLeftDrivePort = 5;
    public static final int kBackLeftSteerPort = 6;
    public static final int kBackLeftCancoderPort = 11;
    public static final int kBackRightDrivePort = 7;
    public static final int kBackRightSteerPort = 8;
    public static final int kBackRightCancoderPort = 12;
    public static final int kGyroPort = 0;               // CAN ID — change to yours

    // Magnet offsets (rotations) — measure with Phoenix Tuner X, change to yours.
    public static final double kFrontLeftMagnetOffset = 0.0;
    public static final double kFrontRightMagnetOffset = 0.0;
    public static final double kBackLeftMagnetOffset = 0.0;
    public static final double kBackRightMagnetOffset = 0.0;

    public static final double kDriveGearRatio = 6.75;                 // rotor : wheel
    public static final double kWheelDiameterMeters = 0.1016;          // 4 inch wheel
    public static final double kWheelCircumferenceMeters =
        Math.PI * kWheelDiameterMeters;                                // ≈ 0.319 m

    public static final double kHalfLength = 0.3;  // meters, wheelbase / 2
    public static final double kHalfWidth  = 0.3;  // meters, track width / 2

    // +X is forward, +Y is left, yaw is CCW-positive.
    public static final Translation2d kFrontLeft  = new Translation2d( kHalfLength,  kHalfWidth);
    public static final Translation2d kFrontRight = new Translation2d( kHalfLength, -kHalfWidth);
    public static final Translation2d kBackLeft   = new Translation2d(-kHalfLength,  kHalfWidth);
    public static final Translation2d kBackRight  = new Translation2d(-kHalfLength, -kHalfWidth);

    // Kraken X60 free speed ≈ 6000 RPM = 100 rotations/sec. Divide by the gear
    // ratio, multiply by circumference → meters/sec. About 4.7 m/s for our numbers.
    public static final LinearVelocity kMaxSpeed =
        MetersPerSecond.of(100.0 / kDriveGearRatio * kWheelCircumferenceMeters);

    // How fast the chassis may spin at full stick — one full rotation per second.
    public static final AngularVelocity kMaxAngularSpeed = RotationsPerSecond.of(1.0);

    public static final double kDriveKV = 0.8;          // volts per wheel rotation/sec — the model
    public static final double kDriveKP = 0.1;          // volts per rps of error — the trim
  }

  public static final class SteerConstants {
    public static final double kSteerGearRatio = 25.0;  // rotor : CANcoder
    public static final double kSteerKP = 40.0;         // volts per rotation of error — tune
    public static final InvertedValue kSteerInverted = InvertedValue.CounterClockwise_Positive; // flip if your steering counts backward
  }

  public static final class HeadingConstants {
    public static final double kP = 0.02; // turn power per degree of heading error
  }

  /**
   * ====== NEXT LESSON: ADD CODE HERE ======
   * Add a VisionConstants class: this season's field layout — where every AprilTag sits
   * — loaded once; each camera's name, which must match the name set in that camera's
   * web UI, and its mount position as a Transform3d from robot center to lens; and the
   * simulated camera's field of view and range.
   */
}
  public static class ElevatorConstants {
    public static final int kMotorPort = 20; // CAN ID — change to yours

    // Mechanism geometry. The drum is what the belt wraps around, so one drum
    // rotation raises the carriage by its circumference.
    public static final double kGearRatio = 12.0; // rotor : drum
    public static final Distance kDrumRadius = Inches.of(1.0);
    public static final double kDrumCircumferenceMeters =
        2 * Math.PI * kDrumRadius.in(Meters);
    public static final Mass kCarriageMass = Kilograms.of(5.0);

    // How far it can physically travel. Ask for more and the goal gets clamped.
    public static final Distance kMinHeight = Meters.of(0.0);
    public static final Distance kMaxHeight = Meters.of(1.5);

    // Motion Magic: not where to go, but how fast it may get there.
    public static final LinearVelocity kMaxVelocity = MetersPerSecond.of(1.0);
    public static final LinearAcceleration kMaxAcceleration =
        MetersPerSecondPerSecond.of(2.0);

    // The model: what this mechanism costs to hold, to move, and to speed up.
    public static final double kElevatorKG = 0.18; // volts just to hold station
    public static final double kElevatorKV = 1.44; // volts per drum rotation/sec
    public static final double kElevatorKA = 0.003; // volts per drum rotation/sec²
    // The trim: whatever the model got wrong.
    public static final double kElevatorKP = 20.0; // volts per rotation of error

    // Where the driver actually wants it.
    public static final Distance kStowed = Meters.of(0.02);
    public static final Distance kScoreMid = Meters.of(0.75);
    public static final Distance kScoreHigh = Meters.of(1.45);

    public static final Distance kTolerance = Centimeters.of(2);
  }
    // Physical facts a physics engine needs that pure motor math never did.
  public static final Mass kRobotMass = Kilograms.of(45);          // with battery and bumpers
  public static final Distance kBumperLength = Inches.of(30);      // front-to-back, over bumpers
  public static final Distance kBumperWidth  = Inches.of(30);      // side-to-side, over bumpers
  public static final Distance kWheelRadius  = Meters.of(kWheelDiameterMeters / 2);

  /** Where the simulated robot is placed on the field at startup. */
  public static final Pose2d kSimStartingPose = new Pose2d(3, 3, new Rotation2d());
  import static edu.wpi.first.units.Units.KilogramSquareMeters;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Volts;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;
import org.ironmaple.simulation.drivesims.configs.SwerveModuleSimulationConfig;

import edu.wpi.first.math.system.plant.DCMotor;
import frc.robot.Constants.SteerConstants;