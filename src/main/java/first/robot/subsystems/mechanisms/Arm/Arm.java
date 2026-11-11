package first.robot.subsystems.mechanisms.Arm;

import org.wpilib.command3.Command;
import org.wpilib.math.util.MathUtil;
import org.wpilib.units.measure.Angle;

import first.robot.Constants;
import first.robot.Constants.ArmConstants;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.RadiansPerSecond;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.RotationsPerSecond;


public class Arm extends Mechanisms {
    private final ArmIO m_io = switch(Constants.kCurrentMode) {
        case REAL -> new ArmIOTalonFX(ArmConstants.kPivotMotorPort);
        case SIM -> new ArmIOSim(ArmConstants.kPivotMotorPort, ArmConstants.kRollerMotorPort);
        case REPLAY -> new ArmIO() {};
    };
    private final ArmIOInputsAutoLogged m_inputs = new ArmIOInputsAutoLogged();

    private Angle m_goal = ArmConstants.kStowed;

    private final LoggedMechanismLigament2d m_ligament;

    public Arm()
     @Override
    public void periodic() {
        m_io.updateInputs(m_inputs);
        Logger.processInputs("Arm", m_inputs);
        Logger.recordOutput("Arm/GoalDegrees", m_goal.in(Degrees));
        Logger.recordOutput("Arm/AtGoal", atGoal());

        m_ligament.setAngle(toDrawingAngle(Degrees.of(m_inputs.angleDegrees)));
        m_ligament.setColor(
                atGoal() ? ElevatorConstants.kAtGoalColor : ElevatorConstants.kMovingColor);
    }

    /** Swing the arm to an angle. Finishes on arrival; the firmware holds it there. */
    public Command goToAngle(Angle angle) {
        return run(() -> {
            m_goal = clampToTravel(angle);
            m_io.setGoalAngleDegrees(m_goal.in(Degrees));
        }).until(this::atGoal);
    }

    /** Spin the roller while this command runs, and stop it when the command ends. */
    public Command runRoller(double output) {
        return run(() -> m_io.setRollerOutput(output))
                .finallyDo(() -> m_io.setRollerOutput(0.0));
    }

    public boolean atGoal() {
        return Math.abs(m_inputs.angleDegrees - m_goal.in(Degrees))
                < ArmConstants.kTolerance.in(Degrees);
    }

    /** An angle outside the arm's swing is a bug, not a request. Pin it to the ends. */
    private static Angle clampToTravel(Angle angle) {
        return Degrees.of(MathUtil.clamp(
                angle.in(Degrees),
                ArmConstants.kMinAngle.in(Degrees),
                ArmConstants.kMaxAngle.in(Degrees)));
    }

    /**
     * A ligament's angle is measured from its parent, and the carriage points
     * straight up — so a world angle becomes a drawing angle by subtracting it.
     */
    private static Angle toDrawingAngle(Angle armAngle) {
        return armAngle.minus(ElevatorConstants.kCarriageAngle);
    }

}

