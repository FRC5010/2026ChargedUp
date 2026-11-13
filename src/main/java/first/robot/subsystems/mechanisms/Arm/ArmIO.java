package first.robot.subsystems.mechanisms.Arm;

public interface ArmIO {
    void setArmPosition(double position);

    double getArmPosition(double position);

    public static class ArmIOInputs {
        public double rollerVelocityRotPerSec = 0.0;
    }

    public default void setRollerOutput(double output) {}

}