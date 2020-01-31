/**
 *    Copyright 2019 Cpac Systems AB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package se.cpacsystems.digassist;

import android.os.Parcel;
import android.os.Parcelable;

public class ActiveControlData implements Parcelable, ActiveControlSignals {

    public static final int FWD_DIST = 0;
    public static final int FWD_ANGLE = 1;
    public static final int BCK_DIST = 2;
    public static final int BCK_ANGLE = 3;
    public static final int RGT_DIST = 4;
    public static final int RGT_ANGLE = 5;
    public static final int LFT_DIST = 6;
    public static final int LFT_ANGLE = 7;
    public static final int PLANE_EQ_A = 8;
    public static final int PLANE_EQ_B = 9;
    public static final int PLANE_EQ_C = 10;
    public static final int PLANE_EQ_D = 11;
    public static final int TOOL_DIST = 12;

    private static final int NO_OF_PARAMS = 13;

    /**
     * Should be assigned to value when not defined.
     */
    public static final double UNDEFINED = 999999;

    private double[] distsAndAngles = new double[NO_OF_PARAMS];

    private int activeToolRef = ToolRef.TOOL_REF_LEFT;


    /**
     * Constructor for ActiveControlData.
     *
     * Sets all paramters to undefined
     */
    public ActiveControlData() {
        for (int i = 0; i < NO_OF_PARAMS; i++) {
            distsAndAngles[i] = UNDEFINED;
        }
    }
    /**
     * Constructor for ActiveControlData.
     *
     * @param in reads from parcel
     */
    public ActiveControlData(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Constructor for ActiveControlData
     *
     *  @param distsAndAngles set from array
     */
    public ActiveControlData(double[] distsAndAngles) {
        this.distsAndAngles = distsAndAngles;
    }

    public static final Parcelable.Creator<ActiveControlData> CREATOR = new Parcelable.Creator<ActiveControlData>() {
        @Override
        public ActiveControlData createFromParcel(Parcel in) {
            return new ActiveControlData(in);
        }

        @Override
        public ActiveControlData[] newArray(int size) {
            return new ActiveControlData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(distsAndAngles);
        dest.writeInt(activeToolRef);
    }

    private void readFromParcel(Parcel in) {
        in.readDoubleArray(distsAndAngles);
        activeToolRef = in.readInt();
    }

    public void setDistAndAngles(double[] values) {
        distsAndAngles = values;
    }

    public double getPlaneEquationA() {
        return distsAndAngles[PLANE_EQ_A];
    }
    public double getPlaneEquationB() {
        return distsAndAngles[PLANE_EQ_B];
    }
    public double getPlaneEquationC() {
        return distsAndAngles[PLANE_EQ_C];
    }
    public double getPlaneEquationD() {
        return distsAndAngles[PLANE_EQ_D];
    }

    public double getDistFwd() {
        return distsAndAngles[FWD_DIST];
    }
    public double getDistBck() {
        return distsAndAngles[BCK_DIST];
    }
    public double getDistLft() {
        return distsAndAngles[LFT_DIST];
    }
    public double getDistRgt() {
        return distsAndAngles[RGT_DIST];
    }

    public double getAngleFwd() {
        return distsAndAngles[FWD_ANGLE];
    }
    public double getAngleBck() {
        return distsAndAngles[BCK_ANGLE];
    }
    public double getAngleLft() {
        return distsAndAngles[LFT_ANGLE];
    }
    public double getAngleRgt() {
        return distsAndAngles[RGT_ANGLE];
    }

    public double getDistTool() {
        return distsAndAngles[TOOL_DIST];
    }

    public void setPlaneEquationA(double value) {
        distsAndAngles[PLANE_EQ_A] = value;
    }
    public void setPlaneEquationB(double value) {
        distsAndAngles[PLANE_EQ_B] = value;
    }
    public void setPlaneEquationC(double value) {
        distsAndAngles[PLANE_EQ_C] = value;
    }
    public void setPlaneEquationD(double value) {
        distsAndAngles[PLANE_EQ_D] = value;
    }

    public void setDistFwd(double value) {
        distsAndAngles[FWD_DIST] = value;
    }
    public void setDistBck(double value) {
        distsAndAngles[BCK_DIST] = value;
    }
    public void setDistLft(double value) {
        distsAndAngles[LFT_DIST] = value;
    }
    public void setDistRgt(double value) {
        distsAndAngles[RGT_DIST] = value;
    }

    public void setAngleFwd(double value) {
        distsAndAngles[FWD_ANGLE] = value;
    }
    public void setAngleBck(double value) {
        distsAndAngles[BCK_ANGLE] = value;
    }
    public void setAngleLft(double value) {
        distsAndAngles[LFT_ANGLE] = value;
    }
    public void setAngleRgt(double value) {
        distsAndAngles[RGT_ANGLE] = value;
    }

    public void setDistTool(double value) {
        distsAndAngles[TOOL_DIST] = value;
    }
}
