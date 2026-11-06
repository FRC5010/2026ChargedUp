package first.robot.commands;

import org.wpilib.command3.Command;
import org.wpilib.math.controller.PIDController;

import bline.commands.FollowPath;
import bline.following.DriveType;
import bline.path.Path;
import first.robot.Constants.PathConstants;
import first.robot.subsystems.Drivetrain;
import first.robot.subsystems.Localizer;

public final class Autos {
  /** How this robot follows a path. One for the whole program — the first followPath sets it. */
  private static FollowPath.Builder s_pathBuilder;

  private Autos() {} // utility class — never instantiated

  /** Drive 1 m, turn to 90°, drive 1 m more. */
  public static Command driveTurnDrive(Drivetrain drivetrain) {
    return Command.noRequirements(coroutine -> {
          coroutine.await(drivetrain.driveDistance(1.0));  // step 1: forward 1 meter
          coroutine.await(drivetrain.turnToHeading(90));   // step 2: face 90°
          coroutine.await(drivetrain.driveDistance(1.0));  // step 3: forward 1 meter
        })
        .named("Drive Turn Drive");
  }

  /** Everything about following a path that doesn't depend on which path it is. */
  private static FollowPath.Builder makePathBuilder(Drivetrain drivetrain, Localizer localizer) {
    return new FollowPath.Builder(
        DriveType.SWERVE,               // which kind of drivetrain BLine is driving
        drivetrain,                     // the mechanism the command will require
        localizer::getPose,             // where we are (fused, Lesson 14)
        localizer::resetPose,           // how to snap the estimate to the path's start
        drivetrain::getChassisSpeeds,   // how fast we're going, robot-relative
        drivetrain::driveRobotRelative, // how to make the robot move
        new PIDController(PathConstants.kTranslationP, 0, 0),
        new PIDController(PathConstants.kRotationP, 0, 0),
        new PIDController(PathConstants.kCrossTrackP, 0, 0))
        .withDefaultShouldFlip();       // mirror the path for the red alliance
  }

  /** Names a path file can fire with lib_key. BLine keeps the registry statically. */
  private static void registerEventTriggers() {
    
    FollowPath.registerEventTrigger(
        "shoot",
        Command.noRequirements(coroutine -> System.out.println("Event: shoot!"))
            .named("Event: shoot"));
  }

 
  public static Command followPath(Drivetrain drivetrain, Localizer localizer, String pathName) {
    if (s_pathBuilder == null) {
      registerEventTriggers();
      s_pathBuilder = makePathBuilder(drivetrain, localizer);
    }
    return s_pathBuilder.build(new Path(pathName)).withPoseReset();
  }
}
