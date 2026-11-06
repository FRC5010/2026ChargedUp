package first.robot.subsystems.mechanisms.Arm;

import static org.wpilib.units.Units.Rotations;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.units.measure.Angle;

public class Arm implements Mechanism {
    private final ArmIO io;
    
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
    
}

