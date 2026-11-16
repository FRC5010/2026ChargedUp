package first.robot.subsystems;

public enum SuperstructureState {
    UNHOMED(SuperstructureData.kUnhomed),
    IDLE(SuperstructureData.kIdle),
    GROUNDCONEINTAKING(SuperstructureData.kGroundConeIntaking),
    //GROUNDCUBEINTAKING,
    //HIGHCONEINTAKING,
    //HIGHCUBEINTAKING,
    HOLDINGCONE(SuperstructureData.kHoldingCone),
    //CUBESCORELOW,
    //CUBESCOREHIGH,
    //CUBESCOREMID,
    CONESCORELOW(SuperstructureData.kConeScoreLow);
    //CONESCOREHIGH,
    //CONESCOREMID;

    private final SuperstructureData m_SuperstructureData;

    SuperstructureState(SuperstructureData stateParameters) {
        m_SuperstructureData = stateParameters;
    }

    public SuperstructureData state() {
        return m_SuperstructureData;
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

as of 11/6/26 i am done with the data object part!

*/
