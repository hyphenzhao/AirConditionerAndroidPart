package com.example.hyphen.airconditioner;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    protected int delayTime = 360000;
    protected boolean powerButtonClicked = false;
    protected int temp, humi, speed, time, mode;
    protected String speedList[] = {
            "自动", "低速", "中速", "高速"
    };
    protected String modeList[] = {
            "制冷", "制热", "除湿", "换气", "自动"
    };
    protected Handler myHandler = null;
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
        };

        final Runnable myRunnable = new Runnable(){
            @Override
            public void run(){
                if(temp + 2 <= 30) {
                    temp += 2;
                    String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=2";
                    new CommandSender().execute(powerOnUrl);
                }
                else if(temp + 1 <= 30) {
                    temp += 1;
                }
                textViews[0].setText("温度:\n"+ temp + "℃");
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=2";
                new CommandSender().execute(powerOnUrl);
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
                    for(int i = 0; i < 5; i++) {
                        textViews[i].setEnabled(false);
                        for (int j = 0; j < 2; j++) {
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
                    String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=1";
                    new CommandSender().execute(powerOnUrl);
                    if(myHandler != null) {
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }

                }else{
                    powerButton.setTextColor(0xff00ff00);
                    smartButton.setTextColor(0xff00ff00);
                    for(int i = 0; i < 5; i++) {
                        textViews[i].setEnabled(true);
                        for (int j = 0; j < 2; j++) {
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
                    String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=0";
                    new CommandSender().execute(powerOnUrl);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
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
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=2";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[0][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp - 1 >= 16)
                    temp -= 1;
                textViews[0].setText("温度:\n"+ temp + "℃");
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=3";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[1][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(humi + 1 <= 60)
                    humi += 1;
                textViews[1].setText("湿度:\n"+ humi + "%");
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=4";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[1][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(humi - 1 >= 30)
                    humi -= 1;
                textViews[1].setText("湿度:\n"+ humi + "%");
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=5";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[2][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (speed + 1) % 4;
                textViews[2].setText("风速:\n" + speedList[speed]);
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=6";
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
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=7";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[3][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = (time + 1) % 25;
                textViews[3].setText("定时:\n" + time + "H");
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=8";
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
                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=9";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[4][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = (mode + 1) % 5;
                textViews[4].setText("模式:\n" + modeList[mode]);
                boolean button1Enabled = true;

                if(mode == 2) {
                    button1Enabled = false;
                }else if(mode == 0){
                    smartMode = true;
                    smartButton.setTextColor(0xff00ff00);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
                }
                if(mode != 0) {
                    if(myHandler != null) {
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }
                }

                buttons[1][0].setEnabled(button1Enabled);
                buttons[1][1].setEnabled(button1Enabled);
                textViews[1].setEnabled(button1Enabled);

                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=10";
                new CommandSender().execute(powerOnUrl);
            }
        });

        buttons[4][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode - 1 >= 0)
                    mode -= 1;
                else
                    mode = 4;
                textViews[4].setText("模式:\n" + modeList[mode]);
                boolean button1Enabled = true;

                if(mode == 2) {
                    button1Enabled = false;
                }else if(mode == 0){
                    smartMode = true;
                    smartButton.setTextColor(0xff00ff00);
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, delayTime);
                }
                if(mode != 0) {
                    if(myHandler != null) {
                        myHandler.removeCallbacks(myRunnable);
                        System.out.println("Temperature automatically increase has been terminated!");
                    }
                }

                buttons[1][0].setEnabled(button1Enabled);
                buttons[1][1].setEnabled(button1Enabled);
                textViews[1].setEnabled(button1Enabled);

                String powerOnUrl = "http://ec2-54-254-214-255.ap-southeast-1.compute.amazonaws.com/sendCommand.php?commandID=11";
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

    public void powerButtonClicked(View v) {
        SimpleAlertDialog.displayWithOK(this, "Power button clicked via XML handler");
    }
}
