    // The D-pad drives the elevator: down stows it, right is the mid goal,
    // up is the high one. Each finishes on arrival and the firmware holds.
    m_driverController.povDown().onTrue(m_elevator.goToHeight(ElevatorConstants.kStowed));
    m_driverController.povRight().onTrue(m_elevator.goToHeight(ElevatorConstants.kScoreMid));
    m_driverController.povUp().onTrue(m_elevator.goToHeight(ElevatorConstants.kScoreHigh));