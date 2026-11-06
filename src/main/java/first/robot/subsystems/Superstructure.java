package first.robot.subsystems;

import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.Trigger;
import org.wpilib.math.filter.Debouncer;

//import org.littletonrobotics.junction.Logger;

//import first.robot.Constants.ArmConstants;
//import first.robot.Constants.ElevatorConstants;

public class Superstructure {
  //private final Elevator m_elevator;
  //private final Arm m_arm;
  private SuperstructureState m_state = SuperstructureState.UNHOMED;
/*
  private final Debouncer m_pieceFilter = new Debouncer(ArmConstants.kBeamDebounce.in(Seconds));

  public Superstructure(Elevator elevator, Arm arm) {
    m_elevator = elevator;
    m_arm = arm;
    Scheduler.getDefault().addPeriodic(this::periodic);
  }
    */

    private void periodic() {
    //boolean hasPiece = m_pieceFilter.calculate(m_arm.hasGamePiece());
/*
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
*/
 }

}