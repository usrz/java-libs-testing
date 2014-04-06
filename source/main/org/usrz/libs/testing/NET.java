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
import java.net.InetAddress;
import java.net.ServerSocket;

public class NET {

    private NET() {
        throw new IllegalStateException("Do not construct");
    }

    public static int serverPort()
    throws IOException {
        return serverPort(InetAddress.getLoopbackAddress());
    }

    public static int serverPort(String host)
    throws IOException {
        return serverPort(InetAddress.getByName(host));
    }

    public static int serverPort(InetAddress address)
    throws IOException {
        final ServerSocket socket = new ServerSocket(0, 10, address);
        final int port = socket.getLocalPort();
        socket.close();
        return port;
    }
}
