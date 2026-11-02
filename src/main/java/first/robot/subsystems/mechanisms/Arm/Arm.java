package first.robot.subsystems.features;

public class Arm implements GenericSubsystem {
    private final ArmIO io;
    
    public Arm() {
        super("arm.json");
    }
   
    public void configTestControls(Controller controller) {
        controller.createAButton().whileTrue((armCommand(.90)));
    }
    
}
