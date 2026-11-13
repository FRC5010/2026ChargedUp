package first.robot.subsystems;



import org.littletonrobotics.junction.Logger;

import org.wpilib.command3.Scheduler;
import org.wpilib.driverstation.DriverStation;
import org.wpilib.math.estimator.SwerveDrivePoseEstimator;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.geometry.Translation3d;
import org.wpilib.math.linalg.Matrix;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.numbers.N1;
import org.wpilib.math.numbers.N3;
import org.wpilib.telemetry.Telemetry;

import gg.questnav.questnav.PoseFrame;
import gg.questnav.questnav.QuestNav;

import org.wpilib.fields.Field;
import org.wpilib.fields.Fields;

public class QuestNavSubsystem implements QuestNavIO, PoseProvider {
    private QuestNav questNav;
    private QuestNavIOInputsAutoLogged questNavInputs = new QuestNavIOInputsAutoLogged();

    private static final Field FIELD_LAYOUT = new Field().loadField(
            Fields.DEFAULT_FIELD);

    private final Drivetrain drivetrain;
    private VisionPoseProvider visionPoseProvider;
    private final Matrix<N3, N1> QUESTNAV_STD_DEV = VecBuilder.fill(
            0.01, // 10 mm x positions trust
            0.01, // 10 mm y positions trust
            0.01 // rotation
    );

    private boolean shouldReject(Pose3d pose) {
        return pose.getX() < 0.0
                || pose.getX() > FIELD_LAYOUT.getFieldLength()
                || pose.getY() < 0.0
                || pose.getY() > FIELD_LAYOUT.getFieldWidth();
    }

    public QuestNavSubsystem(Drivetrain drivetrain) {
        this.questNav = new QuestNav();
        this.drivetrain = drivetrain;

        questNav.onConnected(() -> System.out.println("Quest is Connected!!!"));
        questNav.onDisconnected(() -> System.out.println("Quest is Disconnected!!!"));
        questNav.onTrackingAcquired(() -> System.out.println("Quest is Tracking!!!"));
        questNav.onTrackingLost(() -> System.out.println("Quest is Lost!!!"));

        Scheduler.getDefault().addPeriodic(this::periodic);
    }

    Transform3d RobotToQuest = new Transform3d(
            new Translation3d(0.0, 0.0, 0.0), // Translation from robot to QuestNav
            new Rotation3d(0.0, 0.0, 0.0) // Rotation from robot to QuestNav
    );

    PoseFrame[] poseFrames = questNav.getAllUnreadPoseFrames();

    static Pose3d robotPose = new Pose3d(0.0, 0.0, 0.0, new Rotation3d(0.0, 0.0, 0.0));

    Pose3d questPose = robotPose.transformBy(RobotToQuest);

    boolean poseInit = false;

    public void updatePose() {
        poseFrames = questNav.getAllUnreadPoseFrames();
    }

    public void transformPose() {
        if (poseFrames.length > 0) {
            questPose = poseFrames[poseFrames.length - 1].questPose3d();
            robotPose = questPose.transformBy(RobotToQuest.inverse());

        }
    }

    public static Pose3d getRobotPose() {
        return robotPose;
    }

    public Pose3d getQuestPose() {
        return questPose;
    }

    public void resetPose(Pose3d robotPose) {
        Pose3d questPose = robotPose.plus(RobotToQuest);
        questNav.setPose(questPose);
    }

    @Override
    public void updateInputs(QuestNavIOInputs inputs) {
        inputs.connected = questNav.isConnected();
        inputs.tracking = questNav.isTracking();
        inputs.batteryPercent = questNav.getBatteryPercent().orElse(0);
        inputs.trackingLostCounter = questNav.getTrackingLostCounter().orElse(0);
    }
    @Override
    public void updatePoseEstimate(SwerveDrivePoseEstimator estimator){
        for(int i = 0; i < questNavInputs.poseObservations.length; i++){
        estimator.addVisionMeasurement(questNavInputs.poseObservations[i].toPose2d(), questNavInputs.latency[i], QUESTNAV_STD_DEV);

        }
    }

    public void periodic() {

        questNav.commandPeriodic();
        PoseFrame[] questFrames = questNav.getAllUnreadPoseFrames();
        questNavInputs.poseObservations = new Pose3d[questFrames.length];
        questNavInputs.latency = new double[questFrames.length];

        updateInputs(questNavInputs);
        int i = 0;
        for (PoseFrame questFrame : questFrames) {
            if (!questFrame.isTracking()) {
                continue;
            }

            Pose3d questPose = questFrame.questPose3d();
            double timeStamp = questFrame.dataTimestamp();

            Pose3d robotPose = questPose.transformBy(RobotToQuest.inverse());

            if (shouldReject(robotPose)) {
                continue;
            }

            drivetrain.setQuestNavePose(robotPose.toPose2d());

            if (!poseInit) {
                drivetrain.resetPoseFromQuestNav(robotPose.toPose2d());
                poseInit = true;
            } else {
               questNavInputs.poseObservations[i] = robotPose;
               questNavInputs.latency[i] = timeStamp;
               
            }
            i++;
        }
        Logger.processInputs("QuestNav", questNavInputs);
    }

}
