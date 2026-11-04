package first.robot.subsystems;

import org.wpilib.hardware.led.LEDPattern;

public enum SuperstructureState {
    UNHOMED(SuperstructureData.kIdle),
    IDLE,
    GROUNDCONEINTAKING,
    //GROUNDCUBEINTAKING,
    //HIGHCONEINTAKING,
    //HIGHCUBEINTAKING,
    HOLDINGCONE,
    //CUBESCORELOW,
    //CUBESCOREHIGH,
    //CUBESCOREMID,
    CONESCORELOW;
    //CONESCOREHIGH,
    //CONESCOREMID;

    private final SuperstructureData m_SuperstructureData;

    SuperstructureState(SuperstructureData stateParameters) {
        m_SuperstructureData = stateParameters;
    }

    public boolean canGoTo(SuperstructureState next) {
        return switch (this) {
            case UNHOMED -> false;
            case IDLE -> next == GROUNDCONEINTAKING;
            case GROUNDCONEINTAKING -> next == HOLDINGCONE || next == IDLE;
            case HOLDINGCONE -> next == CONESCORELOW || next == IDLE;
            case CONESCORELOW -> next == IDLE;
        };
    }
}

//current plan:
/*

what im already thinking is to make a data object that stores "paramaters" that are passed in through the robot's
different subsystems

then ill assign each enum type to an instance of the object that is specific to that state
like:
IDLE = new robotState(armAngle: #, armExtensionL);

*/
