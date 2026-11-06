package first.robot.subsystems;

import org.wpilib.hardware.bus.CANPort;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;

import first.robot.Constants.WristConstants;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.RotationsPerSecond;
import static org.wpilib.units.Units.RotationsPerSecondPerSecond;
import static org.wpilib.units.Units.Volts;



public class WristIOTalonFX implements WristIO {
  
  protected final TalonFX m_pivot;
  private final MotionMagicVoltage m_request = new MotionMagicVoltage(0);

  public WristIOTalonFX() {
    m_pivot = new TalonFX(WristConstants.kPivotMotorPort, new CANBus(CANPort.CAN_S0));
    

    TalonFXConfiguration config = new TalonFXConfiguration();

    config.Feedback.SensorToMechanismRatio = WristConstants.kGearRatio;
    config.Slot0.kG = WristConstants.kArmKG;
    config.Slot0.GravityType = GravityTypeValue.Arm_Cosine;
    config.Slot0.kV = WristConstants.kArmKV;
    config.Slot0.kA = WristConstants.kArmKA;
    config.Slot0.kP = WristConstants.kArmKP;

    config.MotionMagic.MotionMagicCruiseVelocity = WristConstants.kMaxVelocity.in(RotationsPerSecond);
    config.MotionMagic.MotionMagicAcceleration =
        WristConstants.kMaxAcceleration.in(RotationsPerSecondPerSecond);

    config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
    config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = WristConstants.kMaxAngle.in(Rotations);
    config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
    config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = WristConstants.kMinAngle.in(Rotations);

    m_pivot.getConfigurator().apply(config);
    
  }

  @Override
  public void updateInputs(WristIOInputs inputs) {
    inputs.angleDegrees = m_pivot.getPosition().getValue().in(Degrees);
    inputs.velocityDegreesPerSecond = m_pivot.getVelocity().getValue().in(DegreesPerSecond);
    inputs.appliedVolts = m_pivot.getMotorVoltage().getValue().in(Volts);
    inputs.setpointDegrees = Rotations.of(m_pivot.getClosedLoopReference().getValue()).in(Degrees);
    
  }

  public void setGoalAngleDegrees(double angleDegrees) {
    m_pivot.setControl(m_request.withPosition(Degrees.of(angleDegrees)));
  }

    
}
