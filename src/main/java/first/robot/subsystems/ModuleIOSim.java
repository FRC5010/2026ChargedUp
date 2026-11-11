package first.robot.subsystems;

import com.ctre.phoenix6.sim.CANcoderSimState;
import com.ctre.phoenix6.sim.TalonFXSimState;

import org.wpilib.math.system.DCMotor;
import org.wpilib.math.system.Models;
import org.wpilib.simulation.DCMotorSim;
import org.wpilib.system.RobotController;

import first.robot.Constants.DriveConstants;
import first.robot.Constants.SteerConstants;

/** Sim: the real TalonFX class, plus the physics models from Lessons 4-5/12. */
package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import org.ironmaple.simulation.drivesims.SwerveModuleSimulation;
import org.ironmaple.simulation.motorsims.SimulatedMotorController;

import com.ctre.phoenix6.sim.CANcoderSimState;
import com.ctre.phoenix6.sim.TalonFXSimState;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.RobotController;

public class ModuleIOSim extends ModuleIOTalonFX {
  private final TalonFXSimState m_driveSim;
  private final TalonFXSimState m_steerSim;
  private final CANcoderSimState m_steerEncoderSim;

  public ModuleIOSim(
      int driveId, int steerId, int cancoderId, double magnetOffsetRotations,
      SwerveModuleSimulation moduleSim) {
    super(driveId, steerId, cancoderId, magnetOffsetRotations); // motors, CANcoder, configs
    m_driveSim = m_driveMotor.getSimState();
    m_steerSim = m_steerMotor.getSimState();
    m_steerEncoderSim = m_steerEncoder.getSimState();

    // Drive: the engine asks what we're applying; we answer, and take its motion.
    moduleSim.useDriveMotorController(new SimulatedMotorController() {
      @Override
      public Voltage updateControlSignal(
          Angle mechanismAngle, AngularVelocity mechanismVelocity,
          Angle encoderAngle, AngularVelocity encoderVelocity) {
        m_driveSim.setSupplyVoltage(RobotController.getBatteryVoltage());
        m_driveSim.setRawRotorPosition(encoderAngle.in(Rotations));
        m_driveSim.setRotorVelocity(encoderVelocity.in(RotationsPerSecond));
        return Volts.of(m_driveSim.getMotorVoltage());
      }
    });

    // Steer: the same trade, plus the CANcoder — which reads the wheel, not the rotor.
    moduleSim.useSteerMotorController(new SimulatedMotorController() {
      @Override
      public Voltage updateControlSignal(
          Angle mechanismAngle, AngularVelocity mechanismVelocity,
          Angle encoderAngle, AngularVelocity encoderVelocity) {
        m_steerSim.setSupplyVoltage(RobotController.getBatteryVoltage());
        m_steerSim.setRawRotorPosition(encoderAngle.in(Rotations));
        m_steerSim.setRotorVelocity(encoderVelocity.in(RotationsPerSecond));
        m_steerEncoderSim.setRawPosition(mechanismAngle.in(Rotations));
        m_steerEncoderSim.setVelocity(mechanismVelocity.in(RotationsPerSecond));
        return Volts.of(m_steerSim.getMotorVoltage());
      }
    });
  }
}
  @Override
  public void simulationPeriodic() {
    SimulatedArena.getInstance().simulationPeriodic();
  }