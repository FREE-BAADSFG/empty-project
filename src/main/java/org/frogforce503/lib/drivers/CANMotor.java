package org.frogforce503.lib.drivers;

import org.frogforce503.lib.drivers.BaseMotorWrapper.ConversionFactorType;
import org.frogforce503.lib.drivers.CTRE.TalonFXWrapper;
import org.frogforce503.lib.drivers.CTRE.TalonSRXWrapper;
import org.frogforce503.lib.drivers.REV.SparkFlexWrapper;
import org.frogforce503.lib.drivers.REV.SparkMaxWrapper;

import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

public class CANMotor {

    private final CANMotorType motorType;
    private final int CAN_ID;

    private BaseMotorWrapper motor;

    public Sim sim;

    private double lastSetpoint = 503.6328;
    private double lastFF = 503.6328;
    private MotorControlMode lastControlMode = null;

    private static class Sim {
        private double timeForOne; // number of seconds to complete a full revolution
        private double timeConstantFactor; // MOTOR IS MODELED AS LOGARITHMIC GROWTH, LIKE A CHARGING CAPACITOR
        private double CPR;

        private Setpoint lastCommand;
        private double lastUpdateTime;
        private double lastPosition = 0;
        
        private double currentPosition = 0;
        private double currentVelocity = 0;

        public Sim(double timeConstantFactor, double CPR) {
            this.timeConstantFactor = timeConstantFactor;
            this.timeForOne = Math.sqrt(timeConstantFactor); // 5 * (1 / timeConstantFactor); // https://www.electronics-tutorials.ws/rc/rc_1.html
            this.CPR = CPR;

            this.lastCommand = new Setpoint(Timer.getFPGATimestamp(), MotorControlMode.PercentOutput, 0, 0);
            this.lastUpdateTime = Timer.getFPGATimestamp();
        }

        public void set(MotorControlMode controlMode, double value) {
            if (controlMode == MotorControlMode.Position || controlMode == MotorControlMode.ProfiledPosition)
                value /= CPR;
            
            else if (controlMode == MotorControlMode.Velocity)
                value *= 60;

            lastCommand = new Setpoint(Timer.getFPGATimestamp(), controlMode, value, this.currentPosition);
        }

        public void update() {
            // updates speed and position
            double time = Timer.getFPGATimestamp();

            switch(lastCommand.controlMode) {
                case PercentOutput: {
                    this.currentVelocity = (1 / this.timeForOne) * this.lastCommand.demand; // basically, 1.0 power is max velocity
                    this.currentPosition += (time - this.lastUpdateTime) * this.currentVelocity;
                    break;
                }
                case Voltage: {
                    this.currentVelocity = (1 / this.timeForOne) * this.lastCommand.demand / 12.0; // basically, 12 Volts is max velocity
                    this.currentPosition += (time - this.lastUpdateTime) * this.currentVelocity;
                    break;
                }
                case ProfiledVelocity:
                case Velocity: {
                    this.currentVelocity = this.lastCommand.demand;
                    this.currentPosition += (time - this.lastUpdateTime) * this.currentVelocity;
                    break;
                }
                case ProfiledPosition:
                case Position: {
                    // https://www.desmos.com/calculator/euno0nwy5h
                    double t = time - this.lastCommand.startTimestamp;

                    if (Math.abs(lastCommand.demand - this.currentPosition) < 0.01) {
                        this.currentPosition = lastCommand.demand;
                        break;
                    }

                    double a = this.lastCommand.demand - this.lastCommand.initialPosition;
                    double b = Math.abs(a) * (1 / this.timeConstantFactor);

                    double e = Math.pow(Math.E, -t/b);

                    double r = (Math.abs(a) * (1 - e));

                    this.currentPosition = (a < 0 ? -r : r) + this.lastCommand.initialPosition;
                    this.currentVelocity = (this.currentPosition - this.lastPosition) - (time - this.lastUpdateTime);

                    break;
                }
            }

            this.lastPosition = this.currentPosition;
            this.lastUpdateTime = time;
        }

        public double getPosition() {
            return (this.currentPosition * this.CPR);
        }

