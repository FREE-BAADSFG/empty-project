package org.frogforce503.robot2025.subsystems.arm;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import org.frogforce503.robot2025.subsystems.arm.ArmIO.ArmIOData;
import org.frogforce503.robot2025.subsystems.arm.ArmIO.ArmIOInputs;


public class ArmIOSpark<CANSparkMax, SparkMaxPIDController> implements ArmIO {
    


   
    private static final double GEAR_RATIO_TO_ROTATIONS = 100.0 / (2.0 * Math.PI); 
    private static final int CURRENT_LIMIT_AMPS = 40;
    private static final int ARM_MOTOR_ID = 5;

    private final CANSparkMax motor;
    private final RelativeEncoder encoder;
    private final SparkMaxPIDController pidController;

 
    public ArmIOSpark() {
       
        motor = new CANSparkMax(ARM_MOTOR_ID, CANSparkMax.MotorType.kBrushless);
        motor.restoreFactoryDefaults();
        
       
        encoder = motor.getEncoder();
        
        
        encoder.setPositionConversionFactor(GEAR_RATIO_TO_ROTATIONS);
        encoder.setVelocityConversionFactor(GEAR_RATIO_TO_ROTATIONS / 60.0); 

    
        pidController = motor.getPIDController();
        pidController.setFeedbackDevice(encoder);

       
        motor.setInverted(false); 
        motor.setSmartCurrentLimit(CURRENT_LIMIT_AMPS);
        motor.setIdleMode(com.revrobotics.spark.config.SparkBaseConfig.IdleMode.kCoast);
        motor.burnFlash();
    }

   
    @Override
    public void updateInputs(ArmIOInputs inputs) {
        
        inputs.data = new ArmIOData(
            encoder.getPosition(), 
            encoder.getVelocity(), 
            motor.getAppliedOutput() * motor.getBusVoltage(), 
            motor.getOutputCurrent(),
            motor.getForwardLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type.kNormallyOpen).isPressed(),
            motor.getReverseLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type.kNormallyOpen).isPressed(),
            new double[] {motor.getMotorTemperature()}
        );
    }


    @Override
    public void setVoltage(double volts, Object ControlType) {
            ((Object) pidController).setReference(volts, ControlType);
    }

    @Override
    public void setBrakeMode(boolean brake) {
        motor.setIdleMode(brake ? IdleMode.kBrake : IdleMode.kCoast);
    }

    @Override
    public void setPosition(double positionRadians) {
        ((Object) pidController).setReference(positionRadians, com.revrobotics.spark.SparkBase.ControlType.kPosition, 0); 
    }

    @Override
    public void setVelocity(double velocityRadPerSec) {
        ((Object) pidController).setReference(velocityRadPerSec, com.revrobotics.spark.SparkBase.ControlType.kVelocity, 1);
    }

    @Override
    public void setEncoderPosition(double positionRadians) {
        encoder.setPosition(positionRadians);
    }

    @Override
    public void setPositionPID(double p, double i, double d, double f) {
     
        pidController.setP(p, 0);
        pidController.setI(i, 0);
        pidController.setD(d, 0);
        pidController.setFF(f, 0);
        pidController.setOutputRange(-1.0, 1.0, 0);
        motor.burnFlash();
    }

    @Override
    public void setVelocityPID(double p, double i, double d, double f) {
        pidController.setP(p, 1);
        pidController.setI(i, 1);
        ((Object) pidController).setD(d, 1);
        ((Object) pidController).setFF(f, 1);
        ((Object) pidController).setOutputRange(-1.0, 1.0, 1);
        ((Object) motor).burnFlash();
    }


    @Override
    public double getPosition() {
      
        throw new UnsupportedOperationException("Unimplemented method 'getPosition'");
    }
}