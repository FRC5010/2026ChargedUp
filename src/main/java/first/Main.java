// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first;

import org.wpilib.framework.RobotBase;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.LinearAcceleration;
import org.wpilib.units.measure.LinearVelocity;
import org.wpilib.units.measure.Mass;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what
 * you are doing, do not modify this file except to change the parameter class to the startRobot
 * call.
 */
public final class Main {
  private Main() {}

  /**
   * Main initialization function. Do not perform any initialization here.
   *
   * <p>If you change your main robot class, change the parameter type.
   */
  public static void main(String... args) {
    RobotBase.startRobot(first.robot.Robot::new);
  }



  //  just a copied code from lesson 18 that might help.   public static class ElevatorConstants {
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
}
