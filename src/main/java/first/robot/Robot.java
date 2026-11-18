// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.command3.Mechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.SchedulerEvent;
import org.wpilib.command3.button.CommandGamepad;
import org.wpilib.driverstation.RobotState;
import org.wpilib.system.DataLogManager;
import org.wpilib.telemetry.Telemetry;

import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedOpModeRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

import first.robot.commands.Autos;
import first.robot.subsystems.Drivetrain;
import first.robot.subsystems.Elevator;
import first.robot.subsystems.Localizer;
import first.robot.subsystems.Superstructure;
import first.robot.subsystems.SuperstructureState;
import first.robot.subsystems.VisionPoseProvider;
import first.robot.subsystems.mechanisms.Arm.Arm;
import org.wpilib.driverstation.RobotState;

import first.robot.subsystems.Superstructure;
import first.robot.subsystems.SuperstructureState;


/**
 * The methods in this class are called automatically as described in the
 * OpModeRobot documentation.
 * OpMode classes anywhere in the package (or sub-packages) where this class is
 * located are
 * automatically registered to display in the Driver Station. If you change the
 * name of this class
 * or the package after creating this project, you must also update the
 * Main.java file in the
 * project.
 */
public class Robot extends LoggedOpModeRobot {
  // Logging starts before anything else is built. A bare { } block in a class
  // runs while the object is being built, in order with the fields around it,
  // so everything declared below this is built with the logger already running.
  {
    startLogging();
  }

  // The robot's hardware lives here — built once, when the robot boots, and
  // alive for as long as the robot runs. Opmodes are rebuilt fresh every time
  // they're selected, so they reach in and use these instead of owning them.
  public final CommandGamepad driverController = new CommandGamepad(0);
  public final Drivetrain drivetrain = new Drivetrain();
  // Localizer reads the drivetrain's kinematics/rotation/module positions at
  // construction, so drivetrain must be built first — it already is, above.
  public final Localizer localizer = new Localizer(drivetrain);
  public final Elevator elevator = new Elevator();
  public final Arm arm = new Arm(elevator, localizer::getPose);
  public final Superstructure superstructure = new Superstructure(elevator, arm);

  /**
   * ====== NEXT LESSON: CHANGE THE CODE BELOW ======
   * Replace the fake camera with two real Limelight pose providers, front and
   * back.
   * Make them blank finals — declared here, assigned in the constructor — because
   * building a camera needs the localizer's pose, and the constructor body runs
   * after
   * every field above is finished.
   */

  public final VisionPoseProvider camera = new VisionPoseProvider();

    // Arm reads elevator.getCarriage() in its constructor, so elevator must be
  // a finished object first — it already is, above. In simulation its intake
  // also needs to know where the robot really is, which only drivetrain knows.


  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  public Robot() {
    Scheduler.getDefault().addEventListener(this::logCommandStart);

    /**
     * ====== NEXT LESSON: CHANGE THE CODE BELOW ======
     * Build both cameras with LimelightPoseProvider.makeCamera — each one's name,
     * mount
     * position and the localizer's pose getter — then register each with the
     * localizer.
     */
    Autos.registerEventTriggers(); // once, for the whole program

    // Motion that belongs to a state, bound to being in it. No guard here:
    // being in the state at all means the state machine already said yes. The
    // moment the state changes, whileTrue cancels the old motion. HOLDING has
    // none — the elevator and arm are already where they need to be. These are
    // made here, not in an opmode, so they hold in every opmode there is.
    //
    // "And enabled" keeps the motion alive across a disable. Disabling cancels
    // every command that moves a mechanism, and a state that didn't change
    // gives whileTrue no new edge to start from. This way, enabling the robot
    // is that edge.
    superstructure.inState(SuperstructureState.GROUNDCONEINTAKING)
        .and(RobotState::isEnabled)
        .whileTrue(superstructure.intakeMotion());
    superstructure.inState(SuperstructureState.CONESCORELOW)
        .and(RobotState::isEnabled)
        .whileTrue(superstructure.scoreMotion());
    superstructure.inState(SuperstructureState.IDLE)
        .and(RobotState::isEnabled)
        .whileTrue(superstructure.idleMotion());

    localizer.addProvider(camera); // the second provider — vision
  }

  /** Tells AdvantageKit where readings come from and where results go, then starts it. */
  private void startLogging() {
    if (Constants.kCurrentMode == Constants.Mode.REPLAY) {
      // Replay: every reading comes from a saved log, and the code runs as fast
      // as it can instead of once every 20 ms.
      setUseTiming(false);
      String logPath = LogFileUtil.findReplayLog();
      Logger.setReplaySource(new WPILOGReader(logPath));
      // What the code works out this time goes to a new file beside it, ending in _sim.
      Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim")));
    } else {
      // A real robot, or a fresh simulation: publish live, and save it all to a file.
      Logger.addDataReceiver(new NT4Publisher());
      Logger.addDataReceiver(new WPILOGWriter());
      DataLogManager.start(); // still saves everything else that's published, like the field widget
    }
    Logger.start();
  }

  /** This function is called exactly once when the DS first connects. */
  @Override
  public void driverStationConnected() {
  }

  /**
   * Runs every tick, no matter which opmode is selected or whether the robot is
   * enabled.
   */
  @Override
  public void robotPeriodic() {
    Scheduler.getDefault().run();
  }

  private void logCommandStart(SchedulerEvent event) {
    if (event instanceof SchedulerEvent.Scheduled scheduled) {
      for (Mechanism mechanism : scheduled.command().requirements()) {
        Logger.recordOutput(mechanism.getName() + "/CurrentCommand", scheduled.command().name());
      }
    }
  }

  /** Runs every tick, but only while the code is running in simulation. */
  @Override
  public void simulationPeriodic() {
  }

  /**
   * This function is called periodically anytime when no opmode is selected,
   * including when the
   * Driver Station is disconnected.
   */
  @Override
  public void nonePeriodic() {
  }
}
