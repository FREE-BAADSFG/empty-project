package org.frogforce503.robot2025.fields;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.frogforce503.lib.math.MathUtils;
import org.frogforce503.robot2025.RobotStatus;
import org.frogforce503.robot2025.RobotStatus.AllianceColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Filesystem;

public class FieldConfig {
    
    // // CONSTANTS - TODO: FIX THESE CONSTANTS
    // private double AlgaeDiameter = Units.inchesToMeters(16.25) + Units.inchesToMeters(0.0 / 4.0); // actually ± 1/4, but go with median for now (can remeasure)
    // private double DistBetweenBranchesOnSameFace = Units.inchesToMeters(16.25) + Units.inchesToMeters(0.0 / 4.0); // assume AlgaeDiameter for now

    // Field Variables
    public Translation2d FIELD_DIMENSIONS;
    private final double FIELD_X = Units.feetToMeters(57) + Units.inchesToMeters(6) + Units.inchesToMeters(7.0 / 8.0);
    private final double FIELD_Y = Units.feetToMeters(26) + Units.inchesToMeters(5);

    // Blue Measurements
    public double BlueWallToRightAlgae;
    public double BlueCenterAlgaeToRightAlgae;
    public double BlueCenterAlgaeToLeftAlgae;
    public double BlueCenterAlgaeToReefSide;

    public double BlueWallToReef_ALMiddle_X;
    public double BlueLeftHPWallToReefALMiddle_Y;

    public double BlueReefEdgeToBranch;
    public double BlueReefSideLength;
    public double BlueReefInnerToOuter;

    public double BlueLeftHPWallToLeftCage_Y;
    public double BlueInitLineToLeftCage;
    public double BlueCenterCageToLeftCage;
    public double BlueCenterCageToRightCage;

    public double BlueProcToInitLine;

    public double TAG_18_TO_LEFT;
    public double TAG_18_TO_RIGHT;

    public double TAG_17_TO_LEFT;
    public double TAG_17_TO_RIGHT;

    public double TAG_22_TO_LEFT;
    public double TAG_22_TO_RIGHT;

    public double TAG_21_TO_LEFT;
    public double TAG_21_TO_RIGHT;

    public double TAG_20_TO_LEFT;
    public double TAG_20_TO_RIGHT;

    public double TAG_19_TO_LEFT;
    public double TAG_19_TO_RIGHT;

    // Red Measurements
    public double RedWallToRightAlgae;
    public double RedCenterAlgaeToRightAlgae;
    public double RedCenterAlgaeToLeftAlgae;
    public double RedCenterAlgaeToReefSide;

    public double RedWallToReef_ALMiddle_X;
    public double RedLeftHPWallToReefALMiddle_Y;

    public double RedReefEdgeToBranch;
    public double RedReefSideLength;
    public double RedReefInnerToOuter;

    public double RedLeftHPWallToLeftCage_Y;
    public double RedInitLineToLeftCage;
    public double RedCenterCageToLeftCage;
    public double RedCenterCageToRightCage;

    public double RedProcToInitLine;

    public double TAG_7_TO_LEFT;
    public double TAG_7_TO_RIGHT;

    public double TAG_8_TO_LEFT;
    public double TAG_8_TO_RIGHT;

    public double TAG_9_TO_LEFT;
    public double TAG_9_TO_RIGHT;

    public double TAG_10_TO_LEFT;
    public double TAG_10_TO_RIGHT;

    public double TAG_11_TO_LEFT;
    public double TAG_11_TO_RIGHT;

    public double TAG_6_TO_LEFT;
    public double TAG_6_TO_RIGHT;

    // Blue Locations
    public Translation2d BlueLeftAlgae, BlueCenterAlgae, BlueRightAlgae;

    public Translation2d Blue_Coral_A, Blue_Coral_B, Blue_Coral_C, Blue_Coral_D, Blue_Coral_E, Blue_Coral_F,
                  Blue_Coral_G, Blue_Coral_H, Blue_Coral_I, Blue_Coral_J, Blue_Coral_K, Blue_Coral_L;

    public Translation2d Blue_Algae_AB, Blue_Algae_CD, Blue_Algae_EF, Blue_Algae_GH, Blue_Algae_IJ, Blue_Algae_KL;
    
    public Translation2d BlueProc;
    
    public Translation2d BlueLeftCage, BlueCenterCage, BlueRightCage;

    public double BlueInitLine;

    public Translation2d BlueLeftStation;
    public Translation2d BlueRightStation;

    // Red Locations
    public Translation2d RedLeftAlgae, RedCenterAlgae, RedRightAlgae;

