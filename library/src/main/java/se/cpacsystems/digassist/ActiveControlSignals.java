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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public interface ActiveControlSignals {

    @Retention(SOURCE)
    @IntDef({
            AutoMode.MODE_OFF,
            AutoMode.MODE_AUTO,
            AutoMode.MODE_LEVELING
    })
    @interface AutoMode {
        int MODE_OFF = 0;
        int MODE_AUTO = 1;
        int MODE_LEVELING = 2;
    }

    @Retention(SOURCE)
    @IntDef({
            BucketAngleMode.FIXED,
            BucketAngleMode.CUTTING
    })
    @interface BucketAngleMode {
        int FIXED = 0;
        int CUTTING = 1;
    }

    @Retention(SOURCE)
    @IntDef({
            ToolRef.TOOL_REF_LEFT,
            ToolRef.TOOL_REF_RIGHT,
            ToolRef.TOOL_REF_MIDDLE
    })
    @interface ToolRef {
        int TOOL_REF_LEFT = 0;
        int TOOL_REF_RIGHT = 1;
        int TOOL_REF_MIDDLE = 2;
    }

    @Retention(SOURCE)
    @IntDef({
            BucketRecall.IGNORE,
            BucketRecall.INACTIVE,
            BucketRecall.ACTIVE
    })
    public @interface BucketRecall {
        int IGNORE = 0;
        int INACTIVE = 1;
        int ACTIVE = 2;
    }
}
