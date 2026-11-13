package first.robot.subsystems;

import org.littletonrobotics.junction.AutoLog;
    
public interface WristIO{
    @AutoLog
        public static class WristIOInputs{
            public double angleDegrees = 0.0;
            public double velocityDegreesPerSecond = 0.0;
            public double appliedVolts = 0.0;
            public double setpointDegrees = 0.0;
        }
        public default void updateInputs(WristIOInputs inputs) {}

        public default void setGoalDegrees(double goalDegrees) {}
} 