        public double getVelocity() {
            return (this.currentVelocity * this.CPR) / 60;
        }

        public void setEncoderPosition(double position) {
            this.currentPosition = position / this.CPR;
        }

        private class Setpoint {
            public final double startTimestamp;
            public final MotorControlMode controlMode;
            public final double demand;
            public final double initialPosition;

            public final double journeyDuration;

            public Setpoint(double t, MotorControlMode m, double d, double i) {
                this.startTimestamp = t;
                this.controlMode = m;
                this.demand = d;
                this.initialPosition = i;

                if (this.controlMode == MotorControlMode.Position) {
                    this.journeyDuration = timeForOne * (d - i);
                } else {
                    this.journeyDuration = 0;
                }
            }
        }
    }

    private CANMotor(CANMotorType type, int CAN_ID, String canBus, MotorType revMotorType, boolean hasExternalEncoder) {
        this.motorType = type;
        this.CAN_ID = CAN_ID;

        if (this.motorType == CANMotorType.SPARK_MAX) {
            motor = new SparkMaxWrapper(CAN_ID, revMotorType, hasExternalEncoder);
        } else if (this.motorType == CANMotorType.SPARK_FLEX) {
            motor = new SparkFlexWrapper(CAN_ID, revMotorType, hasExternalEncoder);
        } else if (this.motorType == CANMotorType.TALON_FX) {
            motor = canBus.equals("") ? new TalonFXWrapper(this.CAN_ID) : new TalonFXWrapper(this.CAN_ID, canBus);
        } else {
            motor = new TalonSRXWrapper(this.CAN_ID);
        }
    }

    /**
     * configure parameters for motor simulation
     * @param timeConstantFactor technically wrong in terms of physics, but just know that bigger = faster
     * @param CPR counts per rotation of the motor, this is the scale at which the thing will move
     */
    public void configureSim(double timeConstantFactor, double CPR) {
        this.sim = new Sim(timeConstantFactor, CPR);
    }

    // Factory Methods
    public static CANMotor createSparkMax(int id, MotorType revMotorType, boolean hasExternalEncoder) {
        return new CANMotor(CANMotorType.SPARK_MAX, id, null, revMotorType, hasExternalEncoder);
    }

    public static CANMotor createSparkMax(int id, MotorType revMotorType) {
        return CANMotor.createSparkMax(id, revMotorType, false);
    }

    public static CANMotor createSparkFlex(int id, MotorType revMotorType, boolean hasExternalEncoder) {
        return new CANMotor(CANMotorType.SPARK_FLEX, id, null, revMotorType, hasExternalEncoder);
    }

    public static CANMotor createSparkFlex(int id, MotorType revMotorType) {
        return CANMotor.createSparkFlex(id, revMotorType, false);
    }

    public static CANMotor createTalonFX(int id, String canBus) {
        return new CANMotor(CANMotorType.TALON_FX, id, canBus, null, false);
    }

    public static CANMotor createTalonFX(int id) {
        return CANMotor.createTalonFX(id, "");
    }

    public static CANMotor createTalonSRX(int id) {
        return new CANMotor(CANMotorType.TALON_SRX, id, null, null, false);
    }

    public SparkMaxWrapper getSparkMax() {
        return (SparkMaxWrapper) motor;
    }

    public SparkFlexWrapper getSparkFlex() {
        return (SparkFlexWrapper) motor;
    }

    public TalonFXWrapper getTalonFX() {
        return (TalonFXWrapper) motor;
    }

    public TalonSRXWrapper getTalonSRX() {
        return (TalonSRXWrapper) motor;
    }

    public String getIdleMode() {
        return motor.getIdleMode();
    }

    public double getOutputCurrent() {
        return motor.getOutputCurrent();
    }

    public double getTemperature() {
        return motor.getTemperature();
    }

    public void selectProfileSlot(int slotID) {
        motor.selectProfileSlot(slotID);
    }

    public int getSelectedProfileSlot() {
        return motor.getSelectedProfileSlot();
    }

    public void setPID(int slot, double p, double i, double d) {
        setPIDF(slot, p, i, d, 0.0);
    }

