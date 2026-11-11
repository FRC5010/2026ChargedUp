package first.robot.subsystems.mechanisms.Arm;

import static org.wpilib.units.Units.Kilograms;
import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.RadiansPerSecond;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.RotationsPerSecond;

import org.wpilib.math.system.DCMotor;
import org.wpilib.simulation.SingleJointedArmSim;
import org.wpilib.system.RobotController;

import com.ctre.phoenix6.sim.TalonFXSimState;

import first.robot.Constants.ArmConstants;

public class ArmIOSim extends ArmIOTalonFX {
    private final TalonFXSimState m_pivotSim;
    
    private final SingleJointedArmSim m_model = new SingleJointedArmSim(
        DCMotor.getKrakenX60(1),
        ArmConstants.kGearRatio,
        SingleJointedArmSim.estimateMOI(ArmConstants.kArmLength.in(Meters), ArmConstants.kArmMass.in(Kilograms)),
        ArmConstants.kArmLength.in(Meters),
        ArmConstants.kMinAngle.in(Radians),
        ArmConstants.kMaxAngle.in(Radians),
        true,
        ArmConstants.kMinAngle.in(Radians));
    
        public ArmIOSim(int pivotId, int rollerId) {
            super(pivotId, rollerId);
            m_pivotSim = new TalonFXSimState(m_pivot);
        }

        @Override
        public void updateInputs(ArmIOInputs inputs) {
            stepSim();
            super.updateInputs(inputs);
        }

        private void stepSim() {
            m_pivotSim.setSupplyVoltage(RobotController.getBatteryVoltage());
            m_model.setInputVoltage(m_pivotSim.getMotorVoltage());
            m_model.update(0.02);
            m_pivotSim.setIntegratedSensorRawPosition(
                Radians.of(m_model.getAngleRadians()).in(Rotations) * ArmConstants.kGearRatio);
                m_pivotSim.setRotorVelocity(
                    RadiansPerSecond.of(m_model.getVelocityRadiansPerSec()).in(RotationsPerSecond) * ArmConstants.kGearRatio
                );
                
        }
}
