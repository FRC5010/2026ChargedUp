package first.robot.subsystems.features;

public class Arm implements GenericSubsystem {
    private final ArmIO io;
    
    public Arm() {
        
    }
   
    public void configTestControls(Gamepad controller) {
        controller.westFace().whileTrue((armCommand(.90)));
    }
    
}
