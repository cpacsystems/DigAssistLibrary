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

import se.cpacsystems.digassist.IDigAssistListener;
import se.cpacsystems.digassist.ActiveControlData;

interface IDigAssist {

    int getVersion();

    int registerActiveControlStatusListener(in IDigAssistListener listener, in int version);
    void unregisterActiveControlStatusListener(in IDigAssistListener listener);

    void launchActiveControlDialog();
    void launchBoundaryDialog();
    void launchToolsDialog();

    void setActiveControlData(in ActiveControlData data);

    void setActiveToolRef(in int value);

    void setOperatorInterrupt(in boolean value);
}
