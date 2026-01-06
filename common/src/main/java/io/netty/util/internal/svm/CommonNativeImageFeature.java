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

/**
 * GraalVM native image Feature for the Netty common module.
 * <p>
 * This Feature configures class initialization timing for core Netty utility classes to ensure
 * proper native image generation and runtime behavior. It is registered automatically via
 * the property-based configuration in {@code native-image.properties} rather than through
 * the legacy service loader mechanism.
 * <p>
 * <b>Registration:</b> This Feature is registered in the module's
 * {@code META-INF/native-image/io.netty/netty-common/native-image.properties} file using
 * the {@code Args} property:
 * <pre>
 * Args = --features=io.netty.util.internal.svm.CommonNativeImageFeature
 * </pre>
 * <p>
 * <b>Class Initialization Strategy:</b>
 * <ul>
 *   <li>Core utility classes managing reference counting, event executors, and random number
 *       generation are initialized at runtime to ensure proper initialization in the target environment</li>
 *   <li>Platform-specific cleaner implementations are initialized at build time for optimal performance</li>
 *   <li>This hybrid approach ensures both correctness and performance for fundamental Netty utilities</li>
 * </ul>
 *
 * @see NativeImageBuildOptions
 * @see RuntimeClassInitialization
 */
public class CommonNativeImageFeature implements Feature {

    /**
     * Configures class initialization timing for core utility classes before the native image
     * analysis phase begins.
     * <p>
     * This method sets up initialization timing for fundamental Netty utilities:
     * <ul>
     *   <li>Runtime initialization for classes managing state, threading, and network interfaces</li>
     *   <li>Build-time initialization for platform-specific cleaner implementations</li>
     * </ul>
     * <p>
     * The configuration is only applied if {@link NativeImageBuildOptions#shouldApply()} returns
     * {@code true}, allowing for conditional feature activation based on build options.
     *
     * @param access provides access to the native image analysis context
     */
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        if (!NativeImageBuildOptions.shouldApply()) {
            return;
        }

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
