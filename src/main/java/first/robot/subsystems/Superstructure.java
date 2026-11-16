package first.robot.subsystems;

import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.Trigger;
import org.wpilib.math.filter.Debouncer;
import first.robot.subsystems.mechanisms.Arm.Arm;

import org.littletonrobotics.junction.Logger;

import first.robot.Constants.ArmConstants;
import first.robot.Constants.ElevatorConstants;

public class Superstructure {
  private final Elevator m_elevator;
  private final Arm m_arm;
  private SuperstructureState m_state = SuperstructureState.UNHOMED;

  private final Debouncer m_pieceFilter = new Debouncer(ArmConstants.kBeamDebounce.in(Seconds));
  
  public Superstructure(Elevator elevator, Arm arm) {
    m_elevator = elevator;
    m_arm = arm;
    Scheduler.getDefault().addPeriodic(this::periodic);
  }

    private void periodic() {
    boolean hasPiece = m_pieceFilter.calculate(m_arm.hasGamePiece());

    switch (m_state) {
      case UNHOMED -> {
        if (m_elevator.isHomed()) {
          m_state = SuperstructureState.IDLE;
        }
      }
      case GROUNDCONEINTAKING -> {
        if (hasPiece) {
          m_state = SuperstructureState.HOLDINGCONE;
        }
      }
      case HOLDINGCONE, CONESCORELOW -> {
        if (!hasPiece) {
          m_state = SuperstructureState.IDLE;
        }
      }
      case IDLE -> {
        // Nothing happens here on its own. Somebody has to ask.
      }
    }

    Logger.recordOutput("Superstructure/State", m_state.name());

 }
   /** What the robot is doing. Everything downstream reads this instead of sensors. */
  public SuperstructureState getState() {
    return m_state;
  }

  /** True exactly while the robot is in this state, as a Trigger you can bind to. */
  public Trigger inState(SuperstructureState state) {
    return new Trigger(() -> m_state == state);
  }
    // --- Requests: button-bound, guarded, and nothing but a mode switch. ---

  public Command requestIntake() {
    return request(SuperstructureState.GROUNDCONEINTAKING);
  }

  public Command requestIdle() {
    return request(SuperstructureState.IDLE);
  }

  public Command requestScore() {
    return request(SuperstructureState.CONESCORELOW);
  }

  private Command request(SuperstructureState next) {
    return Command.noRequirements(coroutine -> {
          if (!allow(next)) {
            return;
          }
          m_state = next;
        })
        .named("Request " + next.name());
  }

  /** Legality check, logged on refusal — a refused request looks identical to a broken button otherwise. */
  private boolean allow(SuperstructureState next) {
    boolean ok = m_state.canGoTo(next);
    if (!ok) {
      Logger.recordOutput("Superstructure/Rejected", m_state + " -> " + next);
    }
    return ok;
  }
    // --- Motion: inState-bound, unguarded, because arriving already means yes. ---

  /** Both steps require Arm — the same Mechanism — so this has to be a sequence, not a parallel. */
  public Command intakeMotion() {
    return Command.sequence(
            m_arm.goToAngle(ArmConstants.kIntake),
            m_arm.runRoller(ArmConstants.kIntakeSpeed))
        .named("Intake Motion");
  }

  /**
   * The handoff — the thing Lesson 22 did without being able to name it. Arm
   * and elevator require different Mechanisms, so this may run in parallel.
   */
  public Command handoffMotion() {
    return Command.parallel(
            m_arm.goToAngle(ArmConstants.kStowed),
            m_elevator.goToHeight(ElevatorConstants.kScoreMid))
        .named("Handoff Motion");
  }

  /** Roller out. The piece leaving is what ends the state, not a timer. */
  public Command scoreMotion() {
    return m_arm.runRoller(ArmConstants.kEjectSpeed);
  }

  /** Back to neutral: arm stowed, elevator down. */
  public Command idleMotion() {
    return Command.parallel(
            m_arm.goToAngle(ArmConstants.kStowed),
            m_elevator.goToHeight(ElevatorConstants.kStowed))
        .named("Idle Motion");
  }


}