    public Translation2d Red_Coral_A, Red_Coral_B, Red_Coral_C, Red_Coral_D, Red_Coral_E, Red_Coral_F,
                  Red_Coral_G, Red_Coral_H, Red_Coral_I, Red_Coral_J, Red_Coral_K, Red_Coral_L;

    public Translation2d Red_Algae_AB, Red_Algae_CD, Red_Algae_EF, Red_Algae_GH, Red_Algae_IJ, Red_Algae_KL;
    
    public Translation2d RedProc;
    
    public Translation2d RedLeftCage, RedCenterCage, RedRightCage;

    public double RedInitLine;

    public Translation2d RedLeftStation;
    public Translation2d RedRightStation;

    // Key positions used to calculate locations above
    // Blue
    private Translation2d BlueALMiddle;
    private Translation2d BlueBCMiddle;
    private Translation2d BlueDEMiddle;
    private Translation2d BlueFGMiddle;
    private Translation2d BlueHIMiddle;
    private Translation2d BlueJKMiddle;

    // Red
    private Translation2d RedALMiddle;
    private Translation2d RedBCMiddle;
    private Translation2d RedDEMiddle;
    private Translation2d RedFGMiddle;
    private Translation2d RedHIMiddle;
    private Translation2d RedJKMiddle;

    // AprilTag Layout on Field
    public AprilTagFieldLayout fieldLayout;

    private static FieldConfig instance;

    public static FieldConfig getInstance() {
        if (instance == null) { instance = new FieldConfig(); }
        return instance;
    }

