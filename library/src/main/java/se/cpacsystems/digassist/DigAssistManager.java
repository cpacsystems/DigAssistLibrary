/**
 * Copyright 2019 Cpac Systems AB
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.cpacsystems.digassist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class DigAssistManager implements ActiveControlSignals {

    private static final String LOG_TAG = DigAssistManager.class.getSimpleName();

    private static final int VERSION = 1;

    private boolean hasControl = false;
    private int status = ServiceStatus.DISCONNECTED;

    private IDigAssist digAssistService = null;
    private DigAssistConnection digAssistConnection = null;

    private DigAssistListener serviceListener;
    private IDigAssistListener activeControlListener = new IDigAssistListener.Stub() {

        @Override
        public void autoModeStatus(int value) {
            if (serviceListener != null) {
                serviceListener.autoModeStatus(value);
            }
        }

        @Override
        public void fineGradingMode(boolean value) {
            if (serviceListener != null) {
                serviceListener.fineGradingMode(value);
            }
        }

        @Override
        public void bucketAngleMode(int value) {
            if (serviceListener != null) {
                serviceListener.bucketAngleMode(value);
            }
        }

        @Override
        public void activeTiltMode(boolean value) {
            if (serviceListener != null) {
                serviceListener.activeTiltMode(value);
            }
        }

        @Override
        public void backGradingMode(boolean value) {
            if (serviceListener != null) {
                serviceListener.backGradingMode(value);
            }
        }

        @Override
        public void autoActiveStatus(boolean value) {
            if (serviceListener != null) {
                serviceListener.autoActiveStatus(value);
            }
        }

        @Override
        public void hasControl(boolean value) {
            hasControl = value;
            if (serviceListener != null) {
                serviceListener.hasControl(value);
            }
        }
    };

    private Context context;

    @Retention(SOURCE)
    @IntDef({
            ServiceStatus.DISCONNECTED,
            ServiceStatus.CONNECTED
    })
    public @interface ServiceStatus {
        int DISCONNECTED = 0;
        int CONNECTED = 1;
        int WRONG_VERSION = 2;
    }

    @Retention(SOURCE)
    @IntDef({
            GNSS_USAGE.GLOBAL,
            GNSS_USAGE.HEADING,
            GNSS_USAGE.LOCAL
    })
    public @interface GNSS_USAGE {
        int GLOBAL = 0;
        int HEADING = 1;
        int LOCAL = 2;
    }

    public DigAssistManager(@NonNull Context c) {
        context = c;
    }

    /**
     * Callback for all status updates.
     */
    public interface DigAssistListener {
        /**
         * Service connection status.
         *
         *  @param  value  status {@link ServiceStatus}.
         */
        void serviceStatus(@ServiceStatus int value);

        /**
         * Auto mode status.
         *
         *  @param  value  status {@link AutoMode}.
         */
        void autoModeStatus(int value);

        /**
         * Fine grading mode status.
         *
         *  @param  value  false = Normal mode, true = Fine grading mode.
         */
        void fineGradingMode(boolean value);

        /**
         * Bucket angle mode status.
         *
         *  @param  value  status {@link BucketAngleMode}.
         */
        void bucketAngleMode(int value);

        /**
         * Active tilt mode status.
         *
         *  @param  value  false = Not active, true = active.
         */
        void activeTiltMode(boolean value);

        /**
         * Back grading mode status.
         *
         *  @param  value  false = Not active, true = active.
         */
        void backGradingMode(boolean value);

        /**
         * Auto active status.
         *
         *  @param  value  false = Not active, true = active.
         */
        void autoActiveStatus(boolean value);

        /**
         * Does this process own control of active control.
         *
         *  @param  value  false = does not own control, true = owns control.
         */
        void hasControl(boolean value);
    }

    /**
     * Binds to service. Must be called before using the methods in this class.
     * Must have permission se.cpacsystems.permission.DIG_ASSIST.
     */
    public boolean connect(@NonNull DigAssistListener sl) {
        serviceListener = sl;
        digAssistConnection = new DigAssistConnection();

        Intent intent = new Intent("se.cpacsystems.guidance.service.GuidanceService.Bind");
        intent.setClassName("se.volvo.ce.doris.mcs2d", "se.cpacsystems.guidance.service.GuidanceService");
        context.startService(intent);
        boolean ret = context.bindService(intent, digAssistConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "DigAssistService bound with " + ret);
        return ret;
    }

    /**
     * Unbinds service. Must be called to tear down service connection.
     */
    public void disconnect() {
        if (digAssistConnection != null) {
            context.unbindService(digAssistConnection);
            digAssistConnection = null;
            Log.d(LOG_TAG, "DigAssistService unbound");
            if (serviceListener != null) {
                serviceListener.serviceStatus(ServiceStatus.DISCONNECTED);
            }
        }
    }

    /**
     * Get version of service.
     *
     * @return Version of service.
     */
    public int getServiceVersion() {
        int serviceVersion = -1;
        try {
            serviceVersion = digAssistService.getVersion();
        } catch (Exception e) {
        }
        return serviceVersion;
    }

    /**
     * Get version of library.
     *
     * @return Version of library.
     */
    public static int getVersion() {
        return VERSION;
    }

    /**
     * Launch Active Control Dialog.
     * Can onyl be called when connected to service.
     *
     * @return True if it was possible to launch dialog.
     */
    public boolean launchActivityActiveControl() {
        boolean res = false;
        if (status == ServiceStatus.CONNECTED) {
            try {
                digAssistService.launchActiveControlDialog();
                res = true;
            } catch (Exception e) {
            }
        }
        return res;
    }

    /**
     * Launch Boundary Dialog.
     * Can onyl be called when connected to service.
     *
     * @return True if it was possible to launch dialog.
     */
    public boolean launchBoundaryDialog() {
        boolean res = false;
        try {
            digAssistService.launchBoundaryDialog();
            res = true;
        } catch (Exception e) {
        }
        return res;
    }

    /**
     * Launch Tools Dialog.
     * Can onyl be called when connected to service.
     *
     * @return True if it was possible to launch dialog.
     */
    public boolean launchToolsDialog() {
        boolean res = false;
        try {
            digAssistService.launchToolsDialog();
            res = true;
        } catch (Exception e) {
        }
        return res;
    }

    /**
     * Send update of active control data.
     * Must only be sent when this process own control.
     * If sent without control, a {@link RuntimeException} will be thrown.
     *
     *  @param  data  data {@link ActiveControlData}.
     */
    public void setActiveControlData(ActiveControlData data) {
        if (hasControl) {
            try {
                digAssistService.setActiveControlData(data);
            } catch (RemoteException e) {
            }
        } else {
            throw new RuntimeException("Tried to send control data without control");
        }
    }

    /**
     * Send update of active tool ref.
     * Must only be sent when this process own control.
     * If sent without control, a {@link RuntimeException} will be thrown.
     *
     *  @param  value  data {@link ToolRef}.
     */
    public void setActiveToolRef(@ToolRef int value) {
        if (hasControl) {
            try {
                digAssistService.setActiveToolRef(value);
            } catch (RemoteException e) {
            }
        } else {
            throw new RuntimeException("Tried to send control data without control");
        }
    }

    /**
     * Send update of operator interrupt.
     * To inform active contrl function to temporarly stop active control.
     * Must only be sent when this process own control.
     * If sent without control, a {@link RuntimeException} will be thrown.
     *
     *  @param  interrupted  false = active control status visable,
     *                       true = active control status NOT visable.
     */
    public void setOperatorInterrupt(boolean interrupted) {
        if (hasControl) {
            try {
                digAssistService.setOperatorInterrupt(interrupted);
            } catch (RemoteException e) {
            }
        } else {
            throw new RuntimeException("Tried to send control data without control");
        }
    }

    private class DigAssistConnection implements ServiceConnection {
        @Override
        public final void onServiceConnected(ComponentName name, IBinder boundService) {
            digAssistService = IDigAssist.Stub.asInterface(boundService);
            try {
                status = digAssistService.registerActiveControlStatusListener(activeControlListener, VERSION);
            } catch (RemoteException e) {
            }
            Log.d(LOG_TAG, "DigAssistService onServiceConnected");
            if (serviceListener != null) {
                serviceListener.serviceStatus(status);
            }
        }

        @Override
        public final void onServiceDisconnected(ComponentName name) {
            digAssistService = null;
            status = ServiceStatus.DISCONNECTED;
            Log.d(LOG_TAG, "DigAssistService onServiceDisconnected");
            if (serviceListener != null) {
                serviceListener.serviceStatus(status);
            }
        }
    }
}
