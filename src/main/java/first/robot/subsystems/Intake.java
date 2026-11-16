package first.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import first.robot.Constants;

public class Intake {

    private final TalonFX intakeMotor;

    public Intake() {
        intakeMotor = new TalonFX(Constants.kRollerMotorPort);
    }

    public void setIntakePosition(double position) {

    }

    public void setIntakeSpeed(double speed) {

    }

    public void runIntake(double output) {
        intakeMotor.set(output);
    }

    public void stopIntake() {
        intakeMotor.set(0.0);
    }

}
