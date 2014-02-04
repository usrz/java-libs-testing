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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Exec {

    private Exec() {
        throw new IllegalStateException("Do not construct");
    }

    public static final Process exec(String[] commandLine)
    throws IOException {
        StringBuilder builder = new StringBuilder("EXECUTING: ");
        for (String string: commandLine) builder.append(' ').append(string);
        System.out.println(builder.toString());

        final Process process = Runtime.getRuntime().exec(commandLine);

        new Thread(new Copier(process.getInputStream(), System.out, "output")).start();
        new Thread(new Copier(process.getErrorStream(), System.err, "output")).start();

        return process;
    }

    private static final class Copier implements Runnable {

        private final InputStream input;
        private final OutputStream output;
        private final String name;

        private Copier(InputStream input, OutputStream output, String name) {
            this.input = input;
            this.output = output;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                IO.copy(input, output);
            } catch (IOException exception) {
                System.err.println("ERROR: Exception copying standard " + name);
                exception.printStackTrace(System.err);
            }
        }
    }
}
