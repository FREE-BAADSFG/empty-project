package org.frogforce503.robot2025;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import org.frogforce503.robot2025.auto.AutoChooser;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class RobotContainer{
  // Controllers
  public static final CommandXboxController driver = new CommandXboxController(0);
  public static final CommandXboxController operator = new CommandXboxController(1);

  // Needs
  public static final AutoChooser autoChooser = new AutoChooser();
  
  // Other Hardware
  // public static final PowerDistribution powerDistribution = new PowerDistribution();

  public static void init() {
    configureButtonBindings();
  }

  private static void configureButtonBindings() {
   
  }
}