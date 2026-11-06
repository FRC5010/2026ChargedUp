package first.robot.commands;

import org.wpilib.command3.Command;
import org.wpilib.math.controller.PIDController;

import bline.commands.FollowPath;
import bline.following.DriveType;
import bline.path.Path;
import first.robot.subsystems.Drivetrain;
import first.robot.subsystems.Localizer;

public class BLine {
  private static boolean s_initialized = false;

  private final FollowPath.Builder m_builder;

  public BLine(Drivetrain drivetrain, Localizer localizer) {
    initOnce();

    m_builder = new FollowPath.Builder(
            DriveType.SWERVE,
            drivetrain,
            localizer::getPose,
            localizer::resetPose,
            drivetrain::getRobotRelativeVelocities,
            drivetrain::driveRobotRelative,
            new PIDController(0.0, 0.0, 0.0),
            new PIDController(0.0, 0.0, 0.0),
            new PIDController(0.0, 0.0, 0.0))
        .withDefaultShouldFlip();
  }

  private static void initOnce() {
    if (s_initialized) {
      return;
    }
    s_initialized = true;

    B_LineAutoCommands.configureBlineAutoCommands();
  }

  public Command autoCommand(String pathName) {
    return m_builder.build(new Path(pathName)).withPoseReset();
  }

  public Command pathCommand(String pathName) {
    return m_builder.build(new Path(pathName));
  }
}
