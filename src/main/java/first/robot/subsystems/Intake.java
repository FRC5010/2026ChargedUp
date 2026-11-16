package first.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import first.robot.Constants;

public class Intake {

    private final TalonFX intakeMotor;
    public double intakeSpeed = 0.5;
    public double intakePosition = 0.0;
    public boolean isIntakeRunning = false;

    public Intake() {
        intakeMotor = new TalonFX(Constants.kRollerMotorPort);
    }

    public void setIntakePosition(double position) {
        intakePosition = position;
    }

    public void setIntakeSpeed(double speed) {
        intakeSpeed = speed;
    }

    public void runIntake(double output) {
        intakeMotor.setControl(new DutyCycleOut(output));
        isIntakeRunning = true;
        System.out.println("Intake running.");
        detectObject();
    }

    public void stopIntake() {
        intakeMotor.setControl(new DutyCycleOut(0.0));
        isIntakeRunning = false;
        System.out.println("Intake stopped.");
    }

    public boolean isIntakeActive() {
        return isIntakeRunning;
    }

    public int currentDraw() {
        return intakeMotor.getSupplyCurrent().get();
    }

    public void detectObject() {
        while (isIntakeRunning) {
            if (currentDraw() > 14) {
                System.out.println("Object detected.");
            }
        }
    }

}
