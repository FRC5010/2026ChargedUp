package first.robot.subsystems;

import org.wpilib.math.estimator.SwerveDrivePoseEstimator;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.system.Timer;
import org.wpilib.telemetry.Telemetry;

import gg.questnav.questnav.PoseFrame;
import gg.questnav.questnav.QuestNav;

public class QuestNavSubsystem implements PoseProvider {
    private final static QuestNav questNav = new QuestNav();
    private static boolean poseInit = false;
    // private static boolean isTracking = false;
    // private static Pose3d lastPose3d = new Pose3d();

    private static final Transform3d ROBOT_TO_QUEST = 
        new Transform3d(0.13,0.0,0.5, new Rotation3d());
   



    public void updatePoseEstimate(SwerveDrivePoseEstimator estimator) {
        for (PoseFrame frame : questNav.getAllUnreadPoseFrames()){
            Pose3d robotPose = frame.questPose3d()
                    .transformBy(ROBOT_TO_QUEST.inverse());
                Pose2d lastPose2d = robotPose.toPose2d();
                double timestamp = frame.dataTimestamp();
            estimator.addVisionMeasurement(lastPose2d, timestamp);
            Telemetry.log("Localizer/VisionPose", lastPose2d, Pose2d.struct);
        

        }
    }


}
