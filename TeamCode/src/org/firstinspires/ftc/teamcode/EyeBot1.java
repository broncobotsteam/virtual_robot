package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;

//the wrist is moving weirdly but it has to do with the claw

@TeleOp
public class EyeBot1 extends LinearOpMode {  // Removed .java here

    private DcMotor armMotor;
    private Servo clawServo;
    private Servo wristServo;
    private DcMotor linearSliderMotor;

    private int maxEncoderTicks = 732; // 0.5 revolutions * encoder ticks per revolution (assuming 1680 for the TorqueNADO)
    private ElapsedTime runtime = new ElapsedTime();

    static final int ARM_DOWN = -80; // this is not final it does need testing
    static final int ARM_UP = 200; // this is not final it does need testing

    static final double WRIST_BACK_POSITION = 0; // this is not final it does need testing
    static final double WRIST_FORWARD_POSITION = 0.5; // this is not final it does need testing
    static final double WRIST_DOWN_POSITION = 1; // this is not final it does need testing
    static boolean WRIST_BACK = true;
    static boolean WRIST_DOWN = false;
    static boolean WRIST_FORWARD = false;


    static final double CLAW_OPEN_SERVO_POSITION = 0.1;
    static final double CLAW_CLOSED_SERVO_POSITION = 0.9;
    static final double CLAW_HALF_SERVO_POSITION = 0.5;
    static boolean CLAW_HALF = false;
    static boolean CLAW_CLOSED = true;
    static boolean CLAW_OPEN = false;

    static final double LINEAR_SLIDER_UP = 0;  // this is not final it does need testing
    static final double LINEAR_SLIDER_DOWN = 0;  // this is not final it does need testing
    static final double LINEAR_SLIDER_MIDDLE = 0;  // this is not final it does need testing

    @Override
    public void runOpMode() throws InterruptedException {
        double tgtPower2 = CLAW_CLOSED_SERVO_POSITION;


        // Declare our motors
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("backRight");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("frontRight");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("backLeft");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("frontLeft");
        armMotor = hardwareMap.dcMotor.get("armMotor");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        wristServo = hardwareMap.get(Servo.class, "wristServo");
        linearSliderMotor = hardwareMap.get(DcMotor.class,"linearSliderMotor");




        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setDirection(DcMotor.Direction.REVERSE);
        armMotor.setTargetPosition(armMotor.getCurrentPosition());
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);



        linearSliderMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSliderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSliderMotor.setTargetPosition(linearSliderMotor.getCurrentPosition());
        linearSliderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            int currentPosition = armMotor.getCurrentPosition();
            int currentPositionLinear = linearSliderMotor.getCurrentPosition();

            telemetry.addData("Arm at:", currentPosition);
            telemetry.addData("Linear Slider at:", currentPositionLinear);
            telemetry.update();

            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double power_multiplier = 0.25 + (0.75 * gamepad1.right_trigger);

            double frontLeftPower = power_multiplier * (y + x + rx) / denominator;
            double backLeftPower = power_multiplier * (y - x + rx) / denominator;
            double frontRightPower = power_multiplier * (y - x - rx) / denominator;
            double backRightPower = power_multiplier * (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
            clawServo.setPosition(tgtPower2);
            //wristServo.setPosition(tgtPower3);
            //linearSliderMotor.setTargetPosition(1);

            currentPosition = armMotor.getCurrentPosition();
            currentPositionLinear = linearSliderMotor.getCurrentPosition();

            double lowPower = 0;
            if (gamepad2.dpad_down) {
                armMotor.setTargetPosition(armMotor.getCurrentPosition()-100);

                armMotor.setPower(1.5);
            }
            else if (gamepad2.dpad_up) {
                armMotor.setTargetPosition(armMotor.getCurrentPosition()+100);

                armMotor.setPower(1.5);

            }else {
                armMotor.setTargetPosition(armMotor.getCurrentPosition());
                armMotor.setPower(lowPower);


            }
//            boolean shouldMoveDown = armMotor.getTargetPosition() == ARM_DOWN && armMotor.getCurrentPosition() > ARM_DOWN;
//            boolean shouldMoveUp = armMotor.getTargetPosition() == ARM_UP && armMotor.getCurrentPosition() < ARM_UP;
//            if (shouldMoveUp || shouldMoveDown){
//                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }else{
//                armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            }








            //tPower2 = -gamepad2.right_stick_y;
            if (gamepad2.right_stick_y < -0.5){
                CLAW_CLOSED = true;
                CLAW_HALF = false;
                CLAW_OPEN = false;
            }

            if (gamepad2.right_stick_y > 0.5) {
                CLAW_CLOSED = false;
                CLAW_HALF = false;
                CLAW_OPEN = true;
            }
            if (gamepad2.right_stick_x > 0.5){
                CLAW_HALF = true;
                CLAW_CLOSED = false;
                CLAW_OPEN = false;
            }

            if (CLAW_CLOSED) {
                tgtPower2 = CLAW_CLOSED_SERVO_POSITION;
            } else if (CLAW_OPEN) {
                tgtPower2 = CLAW_OPEN_SERVO_POSITION;
            }
            else if (CLAW_HALF) {
                tgtPower2 = CLAW_HALF_SERVO_POSITION;
            }
            double duration =0.5;

            double sLiderLimit = 1000;
            if(gamepad2.left_trigger >0.1){
                linearSliderMotor.setTargetPosition(currentPositionLinear+100);
                linearSliderMotor.setPower(2);

            }
            else if(gamepad2.right_trigger >0.1){
                linearSliderMotor.setTargetPosition(currentPositionLinear-100);
                linearSliderMotor.setPower(2);

            }
            else {
                linearSliderMotor.setPower(lowPower);
                linearSliderMotor.setTargetPosition(linearSliderMotor.getCurrentPosition());

            }

            if (gamepad2.left_stick_x > 0.5){
                WRIST_BACK = false;
                WRIST_DOWN = true;
                WRIST_FORWARD = false;
                wristServo.setPosition(WRIST_DOWN_POSITION);

            }
            if (gamepad2.left_stick_y > 0.5){
                WRIST_BACK = false;
                WRIST_DOWN = false;
                WRIST_FORWARD = true;
                wristServo.setPosition(WRIST_FORWARD_POSITION);

            }

            if (gamepad2.left_stick_y < -0.5){
                WRIST_BACK = true;
                WRIST_DOWN = false;
                WRIST_FORWARD = false;
                wristServo.setPosition(WRIST_BACK_POSITION);

            }

        }

    }
}