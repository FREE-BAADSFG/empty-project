package org.frogforce503.robot2025.subsystems.arm;

import org.littletonrobotics.junction.AutoLog;

public interface ArmIO {

   
    public static record ArmIOData(
            double positionRadians,
            double velocityRadPerSec,
            double appliedVolts,
            double outputCurrent,
            boolean isForwardLimitSwitchClosed,
            boolean isReverseLimitSwitchClosed,
            double[] tempCelcius
    ) {}

   
    @AutoLog
    public static class ArmIOInputs {
        public ArmIOData data = new ArmIOData(0.0, 0.0, 0.0, 0.0, false, false, new double[] {});
    }

    public double getPosition();

    public default void updateInputs(ArmIOInputs inputs) {}

    public default void setVoltage(double volts) {}
    
    public default void setBrakeMode(boolean brake) {}

    
    public default void setPosition(double positionRadians) {}

   
    public default void setVelocity(double velocityRadPerSec) {}

  
    public default void setEncoderPosition(double positionRadians) {}

    
    public default void setPositionPID(double p, double i, double d, double f) {}

   
    public default void setVelocityPID(double p, double i, double d, double f) {}

   
    public default void stop() {
        setVoltage(0.0);
    }
}