    public void setPIDF(int slot, double p, double i, double d, double f) {
        motor.setPIDF(slot, p, i, d, f);
        noResetButUpdateConfig();
    }

    public double getP(int slot) {
        return motor.getP(slot);
    }

    public double getP() {
        return getP(0);
    }

    public double getI(int slot) {
        return motor.getI(slot);
    }

    public double getI() {
        return getI(0);
    }

    public double getD(int slot) {
        return motor.getD(slot);
    }

    public double getD() {
        return getD(0);
    }

    public double getF(int slot) {
        return motor.getF(slot);
    }

    public double getF() {
        return getF(0);
    }

    public void configureProfiled(int slotID, double maxVel, double maxAcc, double tol) {
        motor.configureProfiled(slotID, maxVel, maxAcc, tol);
    }

    public void setConversionFactor(double factor, ConversionFactorType type) {
        motor.setConversionFactor(factor, type);
    }

    public void applyMotorConfig(boolean shouldFactoryDefault, boolean shouldBurnConfig) {
        motor.applyMotorConfig(shouldFactoryDefault, shouldBurnConfig);
    }

    public void resetAndUpdateConfig() {
        applyMotorConfig(true, true);
    }

    public void noResetButUpdateConfig() {
        applyMotorConfig(false, true);
    }

    public void noResetAndNoUpdateConfig() {
        applyMotorConfig(false, false);
    }

    public void set(MotorControlMode mode, double value, double arbFF) {

        if (mode == this.lastControlMode && value == this.lastSetpoint && arbFF == this.lastFF)
            return;

        if (RobotBase.isSimulation()) {
            this.sim.set(mode, value);
        }

        motor.set(mode, value, arbFF);

        this.lastSetpoint = value;
        this.lastControlMode = mode;
        this.lastFF = arbFF;
    }

    public void set(MotorControlMode mode, double value) {
        this.set(mode, value, 0);
    }

    public boolean withinVelocityTolerance(double tol) {
        if (this.lastSetpoint == 503.6328)
            return false;

        return Math.abs(this.getVelocity() - this.lastSetpoint) < tol;
    }

    public boolean withinTolerance(double tol) {
        if (this.lastSetpoint == 503.6328)
            return false;

        return Math.abs(this.getPosition() - this.lastSetpoint) < tol;
    }

    public void update() {
        if (RobotBase.isSimulation())
            this.sim.update();
    }

    public void setIdleMode(boolean shouldCoast) {
        motor.setIdleMode(shouldCoast);
        noResetAndNoUpdateConfig();
    }

    public void setInverted(boolean set) {
        motor.setMotorInverted(set);
    }

    public void setEncoderPosition(double position) {
        motor.setEncoderPosition(position);
    }
    
    public double getPercent() {
        return motor.getMotorPercent();
    }

    public double getPosition() {
        if (RobotBase.isSimulation())
            return sim.getPosition();
        
        return motor.getMotorPosition();
    }

    public double getVelocity() {
        if (RobotBase.isSimulation())
            return sim.getVelocity();
        
        return motor.getMotorVelocity();
    }

    public double getOutputFromMode(MotorControlMode m) {
        switch (m) {
            case PercentOutput:
                return getPercent();
            case ProfiledPosition:
            case Position:
                return getPosition();
            case ProfiledVelocity:
            case Velocity:
                return getVelocity();
            default:
                return getPercent();
        }
    }
    
    public enum CANMotorType {
        SPARK_MAX,
        SPARK_FLEX,
        TALON_FX,
        TALON_SRX
    }

    public enum MotorControlMode {
        PercentOutput,
        Voltage,
        Position,
        Velocity,
        ProfiledPosition,
        ProfiledVelocity
    }

    public static class MotorSetpoint {
        public MotorControlMode controlMode;
        public double output;

        public MotorSetpoint(MotorControlMode controlMode, double output) {
            this.controlMode = controlMode;
            this.output = output;
        }

        public MotorSetpoint() {
            this(MotorControlMode.PercentOutput, 0);
        }
    }
}