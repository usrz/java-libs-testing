/* ========================================================================== *
 * Copyright 2014 USRZ.com and Pier Paolo Fumagalli                           *
 * -------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *  http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * ========================================================================== */
package org.usrz.libs.testing;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.usrz.libs.logging.Logging;

@Listeners(LoggingTestListener.class)
public abstract class AbstractTest extends Assert {

    static {
        try {
            Logging.init();
        } catch (Throwable throwable) {
            /* Simply no logging in the project, ignore */
        }
    }

    protected AbstractTest() {
        /* Nothing to do */
    }

}
