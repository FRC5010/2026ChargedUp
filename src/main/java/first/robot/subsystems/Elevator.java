package first.robot.subsystems;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Volts;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.units.measure.Distance;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.mechanism.LoggedMechanismLigament2d;

import first.robot.Constants;
import first.robot.Constants.ElevatorConstants;
import org.littletonrobotics.junction.mechanism.LoggedMechanism2d;
import org.littletonrobotics.junction.mechanism.LoggedMechanismRoot2d;
/** Scoring elevator: Motion Magic profiles the move, Slot0's feedforward model holds it there. */
public class Elevator implements Mechanism {
  private final ElevatorIO m_io = switch (Constants.kCurrentMode) {
    case REAL -> new ElevatorIOTalonFX();
    case SIM -> new ElevatorIOSim();
    case REPLAY -> new ElevatorIO() {}; // inputs come from the log
  };
  private final ElevatorIOInputsAutoLogged m_inputs = new ElevatorIOInputsAutoLogged();
  private Distance m_goal = ElevatorConstants.kStowed;


  public Elevator() {
    Scheduler.getDefault().addPeriodic(this::periodic);
  }
    /** The mount point on top of the carriage. Anything appended here rides along. */

  /** One tick of sensing: read the hardware into the bundle and log it. */
  private void periodic() {
    m_io.updateInputs(m_inputs);
    Logger.processInputs("Elevator", m_inputs); // every reading, under Elevator/
    Logger.recordOutput("Elevator/GoalMeters", m_goal.in(Meters));

  }

  /** Send the carriage to 'target', clamped to safe travel. Keeps holding once it arrives. */
  public Command goToHeight(Distance target) {
    return runRepeatedly(() -> {
          m_goal = clampToTravel(target);
          m_io.setGoalHeightMeters(m_goal.in(Meters));
        })
        .until(this::atGoal)
        .named("Go To Height");
  }

  /**
   * Drive gently downward until the switch trips, then believe it. Open loop on
   * purpose: the encoder is exactly the thing we don't trust yet.
   */
  public Command home() {
    return run(() -> m_io.setVoltage((double) ElevatorConstants.kHomingVolts))
        .until(this::atBottomLimit)
        .finallyDo(() -> {
          m_io.setVoltage(0);
          acceptBottomLimit();
          m_goal = ElevatorConstants.kBottomLimitHeight;
        });
  }

  private Command run(Runnable commandBody) {
    return Commands.run(commandBody, this);
  }

    /** The switch knows where the carriage is. The encoder only had an opinion. */
    private void acceptBottomLimit() {
        m_io.setPositionMeters(ElevatorConstants.kBottomLimitHeight.in(Meters));
    }

  public boolean atBottomLimit() {
    return m_inputs.atBottomLimit;
  }

  public Command rezeroAtBottom() {
    return Commands.runOnce(this::acceptBottomLimit).ignoringDisable(true);
  }
    // The picture: a canvas, one point anchored to it, and a chain of segments.
    private final LoggedMechanism2d m_mechanism = new LoggedMechanism2d(
            ElevatorConstants.kDisplayWidth, ElevatorConstants.kDisplayHeight);
    private final LoggedMechanismRoot2d m_base = m_mechanism.getRoot(
            "Base", ElevatorConstants.kDisplayWidth.in(Meters) / 2, 0);
    /** Straight up from the base. Its length is the carriage height. */
    private final LoggedMechanismLigament2d m_carriage = m_base.append(
            new LoggedMechanismLigament2d("Carriage", Meters.of(0), Degrees.of(90)));
    /** Rides on top of the carriage. Lesson 20 puts a real arm here. */
    private final LoggedMechanismLigament2d m_effector = m_carriage.append(
            new LoggedMechanismLigament2d(
                    "Effector", ElevatorConstants.kEffectorLength, Degrees.of(-90)));
  public boolean atGoal() {
    return Math.abs(m_inputs.heightMeters - m_goal.in(Meters)) < ElevatorConstants.kTolerance.in(Meters);
  }

  public double getHeightMeters() {
    return m_inputs.heightMeters;
  }

  /** Open-loop drive, for homing: no goal, no profile, just a voltage. */
  public void setVoltage(double volts) {
    m_io.setVoltage(volts);
  }

  /** Declare the carriage to be at this height right now, whatever it read before. */
  public void setPositionMeters(double heightMeters) {
    m_io.setPositionMeters(heightMeters);
  }
    /** The mount point on top of the carriage. Anything appended here rides along. */
       /** Rides on top of the carriage. Lesson 20 puts a real arm here. */

    public LoggedMechanismLigament2d getCarriage() {
        return m_carriage;
    }
  /** Keeps 'target' inside [kMinHeight, kMaxHeight] — there's no MathUtil.clamp to reach for here. */
  private static Distance clampToTravel(Distance target) {
    if (target.gt(ElevatorConstants.kMaxHeight)) {
      return ElevatorConstants.kMaxHeight;
    } else if (target.lt(ElevatorConstants.kMinHeight)) {
      return ElevatorConstants.kMinHeight;
    } else {
      return target;
    }
  }
}
