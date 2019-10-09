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
    private TextView autoActiveStatus;
    private TextView hasControl;

    private TextView send;
    private double data = 1;
    private HandlerThread sendHandlerThread;
    private Handler sendHandler;
    private boolean shouldSend;

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
        autoActiveStatus = findViewById(R.id.auto_active_status);
        hasControl = findViewById(R.id.has_control);

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data++;
                send.setText(String.format(Locale.ENGLISH, "%.3f", data));
            }
        });
        send = findViewById(R.id.send);


    }

    @Override
    public void onResume() {
        super.onResume();

        sendHandlerThread = new HandlerThread("sendHandlerThread");
        sendHandlerThread.start();
        sendHandler = new Handler(sendHandlerThread.getLooper());

        sendHandler.post(sendRunnable);
    }

    private final Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            sendHandler.postDelayed(sendRunnable, 200);
            if (shouldSend) {
                double[] dataArray = new double[13];
                for (int i = 0; i< 13; i++) {
                    dataArray[i] = data;
                }
                digAssistManager.setActiveControlData(new ActiveControlData(dataArray));
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
            if (value == DigAssistManager.ServiceStatus.CONNECTED) {
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
}
