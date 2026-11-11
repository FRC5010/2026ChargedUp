package frc.robot.subsystems.mechanisms.Arm;



public class ArmIOTalonFX implements ArmIO {
    m_pivot = new TalonFX(pivotId);
    m_roller = new TalonFX(rollerId);
    private final MotionMagicVoltage m_request = new MotionMagicVoltage(0);

        public ArmIOTalonFX(int pivotId, int rollerId) {
            m_pivot = new TalonFX(pivotId);
            m_roller = new TalonFX(rollerId);

            TalonFXConfiguration config = new TalonFXConfiguration();

            config.Feedback.SensorToMechanismRatio = ArmConstants.kGearRatio;

            config.Slot0.kg = ArmConstants.kArmKG;

            config.Slot0.GravityType = GravityTypeValue.Arm_Cosine
            config.Slot0.kV = ArmConstants.kArmKV;
            config.Slot0.kA = ArmConstants.kArmKA;
            config.Slot0.kP = ArmConstants.kAmrKP;

            config.MotionMagic.MotionMagicCruiseVelocity = 
            ArmConstants.kMaxVelocity.in(RotationsPerSecond);
            config.MotionMagic.kMaxAcceleration = 
            ArmConstants.kMaxAcceleration.in(RotationsPerSecond);

            config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
            config.SoftwareLimitSwitch.ForwardSoftLimitThreshold
                ArmConstants.kMaxAngle.in(Rotations);
            config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
            config.SoftwareLimitSwitch.ReverseSoftLimitThreshold =
                ArmConstants.kMinAngle.in(Rotations);

            m_pivot.getConfigurator().apply(config);
            m_roller.getConfigurator().apply(new TalonFXConfiguration());
        }

        @Override
        public void updateInputs(ArmIOInputs inputs) {
            inputs.angleDegrees = m_pivot.getPosition().getValue().in(Degrees);
            inputs.velocityDegPerSec = m_pivot.getVelocity().getValue().in(DegreesPerSecond);
            inputs.appliedVolts = m_pivot.getMotorVoltages().getValueASDouble();
            inputs setpointDegrees = 
                Rotations.of(m_pivot.getClosedLoopReference().getValueAsDouble()).in(Degrees);
                inputs.rollerVelocityRotPerSec = m_roller.getVelocity().getValueAsDouble();
        }

        @Override
        public void setGoalAngleDegrees(double angleDegrees) {
            m_pivot.setControl(m_request.withPosition(Degrees.of(angleDegrees)));
        }

        @Override
        public void setRollerOutput(double output) {
            m_roller.set(output);
        }

        }
