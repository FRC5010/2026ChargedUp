// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.DegreesPerSecondPerSecond;
import static org.wpilib.units.Units.Inches;
import static org.wpilib.units.Units.Kilograms;
import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.RotationsPerSecond;

import org.wpilib.framework.RobotBase;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularAcceleration;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.LinearVelocity;
import org.wpilib.units.measure.Mass;

import com.ctre.phoenix6.signals.InvertedValue;


public final class Constants {
  public enum Mode { REAL, SIM, REPLAY }

  /** Change kSimMode to Mode.REPLAY to re-run a log file instead of simulating fresh. */
  public static final Mode kSimMode = Mode.SIM;
  public static final Mode kCurrentMode = RobotBase.isReal() ? Mode.REAL : kSimMode;

     public static final class WristConstants {
    public static final int kPivotMotorPort = 1; // CAN ID — change to yours

    // Mechanism geometry. estimateMOI treats the arm as a uniform rod, so its
    // length and mass are all the physics model needs.
    public static final double kGearRatio = 50.0; // rotor : arm
    public static final Distance kArmLength = Inches.of(20);
    public static final Mass kArmMass = Kilograms.of(3.0);
    
    
    // How far it can swing. Zero is horizontal, and that is not a free choice.
    public static final Angle kMinAngle = Degrees.of(-20);
    public static final Angle kMaxAngle = Degrees.of(180);

    public static final AngularVelocity kMaxVelocity = DegreesPerSecond.of(180);
    public static final AngularAcceleration kMaxAcceleration =
        DegreesPerSecondPerSecond.of(360);

    // The model: what this arm costs to hold, to move, and to speed up.
    public static final double kArmKG = 0.25; // volts to hold it out horizontal
    public static final double kArmKV = 6.0; // volts per arm rotation/sec
    public static final double kArmKA = 0.055; // volts per arm rotation/sec^2
    // The trim: whatever the model got wrong.
    public static final double kArmKP = 60.0; // volts per rotation of error

    // Where the operator actually wants it
    public static final Angle kIntake = Degrees.of(-20);
    public static final Angle kStowed = Degrees.of(90);

    public static final Angle kTolerance = Degrees.of(2);
  }


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
