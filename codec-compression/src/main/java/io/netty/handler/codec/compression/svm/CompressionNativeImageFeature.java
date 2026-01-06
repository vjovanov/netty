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
package io.netty.handler.codec.compression.svm;

import io.netty.util.internal.svm.NativeImageBuildOptions;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

/**
 * GraalVM native image Feature for the Netty codec-compression module.
 * <p>
 * This Feature configures class initialization timing for compression codec classes to ensure
 * proper native image generation and runtime behavior. It is registered automatically via
 * the property-based configuration in {@code native-image.properties} rather than through
 * the legacy service loader mechanism.
 * <p>
 * <b>Registration:</b> This Feature is registered in the module's
 * {@code META-INF/native-image/io.netty/netty-codec-compression/native-image.properties} file using
 * the {@code Args} property:
 * <pre>
 * Args = --features=io.netty.handler.codec.compression.svm.CompressionNativeImageFeature
 * </pre>
 * <p>
 * <b>Class Initialization Strategy:</b>
 * <ul>
 *   <li>Compression codec classes are initialized at runtime to ensure proper initialization
 *       of native libraries and codec implementations in the target environment</li>
 *   <li>This is particularly important for codecs that load native libraries (e.g., Brotli)
 *       which must be loaded in the actual runtime environment, not at build time</li>
 * </ul>
 *
 * @see NativeImageBuildOptions
 * @see RuntimeClassInitialization
 */
public class CompressionNativeImageFeature implements Feature {

    /**
     * Configures class initialization timing for compression codec classes before the native image
     * analysis phase begins.
     * <p>
     * This method defers the initialization of compression codec classes to runtime to ensure
     * they can properly load native libraries and initialize codec implementations in the actual
     * execution environment.
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
                "io.netty.handler.codec.compression.BrotliDecoder"
        );
    }
}
