package first.robot.subsystems.mechanisms.Arm;

import org.littletonrobotics.junction.AutoLog;

public interface ArmIO {
    @AutoLog
    public static class ArmIOInputs {
        public double angleDegrees = 0.0;
        public double velocityDegPerSec = 0.0;
        public double appliedVolts = 0.0;
        public double setPointDegrees = 0.0;
        public double rollerVelocityRotPerSec = 0.0;
    }

    public default void updateInputs(ArmIOInputs inputs) {}

    public default void setGoalAngleDegrees(double angleDegrees) {}

    public default void setRollerOutput(double output) {}

    


}