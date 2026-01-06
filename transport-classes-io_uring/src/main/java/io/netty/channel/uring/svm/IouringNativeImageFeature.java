/*
 * Copyright 2025 The Netty Project
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
package io.netty.channel.uring.svm;

import io.netty.util.internal.svm.NativeImageBuildOptions;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

/**
 * GraalVM native image Feature for the Netty transport-classes-io_uring module.
 * <p>
 * This Feature configures class initialization timing for io_uring transport classes to ensure
 * proper native image generation and runtime behavior. It is registered automatically via
 * the property-based configuration in {@code native-image.properties} rather than through
 * the legacy service loader mechanism.
 * <p>
 * <b>Registration:</b> This Feature is registered in the module's
 * {@code META-INF/native-image/io.netty/netty-transport-classes-io_uring/native-image.properties} file using
 * the {@code Args} property:
 * <pre>
 * Args = --features=io.netty.channel.uring.svm.IouringNativeImageFeature
 * </pre>
 * <p>
 * <b>Class Initialization Strategy:</b>
 * <ul>
 *   <li>All io_uring transport classes are initialized at runtime to ensure proper initialization
 *       of native libraries and system calls specific to the Linux io_uring API</li>
 *   <li>Unix utility classes (limits, errors, IOV arrays) are also initialized at runtime
 *       as they depend on platform-specific system configurations</li>
 *   <li>This is critical for Linux-specific transport functionality that relies on modern
 *       kernel features (io_uring) that must be initialized in the target environment</li>
 * </ul>
 *
 * @see NativeImageBuildOptions
 * @see RuntimeClassInitialization
 */
public class IouringNativeImageFeature implements Feature {

    /**
     * Configures class initialization timing for io_uring transport classes before the native image
     * analysis phase begins.
     * <p>
     * This method defers the initialization of all io_uring transport classes to runtime to ensure
     * they can properly load native libraries and initialize Linux-specific io_uring functionality.
     * This includes both the io_uring-specific classes and common Unix utility classes that provide
     * low-level platform integration.
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
                "io.netty.channel.uring",
                "io.netty.channel.unix.Limits",
                "io.netty.channel.unix.IovArray",
                "io.netty.channel.unix.Errors"
        );
    }
}
