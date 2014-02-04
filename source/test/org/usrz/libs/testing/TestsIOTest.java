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
import java.util.concurrent.Callable;

import org.testng.annotations.Test;

public class TestsIOTest extends AbstractTest {

    @Test
    public void testResource()
    throws Exception {
        assertNotNull(IO.resource("foobar.txt"));
    }

    @Test
    public void testResourceAnonymousInnerClass()
    throws Exception {
        new Callable<Void>() {
            public Void call()
            throws Exception {
                assertNotNull(IO.resource("foobar.txt"));
                return null;
            }
        }.call();
    }

    @Test
    public void testResourceDoubleAnonymousInnerClass()
    throws Exception {
        new Callable<Void>() {
            public Void call()
            throws Exception {
                return new Callable<Void>() {
                    public Void call()
                    throws Exception {
                        assertNotNull(IO.resource("foobar.txt"));
                        return null;
                    }
                }.call();
            }
        }.call();
    }

    @Test
    public void testResourceInnerClass1()
    throws Exception {
        new Inner1().call();
    }

    @Test
    public void testResourceInnerClass2()
    throws Exception {
        new Inner2().call();
    }

    @Test
    public void testResourceInnerClass3()
    throws Exception {
        new Inner3().call();
    }

    @Test(expectedExceptions=IOException.class,
          expectedExceptionsMessageRegExp="^Unable to find resource.*")
    public void testResourceFail()
    throws Exception {
        assertNotNull(IO.resource("notfound"));
    }

    /* ====================================================================== */

    public static class Inner1 implements Callable<Void> {
        public Void call()
        throws Exception {
            assertNotNull(IO.resource("foobar.txt"));
            return null;
        }
    }

    public static class Inner2 implements Callable<Void> {

        public static class Inner implements Callable<Void> {
            public Void call()
            throws Exception {
                assertNotNull(IO.resource("foobar.txt"));
                return null;
            }
        }

        public Void call()
        throws Exception {
            return new Inner().call();
        }
    }

    public static class Inner3 implements Callable<Void> {
        public Void call()
        throws Exception {
            return new Callable<Void>() {
                public Void call()
                throws Exception {
                    assertNotNull(IO.resource("foobar.txt"));
                    return null;
                }
            }.call();
        }
    }

}
