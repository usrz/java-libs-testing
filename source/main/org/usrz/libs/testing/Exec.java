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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Exec {

    private Exec() {
        throw new IllegalStateException("Do not construct");
    }

    public static final Process exec(String[] commandLine)
    throws IOException {
        return exec(commandLine, System.out, System.err);
    }

    public static final Process exec(String[] commandLine, OutputStream systemOut, OutputStream systemErr)
    throws IOException {
        StringBuilder builder = new StringBuilder("EXECUTING: ");
        for (String string: commandLine) builder.append(' ').append(string);
        System.out.println(builder.toString());

        final Process process = Runtime.getRuntime().exec(commandLine);
        process.getOutputStream().close();

        final Thread copiers[] = new Thread[2];
        copiers[0] = new Thread(new Copier(process.getInputStream(), systemOut, "output"));
        copiers[1] = new Thread(new Copier(process.getErrorStream(), systemErr, "output"));
        for (Thread copier: copiers) copier.start();

        return new WaitingProcess(process, copiers);
    }

    public static final CapturingProcess capture(String[] commandLine)
    throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ByteArrayOutputStream error = new ByteArrayOutputStream();
        return new CapturingProcess(exec(commandLine, output, error), output, error);
    }

    public static final class CapturingProcess extends Process {

        private final Process process;
        private final ByteArrayOutputStream output;
        private final ByteArrayOutputStream error;

        private CapturingProcess(Process process, ByteArrayOutputStream output, ByteArrayOutputStream error) {
            this.process = process;
            this.output = output;
            this.error = error;
        }

        public byte[] getOutputBytes() {
            return output.toByteArray();
        }

        public byte[] getErrorBytes() {
            return error.toByteArray();
        }

        public String getOutput() {
            return new String(output.toByteArray());
        }

        public String getError() {
            return new String(error.toByteArray());
        }

        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("Don't use!");
        }

        public InputStream getInputStream() {
            throw new UnsupportedOperationException("Use \"getOutput()\"");
        }

        public InputStream getErrorStream() {
            throw new UnsupportedOperationException("Use \"getError()\"");
        }

        public int waitFor() throws InterruptedException {
            return process.waitFor();
        }

        public int exitValue() {
            return process.exitValue();
        }

        public void destroy() {
            process.destroy();
        }
    }

    /* ====================================================================== */

    private static final class WaitingProcess extends Process {

        private final Process process;
        private final Thread[] copiers;

        private WaitingProcess(Process process, Thread[] copiers) {
            this.process = process;
            this.copiers = copiers;
        }

        public int waitFor()
        throws InterruptedException {
            for (Thread thread: copiers) {
                thread.join();
            }
            return process.waitFor();
        }

        public int hashCode() {
            return process.hashCode();
        }

        public OutputStream getOutputStream() {
            return process.getOutputStream();
        }

        public InputStream getInputStream() {
            return process.getInputStream();
        }

        public boolean equals(Object obj) {
            return process.equals(obj);
        }

        public InputStream getErrorStream() {
            return process.getErrorStream();
        }

        public int exitValue() {
            return process.exitValue();
        }

        public void destroy() {
            process.destroy();
        }

        public String toString() {
            return process.toString();
        }
    }

    /* ====================================================================== */

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
                IO.copy(input, output, false);
            } catch (IOException exception) {
                System.err.println("ERROR: Exception copying standard " + name);
                exception.printStackTrace(System.err);
            }
        }
    }
}
