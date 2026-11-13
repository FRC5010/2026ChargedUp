package first.robot.subsystems.mechanisms.Arm;

import static org.wpilib.units.Units.Kilograms;
import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Radians;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.system.DCMotor;
import org.wpilib.math.system.Models;
import org.wpilib.simulation.DCMotorSim;
import org.wpilib.simulation.SingleJointedArmSim;
import org.wpilib.system.RobotController;

import com.ctre.phoenix6.sim.TalonFXSimState;

import first.robot.Constants.ArmConstants;

/**
 * The sim implementation: the real TalonFX class (Phoenix simulates its own
 * firmware, so Motion Magic still runs), plus physics for both motors — an
 * arm that gravity pulls on, and a roller that just has some inertia.
 */
public class ArmIOSim extends ArmIOTalonFX {
  private final TalonFXSimState m_pivotSim;
  private final TalonFXSimState m_rollerSim;
    // The game pieces lying on the field. Nothing in the robot's own code reads
  // this list: the only way the robot learns about a piece is the beam break,
  // and only once a piece is really inside the intake.
  private final List<Translation2d> m_piecesOnField =
      new ArrayList<>(List.of(ArmConstants.kSimGamePiecePositions));
  private boolean m_holdingPiece = false;
  private double m_rollerOutput = 0.0;

  /** Where the robot really is: the simulated chassis, not the estimate. */
  private final Supplier<Pose2d> m_robotPose;

  private final SingleJointedArmSim m_model = new SingleJointedArmSim(
      DCMotor.getKrakenX60(1),
      ArmConstants.kGearRatio,
      SingleJointedArmSim.estimateMOI(
          ArmConstants.kArmLength.in(Meters), ArmConstants.kArmMass.in(Kilograms)),
      ArmConstants.kArmLength.in(Meters),
      ArmConstants.kMinAngle.in(Radians),
      ArmConstants.kMaxAngle.in(Radians),
      true, // simulate gravity — the whole point
      ArmConstants.kMinAngle.in(Radians));

  // The roller has no gravity — just a motor and some spinning inertia, the
  // same generic 2-state system every module motor already uses.
  private final DCMotorSim m_rollerModel = new DCMotorSim(
      Models.singleJointedArmFromPhysicalConstants(DCMotor.getKrakenX60(1), 0.001, 1.0),
      DCMotor.getKrakenX60(1));

  public ArmIOSim(Supplier<Pose2d> robotPose) {
    super(); // build both motors and apply the real configs
    m_pivotSim = m_pivot.getSimState();
    m_rollerSim = m_roller.getSimState();
    m_robotPose = robotPose;
  }

  @Override
  public void updateInputs(ArmIOInputs inputs) {
    stepSim(); // advance the physics one tick...
    super.updateInputs(inputs); // ...then read the sensors like the real class
        // Catch a piece if the roller is pulling in, the arm is down where it can
    // reach the floor, and a piece is really there.
    if (!m_holdingPiece && m_rollerOutput > 0 && m_model.getAngle() < 0) {
      for (int i = 0; i < m_piecesOnField.size(); i++) {
        if (inReach(m_piecesOnField.get(i))) {
          m_piecesOnField.remove(i);
          m_holdingPiece = true;
          break;
        }
      }
    }

    // The beam break reads what's in the intake, not what the roller is doing.
    // Spin it in empty space and nothing happens, which is the entire reason
    // for having the sensor.
    inputs.hasGamePiece = m_holdingPiece;

    Pose2d[] pieces = new Pose2d[m_piecesOnField.size()];
    for (int i = 0; i < pieces.length; i++) {
      pieces[i] = new Pose2d(m_piecesOnField.get(i), new Rotation2d());
    }
    Logger.recordOutput("Sim/GamePieces", pieces); // simulation only: no replay has a field
      @Override
  public void setRollerOutput(double output) {
    super.setRollerOutput(output);
    m_rollerOutput = output;
    if (output < 0 && m_holdingPiece) {
      // Spat back onto the field in front of the robot, where you can go and
      // pick it up again.
      Pose2d robot = m_robotPose.get();
      m_piecesOnField.add(
          robot.getTranslation().plus(new Translation2d(0.8, 0).rotateBy(robot.getRotation())));
      m_holdingPiece = false;
    }
  }
  }

  /** One tick of pretend reality: our voltage in, its motion back out. */
  private void stepSim() {
    m_pivotSim.setSupplyVoltage(RobotController.getBatteryVoltage());
    m_model.setInputVoltage(m_pivotSim.getMotorVoltage());
    m_model.update(0.020);

    // The model speaks radians; the motor's sim state speaks rotor rotations.
    m_pivotSim.setRawRotorPosition(m_model.getAngle() / (2 * Math.PI) * ArmConstants.kGearRatio);
    m_pivotSim.setRotorVelocity(m_model.getVelocity() / (2 * Math.PI) * ArmConstants.kGearRatio);

    m_rollerSim.setSupplyVoltage(RobotController.getBatteryVoltage());
    m_rollerModel.setInputVoltage(m_rollerSim.getMotorVoltage());
    m_rollerModel.update(0.020);
    m_rollerSim.setRawRotorPosition(m_rollerModel.getAngularPosition() / (2 * Math.PI));
    m_rollerSim.setRotorVelocity(m_rollerModel.getAngularVelocity() / (2 * Math.PI));
  }
}