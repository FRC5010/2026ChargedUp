package first.robot.subsystems.mechanisms.Arm;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;

import first.robot.Constants.ArmConstants;


public class ArmIOTalonFX implements ArmIO {
    TalonFX m_pivot;
    private final MotionMagicVoltage m_request = new MotionMagicVoltage(0);

        public ArmIOTalonFX(int pivotId, int rollerId) {
            m_pivot = new TalonFX(pivotId, "rio");

            TalonFXConfiguration config = new TalonFXConfiguration();

            config.Feedback.SensorToMechanismRatio = ArmConstants.kGearRatio;

            config.Slot0.GravityType = GravityTypeValue.Arm_Cosine;
            config.Slot0.kV = ArmConstants.kArmKV;
            config.Slot0.kA = ArmConstants.kArmKA;
            config.Slot0.kP = ArmConstants.kArmKP;

            config.MotionMagic.MotionMagicCruiseVelocity = 
            ArmConstants.kMaxVelocity.in(RotationsPerSecond);
            config.MotionMagic.MotionMagicAcceleration = 
            ArmConstants.kMaxAcceleration.in(RotationsPerSecond);

            config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
            config.SoftwareLimitSwitch.ForwardSoftLimitThreshold =
                ArmConstants.kMaxAngle.in(Degrees);
            config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
            config.SoftwareLimitSwitch.ReverseSoftLimitThreshold =
                ArmConstants.kMinAngle.in(Rotations);

            m_pivot.getConfigurator().apply(config);
    
        }

        @Override
        public void updateInputs(ArmIOInputs inputs) {
            inputs.angleDegrees = m_pivot.getPosition().getValue().in(Degrees);
            inputs.velocityDegPerSec = m_pivot.getVelocity().getValue().in(DegreesPerSecond);
            inputs.appliedVolts = m_pivot.getMotorVoltages().getValueASDouble();
            inputs.setPointDegrees = 
                Rotations.of(m_pivot.getClosedLoopReference().getValueAsDouble()).in(Degrees);
        }

        @Override
        public void setGoalAngleDegrees(double angleDegrees) {
            m_pivot.setControl(m_request.withPosition(Degrees.of(angleDegrees)));
        }

        }
