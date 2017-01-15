package com.example.hyphen.airconditioner;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    public static String httpResultStr;
    public static boolean flag = false;
    protected int delayTime = 360000, refreshTime = 1000;
    protected boolean powerButtonClicked = false;
    protected int temp, humi, speed, time, mode;
    protected String speedList[] = {
            "自动", "低速", "中速", "高速"
    };
    protected String modeList[] = {
            "制冷", "制热", "除湿"
    };
    protected Handler myHandler = null, refreshHandler = null;
    protected boolean smartMode;
//    protected CommandSender mySender = new CommandSender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button powerButton = (Button) findViewById(R.id.power);
        final Button smartButton = (Button) findViewById(R.id.smart);
        final Button buttons[][] = {
                {(Button)findViewById(R.id.button1_1),(Button)findViewById(R.id.button1_2)},
                {(Button)findViewById(R.id.button2_1),(Button)findViewById(R.id.button2_2)},
                {(Button)findViewById(R.id.button3_1),(Button)findViewById(R.id.button3_2)},
                {(Button)findViewById(R.id.button4_1),(Button)findViewById(R.id.button4_2)},
                {(Button)findViewById(R.id.button5_1),(Button)findViewById(R.id.button5_2)}
        };
        final TextView textViews[] = {
                (TextView)findViewById(R.id.textView1),
                (TextView)findViewById(R.id.textView2),
                (TextView)findViewById(R.id.textView3),
                (TextView)findViewById(R.id.textView4),
                (TextView)findViewById(R.id.textView5),
                (TextView)findViewById(R.id.textView6),
                (TextView)findViewById(R.id.textView7),
        };

        final Runnable myRunnable = new Runnable(){
            @Override
            public void run(){
                if(temp + 2 <= 30) {
                    temp += 2;
                }
                else if(temp + 1 <= 30) {
                    temp += 1;
                }
                textViews[0].setText("温度:\n"+ temp + "℃");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                //System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        };

        final Runnable refreshRunnable = new Runnable(){
            @Override
            public void run(){

                if(httpResultStr != null && !httpResultStr.isEmpty()){
                    System.out.println(httpResultStr);
                    String str[] = httpResultStr.split("-");
                    textViews[5].setText("当前温度：\n" + str[0] + "℃");
                    textViews[6].setText("当前湿度：\n" + str[1] + "%");
                }
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/receiveStatus.php";
                //System.out.println(powerOnUrl);
                flag = true;
                new CommandSender().execute(powerOnUrl);
                refreshHandler.postDelayed(this, refreshTime);
            }
        };

        for(int i = 0; i < 5; i++) {
            textViews[i].setEnabled(false);
            for (int j = 0; j < 2; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
        smartButton.setEnabled(false);
        smartMode = true;

        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //powerButtonClicked(v);
                if(powerButtonClicked){
                    powerButton.setTextColor(0xffff0000);
                    for(int i = 0; i < 7; i++) {
                        textViews[i].setEnabled(false);
                        for (int j = 0; j < 2; j++) {
                            if(i < 5)
                                buttons[i][j].setEnabled(false);
                        }
                    }
                    smartButton.setEnabled(false);
                    smartButton.setTextColor(0xffaaaaaa);
                    smartMode = false;
                    textViews[0].setText("温度:\n--℃");
                    textViews[1].setText("湿度:\n--%");
                    textViews[2].setText("风速:\n--");
                    textViews[3].setText("定时:\n--");
                    textViews[4].setText("模式:\n--");
                    textViews[5].setText("当前温度:\n--℃");
                    textViews[6].setText("当前湿度:\n--%");
                    String commandID = generateCommand(0, mode, temp, speed, time, humi);
                    String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                    System.out.println(powerOnUrl);
                    new CommandSender().execute(powerOnUrl);
                    if(myHandler != null) {
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }
                    if(refreshHandler != null) {
                        refreshHandler.removeCallbacks(refreshRunnable);
                    }

                }else{
                    powerButton.setTextColor(0xff00ff00);
                    smartButton.setTextColor(0xff00ff00);
                    for(int i = 0; i < 7; i++) {
                        textViews[i].setEnabled(true);
                        for (int j = 0; j < 2; j++) {
                            if(i < 5)
                                buttons[i][j].setEnabled(true);
                        }
                    }
                    smartButton.setEnabled(true);
                    smartMode = true;
                    textViews[0].setText("温度:\n26℃");
                    textViews[1].setText("湿度:\n40%");
                    textViews[2].setText("风速:\n自动");
                    textViews[3].setText("定时:\n0H");
                    textViews[4].setText("模式:\n制冷");
                    temp = 26;
                    humi = 40;
                    speed = 0;
                    time = 0;
                    mode = 0;
                    String commandID = generateCommand(1, mode, temp, speed, time, humi);
                    String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                    System.out.println(powerOnUrl);
                    new CommandSender().execute(powerOnUrl);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
                    refreshHandler = new Handler();
                    refreshHandler.postDelayed(refreshRunnable, refreshTime);
                }
                powerButtonClicked = !powerButtonClicked;
            }
        });

        buttons[0][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp + 1 <= 30)
                    temp += 1;
                textViews[0].setText("温度:\n"+ temp + "℃");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[0][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp - 1 >= 16)
                    temp -= 1;
                textViews[0].setText("温度:\n"+ temp + "℃");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[1][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(humi + 1 <= 60)
                    humi += 1;
                textViews[1].setText("湿度:\n"+ humi + "%");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[1][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(humi - 1 >= 30)
                    humi -= 1;
                textViews[1].setText("湿度:\n"+ humi + "%");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[2][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (speed + 1) % 4;
                textViews[2].setText("风速:\n" + speedList[speed]);
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[2][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speed - 1 >= 0)
                    speed -= 1;
                else
                    speed = 3;
                textViews[2].setText("风速:\n" + speedList[speed]);
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[3][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = (time + 1) % 25;
                textViews[3].setText("定时:\n" + time + "H");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });
        buttons[3][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time - 1 >= 0)
                    time = time - 1;
                else
                    time = 24;
                textViews[3].setText("定时:\n" + time + "H");
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[4][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = (mode + 1) % 3;
                textViews[4].setText("模式:\n" + modeList[mode]);
                boolean button1Enabled = true;

                if(mode == 2) {
                    button1Enabled = false;
                }else if(mode == 0){
                    smartMode = true;
                    smartButton.setTextColor(0xff00ff00);
                    smartButton.setEnabled(true);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
                }
                if(mode != 0) {
                    if(myHandler != null) {
                        smartButton.setEnabled(false);
                        smartButton.setTextColor(0xffaaaaaa);
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }
                }

                buttons[1][0].setEnabled(button1Enabled);
                buttons[1][1].setEnabled(button1Enabled);
                textViews[1].setEnabled(button1Enabled);
                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[4][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode - 1 >= 0)
                    mode -= 1;
                else
                    mode = 2;
                textViews[4].setText("模式:\n" + modeList[mode]);
                boolean button1Enabled = true;

                if(mode == 2) {
                    button1Enabled = false;
                }else if(mode == 0){
                    smartMode = true;
                    smartButton.setTextColor(0xff00ff00);
                    smartButton.setEnabled(true);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
                }
                if(mode != 0) {
                    if(myHandler != null) {
                        smartButton.setEnabled(false);
                        smartButton.setTextColor(0xffaaaaaa);
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }
                }

                buttons[1][0].setEnabled(button1Enabled);
                buttons[1][1].setEnabled(button1Enabled);
                textViews[1].setEnabled(button1Enabled);

                String commandID = generateCommand(1, mode, temp, speed, time, humi);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/AirConditionerServerPart/sendCommand.php?commandID=" + commandID;
                System.out.println(powerOnUrl);
                new CommandSender().execute(powerOnUrl);
            }
        });

        smartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smartMode == true) {
                    if(myHandler != null) {
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }
                    smartButton.setTextColor(0xffff0000);
                    smartMode = false;
                }else{
                    smartMode = true;
                    smartButton.setTextColor(0xff00ff00);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
                }
            }
        });
    }

    protected String generateCommand(int power, int mode, int temp, int speed, int time, int humi) {
        return power + "-" + mode + "-" + temp + "-" + speed + "-" + time + "-" + humi;
    }

    public void powerButtonClicked(View v) {
        SimpleAlertDialog.displayWithOK(this, "Power button clicked via XML handler");
    }
}
