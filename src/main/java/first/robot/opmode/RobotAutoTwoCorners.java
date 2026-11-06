// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot.opmode;

import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.PeriodicOpMode;

import first.robot.Robot;
import first.robot.commands.Autos;

@Autonomous(name = "Two Corners", group = "Group 1")
public class RobotAutoTwoCorners extends PeriodicOpMode {
  private final Robot robot;

  /** The Robot instance is passed into the opmode via the constructor. */
  public RobotAutoTwoCorners(Robot robot) {
    this.robot = robot;

    // Selecting this opmode builds the path; enabling just runs it.
    RobotModeTriggers.autonomous()
        .onTrue(Autos.followPath(robot.drivetrain, robot.localizer, "TwoCorners"));
  }

  @Override
  public void periodic() {}
}
