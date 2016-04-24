package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.robot.Robot.AutoTask;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class for Autonomous commands. Uses switch-cases in order to acheive
 * multi-threading without creating multiple threads. Also much faster compared
 * to multi-threading and takes less space.
 * 
 * @author Usaid Malik
 */
public class Autonomous {
	private double gyroTurnAngle = 0;
	public static int driveForwardState = 0;
	private int driveForwardGyroState = 0;
	private int touchForwardState = 0;
	private int moatForwardState = 0;
	public int navexLowbarShoot = 0;
	
	public static boolean autoAIMState = false;
	public static boolean seeOnlyTowerState = false;
	private static long forwardGyro_neededTime = 0;
	private static long forwardGyro_landTime = 0;
	private static long forwardGyro_landTurn = 0;
	private static long FlyWheelTimer = 0;
	public static int currAIM = 1;
	private long crossMoatTimer = 0;
	private static double[] driveDistance = { 0, 0 };
	private static double[] off = { 0.0, 0.0 };

	private final double[] speedToOuterWork = { 0.65, 0.65 }, speedToCrossMoat = { 1, 1 };
	
	
	public static int stationNumber = 1;

	private static final double distanceToOuterWork = 48, distanceToCrossWork = 135, // 128
			distanceToCrossRough = 130, distanceToSeeOnlyTower = 12, forwardGyro_barelyCross = 122, forwardGyro_barelyRough = 132, forwardGyro_barelyRock = 150;// 122

	public static enum FirstMove {
		StandStill, TouchOuterWork, Lowbar, RockWall, RoughTerrain, Portcullis, ChevalDeFrise, Moat;
	}

	public static enum AutoShoot {
		False, True;
	}

	/**
	 * Initializes the Autonomous class.
	 */
	public Autonomous() {

	}

	private void curveFix(double speeds[]) {
		Robot.drivebase.drive(-speeds[0], -speeds[1]);
	}

	private void touchForward() {

		if ((driveDistance[0] < distanceToOuterWork || driveDistance[1] < distanceToOuterWork)
				&& driveForwardState == 0) {
			curveFix(speedToOuterWork);
		} else {
			Robot.drivebase.resetDrive();
			driveForwardState = 1;
		}

		// touchForwardState = SwitchCase.driveForward(touchForwardState, 72);
	}

