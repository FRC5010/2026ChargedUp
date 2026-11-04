package first.robot.subsystems;

import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;

import gg.questnav.questnav.PoseFrame;
import gg.questnav.questnav.QuestNav;

public class QuestNavSubsystem {
    private final static QuestNav questNav = new QuestNav();
    private static boolean poseInit = false;
    private static boolean isTracking = false;
    private static Pose3d lastPose3d = new Pose3d();

    private static final Transform3d ROBOT_TO_QUEST = 
        new Transform3d(0.`13,0.0,0.5, new Rotation3d());
    public static void main(String[] args) {
        QuestNavSubsystem questNavSubsystem = new QuestNavSubsystem();
        questNavSubsystem.periodic();
    }


    public void periodic() {
        
        for (PoseFrame frame : questNav.getAllUnreadPoseFrames()) {
            
            if(poseInit == false){
                continue;
            }else {
                getPose(frame);
            }
        }
    }
    public void getPose(PoseFrame frame){
        if (frame.isTracking()) {
            if (frame.questPose3d() != null) {
                Pose3d robotPose = frame.questPose3d()
                    .transformBy(ROBOT_TO_QUEST.inverse());
                lastPose3d = robotPose;
            }
            else {
                System.out.println("pose is null :==(");
                if(lastPose3d != null){
                    System.out.println("last pose is: " + lastPose3d.toString());
                    Pose3d robotPose = lastPose3d;
                }
            }
        }
        else if (!frame.isTracking()) {
            // Handle the case when tracking is lost
            System.out.println("tracking is lost :(");
        }
        
    }

}
