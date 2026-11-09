package first.robot.subsystems;

import static org.wpilib.units.Units.Kilograms;
import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Radians;

import com.ctre.phoenix6.sim.TalonFXSimState;

import org.wpilib.math.system.DCMotor;
import org.wpilib.simulation.SingleJointedArmSim;
import org.wpilib.system.RobotController;

import first.robot.Constants.WristConstants;

public class WristIOSim extends WristIOTalonFX {
  private final TalonFXSimState m_pivotSim;

  private final SingleJointedArmSim m_model = new SingleJointedArmSim(
      DCMotor.getKrakenX60(1),
      WristConstants.kGearRatio,
      SingleJointedArmSim.estimateMOI(
          WristConstants.kArmLength.in(Meters), WristConstants.kArmMass.in(Kilograms)),
      WristConstants.kArmLength.in(Meters),
      WristConstants.kMinAngle.in(Radians),
      WristConstants.kMaxAngle.in(Radians),
      true, 
      WristConstants.kMinAngle.in(Radians));

  public WristIOSim() {
    super(); 
    m_pivotSim = m_pivot.getSimState();
  }

  @Override
  public void updateInputs(WristIOInputs inputs) {
    stepSim();
    super.updateInputs(inputs); 
  }

  private void stepSim() {
    m_pivotSim.setSupplyVoltage(RobotController.getBatteryVoltage());
    m_model.setInputVoltage(m_pivotSim.getMotorVoltage());
    m_model.update(0.020);

    m_pivotSim.setRawRotorPosition(m_model.getAngle() / (2 * Math.PI) * WristConstants.kGearRatio);
    m_pivotSim.setRotorVelocity(m_model.getVelocity() / (2 * Math.PI) * WristConstants.kGearRatio);

  }
}
