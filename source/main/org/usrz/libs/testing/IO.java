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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class IO {

    private IO() {
        throw new IllegalStateException("Do not construct");
    }

    private static final List<File> files;

    static {
        files = new ArrayList<>();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (File file: files) delete(file);
            }
        });
    }

    private static final void delete(File file) {
        if (file.isFile()) {
            if (!file.delete()) System.err.println("Unable to delete file " + file);
        }

        if (file.isDirectory()) {
            for (File f: file.listFiles()) delete(f);
            if (!file.delete()) System.err.println("Unable to delete file " + file);
        }

        if (file.exists()) System.err.println("Unable to delete file" + file);

    }

    /* ====================================================================== *
     * TEMPORARY FILES CREATION                                               *
     * ====================================================================== */

    public static final File makeTempFile()
    throws IOException {
        return makeTempFile(null, null);
    }

    public static final File makeTempFile(String suffix)
    throws IOException {
        return makeTempFile(null, suffix);
    }

    public static final File makeTempFile(String prefix, String suffix)
    throws IOException {
        if (prefix == null) prefix = "tmp-";
        if (prefix.length() == 0) prefix = "tmp-";
        if (!prefix.endsWith("-")) prefix += "-";

        if (suffix == null) suffix = ".tmp";
        if (suffix.length() == 0) suffix = ".tmp";
        if (!suffix.startsWith(".")) suffix = "." + suffix;

        final File file = File.createTempFile(prefix, suffix);
        file.deleteOnExit();
        files.add(file);
        return file.getCanonicalFile();
    }

    public static final File makeTempDir()
    throws IOException {
        return makeTempDir(null, null);
    }

    public static final File makeTempDir(String suffix)
    throws IOException {
        return makeTempDir(null, suffix);
    }

    public static final File makeTempDir(String prefix, String suffix)
    throws IOException {
        if (prefix == null) prefix = "tmp-";
        if (prefix.length() == 0) prefix = "tmp-";
        if (!prefix.endsWith("-")) prefix += "-";

        if (suffix == null) prefix = ".tmp";
        if (prefix.length() == 0) prefix = ".tmp";
        if (!prefix.startsWith(".")) prefix += ".";

        final File file = File.createTempFile(prefix, suffix);
        if (!file.delete()) throw new IOException("Unable to delete file " + file);
        if (!file.mkdirs()) throw new IOException("Unable to create directory " + file);
        file.deleteOnExit();
        files.add(file);
        return file;
    }

    /* ====================================================================== *
     * RESOURCE ACCESS                                                        *
     * ====================================================================== */

    public static final InputStream resource(String resource)
    throws IOException {
        for (StackTraceElement element: new Throwable().getStackTrace()) {
            String className = element.getClassName();
            if (className.equals(IO.class.getName()))
                continue;
            if (className.indexOf('$') >= 0)
                className = className.substring(0, className.indexOf('$'));

            try {
                final Class<?> clazz = Class.forName(className);
                final InputStream input = clazz.getResourceAsStream(resource);
                if (input != null) return input;
                throw new FileNotFoundException("Unable to find resource \"" + resource + "\" for class " + clazz.getName());
            } catch (ClassNotFoundException exception) {
                throw new IOException("Unable to load class " + className, exception);
            }
        }
        throw new FileNotFoundException("Unable to determine class for resource");
    }

    /* ====================================================================== *
     * BASIC STREAMS READING                                                  *
     * ====================================================================== */

    public static final byte[] read(File file)
    throws IOException {
        final FileInputStream input = new FileInputStream(file);
        final byte[] bytes = read(input);
        input.close();
        return bytes;
    }

    public static final byte[] read(URL url)
    throws IOException {
        return read(url.openStream());
    }

    public static final byte[] read(String resource)
    throws IOException {
        return read(resource(resource));
    }

    public static final byte[] read(InputStream input)
    throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /* ====================================================================== *
     * BASIC STREAMS WRITING/COPYING                                          *
     * ====================================================================== */

    public static final void copy(File file, File outputFile)
    throws IOException {
        final FileOutputStream output = new FileOutputStream(outputFile);
        copy(file, output);
        output.close();
    }

    public static final void copy(URL url, File outputFile)
    throws IOException {
        final FileOutputStream output = new FileOutputStream(outputFile);
        copy(url, output);
        output.close();
    }

    public static final void copy(String resource, File outputFile)
    throws IOException {
        final FileOutputStream output = new FileOutputStream(outputFile);
        copy(resource, output);
        output.close();
    }

    public static final void copy(byte[] data, File outputFile)
    throws IOException {
        final FileOutputStream output = new FileOutputStream(outputFile);
        copy(data, output);
        output.close();
    }

    public static final void copy(InputStream input, File outputFile)
    throws IOException {
        final FileOutputStream output = new FileOutputStream(outputFile);
        copy(input, output);
        output.close();
    }

    /* ====================================================================== */

    public static final void copy(File file, OutputStream output)
    throws IOException {
        final FileInputStream input = new FileInputStream(file);
        copy(input, output);
        input.close();
    }

    public static final void copy(URL url, OutputStream output)
    throws IOException {
        copy(url.openStream(), output);
    }

    public static final void copy(String resource, OutputStream output)
    throws IOException {
        copy(resource(resource), output);
    }

    public static final void copy(byte[] data, OutputStream output)
    throws IOException {
        copy(new ByteArrayInputStream(data), output);
    }

    public static final void copy(InputStream input, OutputStream output)
    throws IOException {
        copy(input, output, true);
    }

    public static final void copy(InputStream input, OutputStream output, boolean close)
    throws IOException {
        final byte[] buffer = new byte[4096];
        int read = -1;
        try {
            while ((read = input.read(buffer)) >= 0)
                if (read > 0) output.write(buffer, 0, read);
        } finally {
            if (close) try {
                input.close();
            } finally {
                output.close();
            }
        }
    }

    /* ====================================================================== *
     * COPYING/WRITING TO TEMPORARY FILES                                     *
     * ====================================================================== */

    public static final File copyTempFile(File file)
    throws IOException {
        return copyTempFile(file, null, null);
    }

    public static final File copyTempFile(File file, String suffix)
    throws IOException {
        return copyTempFile(file, null, suffix);
    }

    public static final File copyTempFile(File file, String prefix, String suffix)
    throws IOException {
        final FileInputStream input = new FileInputStream(file);
        final File tempFile = copyTempFile(input, prefix, suffix);
        input.close();
        return tempFile;
    }

    /* ====================================================================== */

    public static final File copyTempFile(URL url)
    throws IOException {
        return copyTempFile(url, null, null);
    }

    public static final File copyTempFile(URL url, String suffix)
    throws IOException {
        return copyTempFile(url, null, suffix);
    }

    public static final File copyTempFile(URL url, String prefix, String suffix)
    throws IOException {
        return copyTempFile(url.openStream(), prefix, suffix);
    }

    /* ====================================================================== */

    public static final File copyTempFile(String resource)
    throws IOException {
        return copyTempFile(resource, null, null);
    }

    public static final File copyTempFile(String resource, String suffix)
    throws IOException {
        return copyTempFile(resource, null, suffix);
    }

    public static final File copyTempFile(String resource, String prefix, String suffix)
    throws IOException {
        return copyTempFile(resource(resource), prefix, suffix);
    }

    /* ====================================================================== */

    public static final File copyTempFile(byte[] data)
    throws IOException {
        return copyTempFile(data, null, null);
    }

    public static final File copyTempFile(byte[] data, String suffix)
    throws IOException {
        return copyTempFile(data, null, suffix);
    }

    public static final File copyTempFile(byte[] data, String prefix, String suffix)
    throws IOException {
        return copyTempFile(new ByteArrayInputStream(data), prefix, suffix);
    }

    /* ====================================================================== */

    public static final File copyTempFile(InputStream input)
    throws IOException {
        return copyTempFile(input, null, null);
    }

    public static final File copyTempFile(InputStream input, String suffix)
    throws IOException {
        return copyTempFile(input, null, suffix);
    }

    public static final File copyTempFile(InputStream input, String prefix, String suffix)
    throws IOException {
        final File file = makeTempFile(prefix, suffix);
        final FileOutputStream output = new FileOutputStream(file);
        copy(input, output);
        output.close();
        return file;
    }

}
