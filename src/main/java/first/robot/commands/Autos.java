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

  private static FollowPath.Builder s_pathBuilder;

  private Autos() {}

  /** Drive 1 m, turn to 90°, drive 1 m more. */
  public static Command driveTurnDrive(Drivetrain drivetrain) {
    return Command.noRequirements(coroutine -> {
          coroutine.await(drivetrain.driveDistance(1.0));  
          coroutine.await(drivetrain.turnToHeading(90));   
          coroutine.await(drivetrain.driveDistance(1.0));  
        })
        .named("Drive Turn Drive");
  }

  /** Everything about following a path that doesn't depend on which path it is. */
  private static FollowPath.Builder makePathBuilder(Drivetrain drivetrain, Localizer localizer) {
    return new FollowPath.Builder(
        DriveType.SWERVE,             
        drivetrain,               
        localizer::getPose,            
        localizer::resetPose,           
        drivetrain::getChassisSpeeds,   
        drivetrain::driveRobotRelative, // how to make the robot move
        new PIDController(PathConstants.kTranslationP, 0, 0),
        new PIDController(PathConstants.kRotationP, 0, 0),
        new PIDController(PathConstants.kCrossTrackP, 0, 0))
        .withDefaultShouldFlip();       // mirror the path for the red alliance
  }

  
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
