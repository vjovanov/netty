/*
 * Copyright 2019 The Netty Project
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

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

public class CommonNativeImageFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        // Run-time initialization
        RuntimeClassInitialization.initializeAtRunTime(
                "io.netty.util.AbstractReferenceCounted",
                "io.netty.util.concurrent.GlobalEventExecutor",
                "io.netty.util.concurrent.ImmediateEventExecutor",
                "io.netty.util.concurrent.ScheduledFutureTask",
                "io.netty.util.internal.ThreadLocalRandom",
                "io.netty.util.NetUtilSubstitutions$NetUtilLocalhost4LazyHolder",
                "io.netty.util.NetUtilSubstitutions$NetUtilLocalhost6LazyHolder",
                "io.netty.util.NetUtilSubstitutions$NetUtilLocalhostLazyHolder",
                "io.netty.util.NetUtilSubstitutions$NetUtilNetworkInterfacesLazyHolder"
        );

        // Build-time initialization
        RuntimeClassInitialization.initializeAtBuildTime(
                "io.netty.util.internal.CleanerJava25"
        );
    }
}
