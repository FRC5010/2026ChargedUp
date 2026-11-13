package first.robot.subsystems;

import java.util.OptionalInt;

import org.littletonrobotics.junction.AutoLog;

import gg.questnav.questnav.QuestNav;

public interface QuestNavIO {
    public static final QuestNav questNav = new QuestNav();
    @AutoLog
    public static class QuestNavIOInputs {
        public boolean connected;
        public boolean tracking;
        public double latency;
        public int batteryPercent;
        public int trackingLostCounter;

    }

    public void updateInputs(QuestNavIOInputs inputs);

}
