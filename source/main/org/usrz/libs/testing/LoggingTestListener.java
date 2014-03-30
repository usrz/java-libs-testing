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

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.usrz.libs.logging.Log;

public class LoggingTestListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult result) {
        log(result).debug("Running test: \"%s()\"", method(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log(result).info("Test success: \"%s\" %s", method(result), timing(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log(result).warn(result.getThrowable(), "Test failure: \"%s\" %s", method(result), timing(result));
    }

    private final Log log(ITestResult result) {
        return new Log(result.getTestClass().getName());
    }

    private final String method(ITestResult result) {
        return result.getMethod().getMethodName();
    }

    private final String timing(ITestResult result) {
        final long elapsed = result.getEndMillis() - result.getStartMillis();
        final long millis = elapsed % 1000;
        final long seconds = (elapsed - millis) / 1000;
        return String.format(" (%d.%03d sec)", seconds, millis);
    }
}
