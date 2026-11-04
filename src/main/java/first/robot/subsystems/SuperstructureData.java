package first.robot.subsystems;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Meters;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Distance;

public class SuperstructureData {
    //define final constants for the states
    public static final SuperstructureData kUnhomed = 
    new SuperstructureData(Degrees.of(0), Degrees.of(0), Meters.of(0));
    public static final SuperstructureData kIdle = 
    new SuperstructureData(Degrees.of(1), Degrees.of(1), Meters.of(0));

    //define parameters
    private final Angle wristAngle;
    private final Angle armAngle;
    private final Distance armDistance;

    //default constructor
    SuperstructureData() {
        this.wristAngle = Degrees.of(0);
        this.armAngle = Degrees.of(0);
        this.armDistance = Meters.of(0);
    }
    //pass in values
    SuperstructureData(Angle _wristAngle, Angle _armAngle, Distance _armDistance) {
        this.wristAngle = _wristAngle;
        this.armAngle = _armAngle;
        this.armDistance = _armDistance;
    }
}
