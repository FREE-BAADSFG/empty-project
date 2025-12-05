package org.frogforce503.lib.subsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/** Thin wrapper around WPILib's {@link FFSubsystemBase} class to create command-based subsystems with common FF boilerplate methods */
public abstract class FFSubsystemBase extends SubsystemBase {
    @Override
    public abstract void periodic();

    public abstract boolean atGoal();

    public abstract Command off();
    public abstract Command idle();
    public abstract Command setToPreset();
}