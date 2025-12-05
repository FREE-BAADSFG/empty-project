package org.frogforce503.robot2025.auto;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import org.frogforce503.robot2025.RobotContainer;
import org.frogforce503.robot2025.RobotStatus;
import org.frogforce503.robot2025.RobotStatus.AllianceColor;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedDashboardBoolean;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;
import org.littletonrobotics.junction.networktables.LoggedDashboardString;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class AutoChooser {
    private LoggedDashboardChooser<StartingLocation> startingSideSelector;
    private LoggedDashboardChooser<AllianceColor> colorSelector;

    private LoggedDashboardBoolean commitAuton;
    private LoggedDashboardString selectedAutoNameDisplay;
    private LoggedDashboardBoolean autoReadyDisplay;

    Command selectedAutoCommand;
    Runnable onReset;
    String selectedAutoName = "NO AUTO SELECTED";

    AllianceColor lastAllianceColor = null;
    StartingLocation lastStartingSide = null;
    String lastRoutine = "";

    public AutoChooser() {
        
    }

    private void createAuto() {
    }

    public void startAuto() {
        if (this.selectedAutoCommand != null) {
            this.selectedAutoCommand.schedule();
        }
    }

    public void cleanup() {

    }

    public void onReset(Runnable onReset) {
        this.onReset = onReset;
    }

    private void reset() {
        
    }

    private void drawPathOnField() {
        
    }

    public enum StartingLocation {
        LEFT,
        CENTER,
        RIGHT;
    }
}