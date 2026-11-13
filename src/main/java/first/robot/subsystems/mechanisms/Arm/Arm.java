package first.robot.subsystems.mechanisms.Arm;

import static org.wpilib.units.Units.Rotations;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.units.measure.Angle;

import first.robot.Constants;

public class Arm implements Mechanism {
    private final ArmIO m_io = switch (Constants.kCurrentMode) {
    case REAL -> new ArmIOTalonFX();
    case SIM -> new ArmIOSim();
    case REPLAY -> new ArmIO() {}; // inputs come from the log
  };
    
    public Arm() {
        
    }
   
    public void configTestControls(Gamepad controller) {
        controller.faceUp(null).whileTrue((armCommand(.90)));
    }

    public void setArmPosition(double position) {
        io.setArmPosition(position);
    }

    public Command armCommand(double position) {
        return null;
        
    }

    public Angle getMechanismPosition() {
        return Rotations.of(kPivotEncoderPort.getPosition().get().div(kGearRatio));
    }

    public Angle getArmAngle() {
        return getMechanismPosition();
    }

    public Command runRoller(double output) {
    return runRepeatedly(() -> m_io.setRollerOutput(output))
        .whenCanceled(() -> m_io.setRollerOutput(0.0))
        .named("Run Roller");
  }

    
}