    private void loadConstants(String file) throws FileNotFoundException, IOException, ParseException {
        JSONObject field = (JSONObject) new JSONParser().parse(new FileReader(
                Filesystem.getDeployDirectory().getAbsolutePath() + "/fields/" + file
        ));

        // Blue Measurements
        BlueWallToRightAlgae = Units.inchesToMeters(Double.parseDouble(field.get("BlueWallToRightAlgae").toString()));
        BlueCenterAlgaeToRightAlgae = Units.inchesToMeters(Double.parseDouble(field.get("BlueCenterAlgaeToRightAlgae").toString()));
        BlueCenterAlgaeToLeftAlgae = Units.inchesToMeters(Double.parseDouble(field.get("BlueCenterAlgaeToLeftAlgae").toString()));
        BlueCenterAlgaeToReefSide = Units.inchesToMeters(Double.parseDouble(field.get("BlueCenterAlgaeToReefSide").toString()));

        BlueWallToReef_ALMiddle_X = Units.inchesToMeters(Double.parseDouble(field.get("BlueWallToReef_ALMiddle_X").toString()));
        BlueLeftHPWallToReefALMiddle_Y = Units.inchesToMeters(Double.parseDouble(field.get("BlueLeftHPWallToReef_ALMiddle_Y").toString()));

        BlueReefEdgeToBranch = Units.inchesToMeters(Double.parseDouble(field.get("BlueReefEdgeToBranch").toString()));
        BlueReefSideLength = Units.inchesToMeters(Double.parseDouble(field.get("BlueReefSideLength").toString()));
        BlueReefInnerToOuter = Units.inchesToMeters(Double.parseDouble(field.get("BlueReefInnerToOuter").toString()));

        BlueLeftHPWallToLeftCage_Y = Units.inchesToMeters(Double.parseDouble(field.get("BlueLeftHPWallToLeftCage_Y").toString()));
        BlueInitLineToLeftCage = Units.inchesToMeters(Double.parseDouble(field.get("BlueInitLineToLeftCage").toString()));
        BlueCenterCageToLeftCage = Units.inchesToMeters(Double.parseDouble(field.get("BlueCenterCageToLeftCage").toString()));
        BlueCenterCageToRightCage = Units.inchesToMeters(Double.parseDouble(field.get("BlueCenterCageToRightCage").toString()));

        BlueProcToInitLine = Units.inchesToMeters(Double.parseDouble(field.get("BlueProcToInitLine").toString()));

        TAG_18_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_18_TO_LEFT").toString()));
        TAG_18_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_18_TO_RIGHT").toString()));

        TAG_17_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_17_TO_LEFT").toString()));
        TAG_17_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_17_TO_RIGHT").toString()));

        TAG_22_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_22_TO_LEFT").toString()));
        TAG_22_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_22_TO_RIGHT").toString()));

        TAG_21_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_21_TO_LEFT").toString()));
        TAG_21_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_21_TO_RIGHT").toString()));

        TAG_20_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_20_TO_LEFT").toString()));
        TAG_20_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_20_TO_RIGHT").toString()));

        TAG_19_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_19_TO_LEFT").toString()));
        TAG_19_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_19_TO_RIGHT").toString()));

        // Red Measurements
        RedWallToRightAlgae = Units.inchesToMeters(Double.parseDouble(field.get("RedWallToRightAlgae").toString()));
        RedCenterAlgaeToRightAlgae = Units.inchesToMeters(Double.parseDouble(field.get("RedCenterAlgaeToRightAlgae").toString()));
        RedCenterAlgaeToLeftAlgae = Units.inchesToMeters(Double.parseDouble(field.get("RedCenterAlgaeToLeftAlgae").toString()));
        RedCenterAlgaeToReefSide = Units.inchesToMeters(Double.parseDouble(field.get("RedCenterAlgaeToReefSide").toString()));

        RedWallToReef_ALMiddle_X = Units.inchesToMeters(Double.parseDouble(field.get("RedWallToReef_ALMiddle_X").toString()));
        RedLeftHPWallToReefALMiddle_Y = Units.inchesToMeters(Double.parseDouble(field.get("RedLeftHPWallToReef_ALMiddle_Y").toString()));

        RedReefEdgeToBranch = Units.inchesToMeters(Double.parseDouble(field.get("RedReefEdgeToBranch").toString()));
        RedReefSideLength = Units.inchesToMeters(Double.parseDouble(field.get("RedReefSideLength").toString()));
        RedReefInnerToOuter = Units.inchesToMeters(Double.parseDouble(field.get("RedReefInnerToOuter").toString()));

        RedLeftHPWallToLeftCage_Y = Units.inchesToMeters(Double.parseDouble(field.get("RedLeftHPWallToLeftCage_Y").toString()));
        RedInitLineToLeftCage = Units.inchesToMeters(Double.parseDouble(field.get("RedInitLineToLeftCage").toString()));
        RedCenterCageToLeftCage = Units.inchesToMeters(Double.parseDouble(field.get("RedCenterCageToLeftCage").toString()));
        RedCenterCageToRightCage = Units.inchesToMeters(Double.parseDouble(field.get("RedCenterCageToRightCage").toString()));

        RedProcToInitLine = Units.inchesToMeters(Double.parseDouble(field.get("RedProcToInitLine").toString()));

        TAG_7_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_7_TO_LEFT").toString()));
        TAG_7_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_7_TO_RIGHT").toString()));

        TAG_8_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_8_TO_LEFT").toString()));
        TAG_8_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_8_TO_RIGHT").toString()));

        TAG_9_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_9_TO_LEFT").toString()));
        TAG_9_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_9_TO_RIGHT").toString()));

        TAG_10_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_10_TO_LEFT").toString()));
        TAG_10_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_10_TO_RIGHT").toString()));

        TAG_11_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_11_TO_LEFT").toString()));
        TAG_11_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_11_TO_RIGHT").toString()));

        TAG_6_TO_LEFT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_6_TO_LEFT").toString()));
        TAG_6_TO_RIGHT = Units.inchesToMeters(Double.parseDouble(field.get("TAG_6_TO_RIGHT").toString()));

        // Blue Locations
        BlueALMiddle = new Translation2d(BlueWallToReef_ALMiddle_X, FIELD_Y - BlueLeftHPWallToReefALMiddle_Y);
        BlueBCMiddle = BlueALMiddle.plus(new Translation2d(BlueReefSideLength, Rotation2d.fromDegrees(270)));
        BlueDEMiddle = BlueBCMiddle.plus(new Translation2d(BlueReefSideLength, Rotation2d.fromDegrees(330)));
        BlueFGMiddle = BlueDEMiddle.plus(new Translation2d(BlueReefSideLength, Rotation2d.fromDegrees(30)));
        BlueHIMiddle = BlueFGMiddle.plus(new Translation2d(BlueReefSideLength, Rotation2d.fromDegrees(90)));
        BlueJKMiddle = BlueHIMiddle.plus(new Translation2d(BlueReefSideLength, Rotation2d.fromDegrees(150)));

        Blue_Coral_A = BlueALMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(270)));
        Blue_Coral_B = BlueBCMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(90)));
        Blue_Coral_C = BlueBCMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(330)));
        Blue_Coral_D = BlueDEMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(150)));
        Blue_Coral_E = BlueDEMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(30)));
        Blue_Coral_F = BlueFGMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(210)));
        Blue_Coral_G = BlueFGMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(90)));
        Blue_Coral_H = BlueHIMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(270)));
        Blue_Coral_I = BlueHIMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(150)));
        Blue_Coral_J = BlueJKMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(330)));
        Blue_Coral_K = BlueJKMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(210)));
        Blue_Coral_L = BlueALMiddle.plus(new Translation2d(BlueReefEdgeToBranch, Rotation2d.fromDegrees(30)));

        Blue_Algae_AB = BlueALMiddle.plus(new Translation2d(BlueReefSideLength / 2, Rotation2d.fromDegrees(270)));
        Blue_Algae_CD = BlueBCMiddle.plus(new Translation2d(BlueReefSideLength / 2, Rotation2d.fromDegrees(330)));
        Blue_Algae_EF = BlueDEMiddle.plus(new Translation2d(BlueReefSideLength / 2, Rotation2d.fromDegrees(30)));
        Blue_Algae_GH = BlueFGMiddle.plus(new Translation2d(BlueReefSideLength / 2, Rotation2d.fromDegrees(90)));
        Blue_Algae_IJ = BlueHIMiddle.plus(new Translation2d(BlueReefSideLength / 2, Rotation2d.fromDegrees(150)));
        Blue_Algae_KL = BlueJKMiddle.plus(new Translation2d(BlueReefSideLength / 2, Rotation2d.fromDegrees(210)));

        BlueCenterAlgae = Blue_Algae_AB.plus(new Translation2d(-BlueCenterAlgaeToReefSide, 0));
        BlueLeftAlgae = BlueCenterAlgae.plus(new Translation2d(0, BlueCenterAlgaeToLeftAlgae));
        BlueRightAlgae = BlueCenterAlgae.plus(new Translation2d(0, -BlueCenterAlgaeToRightAlgae));

        BlueInitLine = (FIELD_X / 2) - BlueInitLineToLeftCage;

        BlueProc = new Translation2d(BlueInitLine - BlueProcToInitLine, 0);
        
        BlueLeftCage = new Translation2d(BlueInitLine + BlueInitLineToLeftCage, FIELD_Y - BlueLeftHPWallToLeftCage_Y);
        BlueCenterCage = BlueLeftCage.plus(new Translation2d(0, -BlueCenterCageToLeftCage));
        BlueRightCage = BlueCenterCage.plus(new Translation2d(0, -BlueCenterCageToRightCage));

        BlueLeftStation = new Translation2d(Units.inchesToMeters(65)/2, FIELD_Y - Units.inchesToMeters(47)/2);
        BlueRightStation = new Translation2d(Units.inchesToMeters(65)/2, Units.inchesToMeters(47)/2);

        // Red Locations
        RedALMiddle = new Translation2d(FIELD_X - RedWallToReef_ALMiddle_X, RedLeftHPWallToReefALMiddle_Y);
        RedBCMiddle = RedALMiddle.plus(new Translation2d(RedReefSideLength, Rotation2d.fromDegrees(270 + 180)));
        RedDEMiddle = RedBCMiddle.plus(new Translation2d(RedReefSideLength, Rotation2d.fromDegrees(330 + 180)));
        RedFGMiddle = RedDEMiddle.plus(new Translation2d(RedReefSideLength, Rotation2d.fromDegrees(30 + 180)));
        RedHIMiddle = RedFGMiddle.plus(new Translation2d(RedReefSideLength, Rotation2d.fromDegrees(90 + 180)));
        RedJKMiddle = RedHIMiddle.plus(new Translation2d(RedReefSideLength, Rotation2d.fromDegrees(150 + 180)));

        Red_Coral_A = RedALMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(270 + 180)));
        Red_Coral_B = RedBCMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(90 + 180)));
        Red_Coral_C = RedBCMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(330 + 180)));
        Red_Coral_D = RedDEMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(150 + 180)));
        Red_Coral_E = RedDEMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(30 + 180)));
        Red_Coral_F = RedFGMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(210 + 180)));
        Red_Coral_G = RedFGMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(90 + 180)));
        Red_Coral_H = RedHIMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(270 + 180)));
        Red_Coral_I = RedHIMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(150 + 180)));
        Red_Coral_J = RedJKMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(330 + 180)));
        Red_Coral_K = RedJKMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(210 + 180)));
        Red_Coral_L = RedALMiddle.plus(new Translation2d(RedReefEdgeToBranch, Rotation2d.fromDegrees(30 + 180)));

        Red_Algae_AB = RedALMiddle.plus(new Translation2d(RedReefSideLength / 2, Rotation2d.fromDegrees(270 + 180)));
        Red_Algae_CD = RedBCMiddle.plus(new Translation2d(RedReefSideLength / 2, Rotation2d.fromDegrees(330 + 180)));
        Red_Algae_EF = RedDEMiddle.plus(new Translation2d(RedReefSideLength / 2, Rotation2d.fromDegrees(30 + 180)));
        Red_Algae_GH = RedFGMiddle.plus(new Translation2d(RedReefSideLength / 2, Rotation2d.fromDegrees(90 + 180)));
        Red_Algae_IJ = RedHIMiddle.plus(new Translation2d(RedReefSideLength / 2, Rotation2d.fromDegrees(150 + 180)));
        Red_Algae_KL = RedJKMiddle.plus(new Translation2d(RedReefSideLength / 2, Rotation2d.fromDegrees(210 + 180)));

        RedCenterAlgae = Red_Algae_AB.plus(new Translation2d(RedCenterAlgaeToReefSide, 0));
        RedLeftAlgae = RedCenterAlgae.plus(new Translation2d(0, -RedCenterAlgaeToLeftAlgae));
        RedRightAlgae = RedCenterAlgae.plus(new Translation2d(0, RedCenterAlgaeToRightAlgae));

        RedInitLine = (FIELD_X / 2) + RedInitLineToLeftCage;

        RedProc = new Translation2d(RedInitLine + RedProcToInitLine, FIELD_Y);
        
        RedLeftCage = new Translation2d(RedInitLine - RedInitLineToLeftCage, RedLeftHPWallToLeftCage_Y);
        RedCenterCage = RedLeftCage.plus(new Translation2d(0, RedCenterCageToLeftCage));
        RedRightCage = RedCenterCage.plus(new Translation2d(0, RedCenterCageToRightCage));

        RedLeftStation = new Translation2d(FIELD_X - Units.inchesToMeters(65)/2, Units.inchesToMeters(47)/2);
        RedRightStation = new Translation2d(FIELD_X - Units.inchesToMeters(65)/2, FIELD_Y - Units.inchesToMeters(47)/2);

        // Field Dimensions
        FIELD_DIMENSIONS = new Translation2d(FIELD_X, FIELD_Y);

        // Field Layout
        fieldLayout = AprilTagFields.k2025ReefscapeWelded.loadAprilTagLayoutField();
    }

    public boolean red() {
        return RobotStatus.getInstance().getAllianceColor() == AllianceColor.RED;
    }

    private Translation2d flip(Translation2d red, Translation2d blue) {
        return red() ? red : blue;
    }

    public Translation2d getProc() {
        return flip(RedProc, BlueProc);
    }

    public Translation2d getLeftStation() {
        return flip(RedLeftStation, BlueLeftStation);
    }

    public Translation2d getRightStation() {
        return flip(RedRightStation, BlueRightStation);
    }

    public Translation2d getLeftCage() {
        return flip(RedLeftCage, BlueLeftCage);
    }

    public Translation2d getCenterCage() {
        return flip(RedCenterCage, BlueCenterCage);
    }

    public Translation2d getRightCage() {
        return flip(RedRightCage, BlueRightCage);
    }

    // Override if field is measured otherwise
    public Translation2d getFieldDimensions() {
        return new Translation2d(FIELD_X, FIELD_Y);
    }

    /**
     * 2023: https://firstfrc.blob.core.windows.net/frc2023/FieldAssets/2023LayoutMarkingDiagram.pdf
     * 2024: https://firstfrc.blob.core.windows.net/frc2024/FieldAssets/2024LayoutMarkingDiagram.pdf
     * 2025: https://firstfrc.blob.core.windows.net/frc2025/FieldAssets/2025FieldDrawings-FieldLayoutAndMarking.pdf
     * 2026:
     * 2027:
     * 
     * Current: Set for 2025 tags
     * Future: Set to 2026 tags
     * 
     * @param tagID tag ID to check
     * @return Is the tag part of my current alliance
     */
    public boolean isMyAlliance(int tagID) {
        return MathUtils.equalsOneOf(tagID, 
                red()
                ? new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11} // red tags
                : new double[] {12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22} // blue tags
            );
    }

    public Pose2d getTagById(int id) {
        var t = fieldLayout.getTagPose(id);
        return t.isPresent() ? t.get().toPose2d() : null;
    }

    public void setVenue(VENUE venue) {
        try {
            loadConstants(venue.filePath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public enum VENUE {
        SHOP("Shop.json"),
        JACKSON("Jackson.json"),
        LIVONIA("Livonia.json"),
        MILSTEIN("Milstein.json"),
        KETTERING("Kettering.json"),
        KETTERING_KICKOFF("KetteringKickoff.json"),
        GIRLS_COMP("GirlsComp.json");

        public String filePath;
        private VENUE(String p) {
            filePath = p;
        }
    }
}
