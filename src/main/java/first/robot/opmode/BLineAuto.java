package first.robot.opmode;

import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.PeriodicOpMode;

import first.robot.Robot;
import first.robot.commands.BLine;

@Autonomous(name = "BLine: New Path", group = "BLine")
public class BLineAuto extends PeriodicOpMode {
  private final Robot robot;

  public BLineAuto(Robot robot) {
    this.robot = robot;

    try {
      BLine bline = new BLine(robot.drivetrain, robot.localizer);
      RobotModeTriggers.autonomous().onTrue(bline.autoCommand("new_path"));
    } catch (Exception e) {
      System.err.println("[BLineAuto] Could not load path \"new_path\": " + e.getMessage());
    }
  }

  @Override
  public void periodic() {}
}
