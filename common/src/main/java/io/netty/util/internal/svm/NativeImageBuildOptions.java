/*
 * Copyright 2026 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal.svm;

import java.util.concurrent.atomic.AtomicBoolean;

final class NativeImageBuildOptions {

    private static final String AVOID_BUILD_TIME_INIT = "io.netty.avoidBuildTimeInit";
    private static final boolean SHOULD_APPLY = System.getProperty(AVOID_BUILD_TIME_INIT) == null;
    private static final AtomicBoolean LOGGED = new AtomicBoolean();

    private NativeImageBuildOptions() {
    }

    static boolean shouldApply() {
        if (SHOULD_APPLY) {
            warn();
        }
        return SHOULD_APPLY;
    }

    static void warn() {
        if (SHOULD_APPLY && LOGGED.compareAndSet(false, true)) {
            System.out.println("In the future releases Netty will start using run-time initialization. Please use '-Dio.netty.avoidBuildTimeInit=true' to prepare for that change.");
        }
    }
}