	private void PorticShoot() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < (distanceToCrossWork + 24) || driveDistance[1] < (distanceToCrossWork + 24))
				&& driveForwardState == 0) {
			Robot.drivebase.drive(-0.7, -0.73);
			Robot.drivebase.chopperDown();
			SmartDashboard.putString("READY READY READY", "Auto driving");
		} else {
			Robot.drivebase.drive(0, 0);
			Robot.drivebase.resetDrive();
			Robot.drivebase.chopperUp();
			Timer.delay(0.25);
			Robot.drivebase.drive(0,
					0);/*
						 * 
						 * 
						 * for(int time = 0; time < 15; time++) {
						 * Robot.drivebase.drive(-0.78, 0.78);
						 * Timer.delay(0.005); } for(int time2 = 0; time2 < 30;
						 * time2++) { Robot.drivebase.drive(-0.76, -0.76);
						 * Timer.delay(0.005); }
						 */
			driveForwardState = 1;
			if (!autoAIMState) {
				autoAIMState = true;
				Timer.delay(0.75);
				// SwitchCase.moveAmount = 0.43;
				SwitchCase.checkAmount = 1;
				SwitchCase.shotTheBall = false;
				currAIM = SwitchCase.autoAim(currAIM, 3350);
			}
			if (autoAIMState) {

				SmartDashboard.putString("READY READY READY", "Auto aiming");
				currAIM = SwitchCase.autoAim(currAIM, 3350);
				if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
					currAIM = 1;
				}
			}
		}

	}

	private void LowbarShoot() {
		if ((driveDistance[0] < distanceToCrossWork || driveDistance[1] < distanceToCrossWork)
				&& driveForwardState == 0) {
			// curveFix(speedToOuterWork);
			Robot.drivebase.drive(-0.80, -0.83);
		} else if (!autoAIMState && !seeOnlyTowerState) {
			driveForwardState = 1;
			Timer.delay(0.5);
			for (int time = 0; time < 30; time++) {
				Robot.drivebase.drive(0.78, -0.78);
				Timer.delay(0.005);
			}
			for (int time2 = 0; time2 < 35; time2++) {
				Robot.drivebase.drive(-0.76, -0.76);
				Timer.delay(0.005);
			}
			Robot.drivebase.resetDrive(); // Reset encoders for moving forward
			seeOnlyTowerState = true;
		} else if (!autoAIMState && seeOnlyTowerState) {
			if (driveDistance[0] < distanceToSeeOnlyTower && driveDistance[1] < distanceToSeeOnlyTower) {
				Robot.drivebase.drive(-.70, -.73);
			} else {
				autoAIMState = true;
				Timer.delay(0.15);
				// SwitchCase.moveAmount = 0.43;
				SwitchCase.checkAmount = 1;
				SwitchCase.shotTheBall = false;
				currAIM = SwitchCase.autoAim(currAIM, 3350);
			}
		}
		if (autoAIMState) {
			SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(currAIM, 3350);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 1;
			}
		}

	}

	private void crossForward() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < distanceToCrossWork && driveDistance[1] < distanceToCrossWork)
				&& driveForwardState == 0) {
			// curveFix(speedToOuterWork);
			Robot.drivebase.drive(-0.70, -0.73);
		} else {
			Robot.drivebase.drive(0, 0);
			Robot.drivebase.resetDrive();
			driveForwardState = 1;
		}

	}

	private void crossRockWall() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < distanceToCrossWork - 5 && driveDistance[1] < distanceToCrossWork - 5)
				&& driveForwardState == 0) {
			Robot.drivebase.drive(-1, -1);
		} else {
			Robot.drivebase.drive(0, 0);
			Robot.drivebase.resetDrive();
			driveForwardState = 1;
		}
	}


	private void shootRockWall() {
		driveDistance = Robot.drivebase.getEncDistance();
		if ((driveDistance[0] < (distanceToCrossWork - 24) || driveDistance[1] < (distanceToCrossWork - 24))
				&& driveForwardState == 0) {
			Robot.drivebase.drive(-1, -1);
			SmartDashboard.putString("READY READY READY", "Auto driving");
		} else {
			Robot.drivebase.drive(0, 0);
			Robot.drivebase.resetDrive();
			Timer.delay(0.7);
			Robot.drivebase.drive(0, 0);

			driveForwardState = 1;
			if (!autoAIMState) {
				for (int time = 0; time < 10; time++) {
					Robot.drivebase.drive(-0.468, 0.468);
					Timer.delay(0.005);
				}
				Timer.delay(0.2);
				for (int time2 = 0; time2 < 70; time2++) {
					Robot.drivebase.drive(-0.468, -0.468);
					Timer.delay(0.005);
				}
				autoAIMState = true;
				// Timer.delay(0.2);
				SwitchCase.shotTheBall = false;
			}
		}

		if (autoAIMState) {
			SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(currAIM, 3350);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 1;
			}
		}

	}

	private int moatForward(int state) {
		switch (state) {
		case 0:
			break;
		default:
			break;
		case 1:
			curveFix(speedToCrossMoat);
			state = 2;
			break;
		case 2:
			if (!(driveDistance[0] < distanceToCrossWork || driveDistance[1] < distanceToCrossWork)
					&& driveForwardState == 0) {
				Robot.drivebase.resetDrive();
				driveForwardState = 1;
				state = 3;
			}
			break;
		case 3:
			crossMoatTimer = System.currentTimeMillis() + 3000;
			state = 4;
			break;
		case 4:
			if (System.currentTimeMillis() >= crossMoatTimer) {
				Robot.drivebase.drive(0, 0);
				state = 5;
			}
			break;
		case 5:
			state = 0;
			break;
		}
		/*
		 * if((driveDistance[0] < distanceToCrossWork || driveDistance[1] <
		 * distanceToCrossWork) && driveForwardState == 0) {
		 * curveFix(speedToCrossMoat); } else { Robot.drivebase.resetDrive();
		 * driveForwardState = 1; }
		 */
		return state;
	}

	/**
	 * Shoots from the spybox position.
	 */
	private void spyboxShoot() {
		for (int time = 0; time < 35; time++) {
			Robot.drivebase.drive(0.8, -0.8);
			Timer.delay(0.005);
		}
		for (int time2 = 0; time2 < 35; time2++) {
			Robot.drivebase.drive(-0.6, -0.6);
			Timer.delay(0.005);
		}
		if (!autoAIMState) {
			driveForwardState = 1;
			autoAIMState = true;
			Timer.delay(0.75);
			// SwitchCase.moveAmount = 0.43;
			SwitchCase.checkAmount = 1;
			SwitchCase.shotTheBall = false;
			currAIM = SwitchCase.autoAim(currAIM, 3350);
		}
		if (autoAIMState) {
			SmartDashboard.putString("READY READY READY", "Auto aiming");
			currAIM = SwitchCase.autoAim(currAIM, 3350);
			if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
				currAIM = 1;
			}
		}
	}

	private static void encoderUpdate() {
		driveDistance = Robot.drivebase.getEncDistance();
	}

	static double maximumError = 0, minimumError = 0, forwardGyro_previousAngle = 0;

	
	/**
	 * Updates the state of various autonomous functions. This must be called in
	 * <b>autonomousPeriodic()</b>.
	 * <ul>
	 * Currently updates
	 * </ul>
	 * :
	 * <li>driveForward()</li>
	 * <li>autoAim()</li>
	 */
	public void updateStates(AutoTask currentAuto) {

		encoderUpdate();
		final int station = (int) SmarterDashboard.getNumber("STATION", 1);
		
		//SmartDashboard.putString("CURRENT SELECTED", currentAuto.name());
		SmartDashboard.putNumber("NavexState", navexLowbarShoot);
		switch (currentAuto) {
		case TouchOuterWork:
			touchForward();
			break;
		case CrossOuter:
			crossForward();
			break;
		case CrossMoatAndStop:
			// moatForward();
			moatForwardState = 1;
			break;
		case CrossRockWallAndStop:
			crossRockWall();
			break;
		case CrossLowbarAndShoot:
			//LowbarShoot();
			gyroTurnAngle = 39;
			int[] shootSpeedLowbar = {3310, 3310};
			switch(navexLowbarShoot)
			{
			default:
				break;
			case 0:
				Robot.drivebase.resetDrive();
				SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
				SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
				SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
				Robot.drivebase.enablePIDCDrive(-0.68, 0.1);
				navexLowbarShoot = 1;
				break;
			case 1:
				driveDistance = Robot.drivebase.getEncDistance();
				if ((driveDistance[0] > (forwardGyro_barelyCross) || driveDistance[1] > (forwardGyro_barelyCross)))
				{
					Robot.drivebase.enablePIDCTurn(gyroTurnAngle);
					Robot.flywheels.setPIDSpeed(shootSpeedLowbar);
					currAIM = SwitchCase.autoAim(11, 3310);
					FlyWheelTimer = System.currentTimeMillis() + 3000;
					navexLowbarShoot = 2;
				}
				SmartDashboard.putBoolean("INQUE", false);
				break;
			case 2:
				SmartDashboard.putBoolean("INQUE", true);
				SmartDashboard.putString("CALLEDMAN", "NO");
				//SmartDashboard.putNumber("VisionManVals", Vision.manVals[0]);
				//SmartDashboard.putString("READY READY READY", "Auto aiming");
				//Timer.delay(2);
				if(System.currentTimeMillis() >= FlyWheelTimer) {
					Robot.drivebase.disablePIDC();
					SmartDashboard.putString("CALLEDMAN", "YES");
					currAIM = SwitchCase.autoAim(currAIM, 3310);
					if ((currAIM == 0 || currAIM == -1) && !SwitchCase.shotTheBall) {
						currAIM = 13;
						SmartDashboard.putString("CALLEDMAN", "RESET");
					}
					
				}
				//if(SwitchCase.shotTheBall) navexLowbarShoot = 4;
				//Robot.flywheels.setFlywheelSpeed(shootSpeed);
				//navexLowbarShoot = 2;
				break;
			case 3:
				if(System.currentTimeMillis() >= FlyWheelTimer){
					Robot.drivebase.disablePIDC();
					double[] currentRPM = Robot.flywheels.getRPM();
					if ((currentRPM[0] <= shootSpeedLowbar[0] * 1.02 && currentRPM[0] >= shootSpeedLowbar[0] * .98)
							|| (currentRPM[1] <= shootSpeedLowbar[1] * 1.02 && currentRPM[1] >= shootSpeedLowbar[1] * .98)) {
						forwardGyro_neededTime = System.currentTimeMillis() + 750;
						Robot.flywheels.setIntakeSpeed(1.0);
						navexLowbarShoot = 4;
					}
				}
				break;
			case 4:
				if (System.currentTimeMillis() >= forwardGyro_neededTime) {
					Robot.flywheels.setIntakeSpeed(0.0);
					Robot.flywheels.setFlywheelSpeed(off);
					Robot.drivebase.disablePIDC();
					navexLowbarShoot = 5;
				} 
				break;
			case 5://Dead state
				SmartDashboard.putBoolean("isMoving", Robot.drivebase.ahrs.isMoving());
				SmartDashboard.putBoolean("isRotating", Robot.drivebase.ahrs.isRotating());
				SmartDashboard.putBoolean("OnTarget", Robot.drivebase.driveController.onTarget());
				SmartDashboard.putNumber("GetError", Robot.drivebase.driveController.getError());
				SmartDashboard.putNumber("GetAvgError", Robot.drivebase.driveController.getAvgError());
				Robot.drivebase.drive(0.0, 0.0);
				break;
			}
			break;
		case Spybox:
			spyboxShoot();
			break;
		case CrossRockwallAndShoot:	//Position 4 - -25
									//Position 3 - -10
			//shootRockWall();		//Position 1 - 25
			gyroTurnAngle = -25;   //Position 2 - 16.5
			int[] shootSpeedMiddle = {3270, 3270};//Power for 1
			if(navexLowbarShoot == 0)
				navexLowbarShoot = 1;
			break;
		case CrossPortcullisAndShoot:
			PorticShoot();
			break;
		case DoNothing:
			Timer.delay(0.1);
			break;
		default:
			Timer.delay(0.1);
			break;
		}
		touchForwardState = SwitchCase.driveForward(touchForwardState, 72);
		moatForwardState = moatForward(moatForwardState);
		driveForwardGyroState = SwitchCase.driveForwardGyro(driveForwardGyroState, distanceToCrossWork);
		navexLowbarShoot = SwitchCase.autonomous(navexLowbarShoot, -25.0, (int)forwardGyro_barelyCross);
		// driveForwardState = SwitchCase.driveForward(driveForwardState);
		// autoAIMState = SwitchCase.autoAim(autoAIMState);

	}

}
