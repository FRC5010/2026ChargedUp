package first.robot.subsystems;

import static org.wpilib.units.Units.Degrees;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.units.measure.Angle;

import first.robot.Constants;
import first.robot.Constants.WristConstants;
import first.robot.subsystems.WristIO.WristIOInputs;


public class Wrist implements Mechanism {
  private final WristIO m_io = switch (Constants.kCurrentMode) {
    case REAL -> new WristIOTalonFX();
    case SIM -> new WristIOSim();
    case REPLAY -> new WristIO() {}; 
  };
  private final WristIOInputs m_inputs = new WristIOInputs();
  private Angle m_goal = WristConstants.kStowed;

 
  
public Command goToAngle(Angle angle) {
    return runRepeatedly(() -> {
          m_goal = clampToTravel(angle);
          m_io.setGoalAngleDegrees(m_goal.in(Degrees));
        })
        .until(this::atGoal)
        .named("Go To Angle");
  } 
 // Needs info on the goal.


  public boolean atGoal() {
    return Math.abs(m_inputs.angleDegrees - m_goal.in(Degrees)) < WristConstants.kTolerance.in(Degrees);
  }

  
  private static Angle clampToTravel(Angle angle) {
    if (angle.gt(WristConstants.kMaxAngle)) {
      return WristConstants.kMaxAngle;
    } else if (angle.lt(WristConstants.kMinAngle)) {
      return WristConstants.kMinAngle;
    } else {
      return angle;
    }
  }

 // private static Angle toDrawingAngle(Angle WristAngle) {
   //return armAngle.minus(Needs the constants of the wrist's base. Specifically it's angle)
 // }
}

