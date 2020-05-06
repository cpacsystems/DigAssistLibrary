package se.cpacsystems.digassist.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import se.cpacsystems.digassist.ActiveControlData;
import se.cpacsystems.digassist.DigAssistManager;

public class MainActivity extends AppCompatActivity {

    private DigAssistManager digAssistManager;
    private boolean connected = false;

    private Button buttonService;

    private TextView textViewServiceVersion;

    private TextView serviceStatus;
    private TextView autoModeStatus;
    private TextView fineGradingMode;
    private TextView bucketAngleMode;
    private TextView activeTiltMode;
    private TextView backGradingMode;
    private TextView bucketRecallStatus;
    private TextView autoActiveStatus;
    private TextView hasControl;

    private TextView send;
    private TextView pause;
    private TextView specifyData;
    private TextView bucketTipInfo;

    private double data = 1;
    private HandlerThread sendHandlerThread;
    private Handler sendHandler;
    private boolean shouldSend;
    private boolean shallPause;
    private boolean specificData;
    private int bucketTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        digAssistManager = new DigAssistManager(getApplicationContext());

        Button buttonBoundary = findViewById(R.id.button_boundary);
        buttonBoundary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digAssistManager.launchBoundaryDialog();
            }
        });

        Button buttonTools = findViewById(R.id.button_tools);
        buttonTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digAssistManager.launchToolsDialog();
            }
        });

        Button buttonActiveControl = findViewById(R.id.button_active_control);
        buttonActiveControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digAssistManager.launchActivityActiveControl();
            }
        });

        buttonService = findViewById(R.id.button_service);
        buttonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    digAssistManager.disconnect();
                } else {
                    digAssistManager.connect(serviceListener);
                }
            }
        });

        textViewServiceVersion = findViewById(R.id.service_version);

        serviceStatus = findViewById(R.id.service_status);
        autoModeStatus = findViewById(R.id.auto_mode);
        fineGradingMode = findViewById(R.id.fine_grading);
        bucketAngleMode = findViewById(R.id.bucket_angle_mode);
        activeTiltMode = findViewById(R.id.active_tilt_mode);
        backGradingMode = findViewById(R.id.backgrading_mode);
        bucketRecallStatus = findViewById(R.id.bucketrecall_status);
        autoActiveStatus = findViewById(R.id.auto_active_status);
        hasControl = findViewById(R.id.has_control);

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data++;
                send.setText(String.format(Locale.ENGLISH, "%.3f", data));
                specifyData.setText(specificData ? "Specific Data" : String.format("%.3f", data));
            }
        });
        send = findViewById(R.id.send);


        Button pauseButton = findViewById(R.id.button_pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shallPause = !shallPause;
                pause.setText(shallPause ? "PAUSE" : "RUNNING");
            }
        });
        pause = findViewById(R.id.pause);

        Button specifyButton = findViewById(R.id.button_sendspecific);
        specifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificData = !specificData;
                specifyData.setText(specificData ? "Specific Data" : String.format("%.3f", data));
                if (specificData) {
                    setSpecificData();
                }
            }
        });
        specifyData = findViewById(R.id.specifydata);

        Button bucketTipButton = findViewById(R.id.button_buckettip);
        bucketTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bucketTip = bucketTip++ >= ActiveControlData.ToolRef.TOOL_REF_MIDDLE ? ActiveControlData.ToolRef.TOOL_REF_LEFT : bucketTip;
                bucketTipInfo.setText(String.format(Locale.ENGLISH, "%d", bucketTip));
                digAssistManager.setActiveToolRef(bucketTip);
            }
        });
        bucketTipInfo = findViewById(R.id.buckettip);
    }

    @Override
    public void onResume() {
        super.onResume();

        sendHandlerThread = new HandlerThread("sendHandlerThread");
        sendHandlerThread.start();
        sendHandler = new Handler(sendHandlerThread.getLooper());
        if (shouldSend) {
            digAssistManager.setOperatorInterrupt(false);
        }
        sendHandler.post(sendRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (shouldSend) {
            digAssistManager.setOperatorInterrupt(true);
        }
    }

    private final Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            sendHandler.postDelayed(sendRunnable, 200);
            if (shouldSend && !shallPause) {
                if (!specificData) {
                    double[] dataArray = new double[13];
                    for (int i = 0; i < 13; i++) {
                        dataArray[i] = data;
                    }
                    activeControlData = new ActiveControlData(dataArray);
                }
                digAssistManager.setActiveControlData(activeControlData);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendHandlerThread.quit();
    }

    private DigAssistManager.DigAssistListener serviceListener = new DigAssistManager.DigAssistListener() {
        @Override
        public void serviceStatus(int value) {
            serviceStatus.setText(String.format(Locale.ENGLISH, "%d", value));
            if (value != DigAssistManager.ServiceStatus.DISCONNECTED) {
                buttonService.setText("Disconnect");
                int version = digAssistManager.getServiceVersion();
                textViewServiceVersion.setText("version: " + version);
                connected = true;
            } else {
                buttonService.setText("Connect");
                textViewServiceVersion.setText("version:  -1");
                connected = false;
            }
        }

        @Override
        public void autoModeStatus(final int value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    autoModeStatus.setText("" + value);
                }
            });
        }

        @Override
        public void fineGradingMode(final boolean value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fineGradingMode.setText("" + value);
                }
            });
        }

        @Override
        public void bucketAngleMode(final int value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bucketAngleMode.setText("" + value);
                }
            });
        }

        @Override
        public void activeTiltMode(final boolean value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activeTiltMode.setText("" + value);
                }
            });
        }

        @Override
        public void backGradingMode(final boolean value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    backGradingMode.setText("" + value);
                }
            });
        }

        @Override
        public void bucketRecallStatus(final int value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bucketRecallStatus.setText("" + value);
                }
            });
        }

        @Override
        public void autoActiveStatus(final boolean value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    autoActiveStatus.setText("" + value);
                }
            });
        }

        @Override
        public void hasControl(final boolean value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hasControl.setText("" + value);
                }
            });
            shouldSend = value;
        }
    };
    ActiveControlData activeControlData = new ActiveControlData();

    private void setSpecificData() {
        activeControlData.setAngleBck(-0.05);
        activeControlData.setAngleLft(-0.07);
        activeControlData.setAngleFwd(0.09);
        activeControlData.setAngleRgt(0.14);
        activeControlData.setDistBck(1.2);
        activeControlData.setDistLft(3.4);
        activeControlData.setDistFwd(5.6);
        activeControlData.setDistRgt(7.8);
        activeControlData.setPlaneEquationA(0.5);
        activeControlData.setPlaneEquationB(0.1);
        activeControlData.setPlaneEquationC(0.4);
        activeControlData.setPlaneEquationD(2);
        activeControlData.setDistTool(4.1f);
    }
}
