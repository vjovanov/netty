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
package io.netty.handler.codec.http.svm;

import io.netty.util.internal.svm.NativeImageBuildOptions;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

/**
 * GraalVM native image Feature for the Netty codec-http module.
 * <p>
 * This Feature configures class initialization timing for HTTP codec and WebSocket classes to ensure
 * proper native image generation and runtime behavior. It is registered automatically via
 * the property-based configuration in {@code native-image.properties} rather than through
 * the legacy service loader mechanism.
 * <p>
 * <b>Registration:</b> This Feature is registered in the module's
 * {@code META-INF/native-image/io.netty/netty-codec-http/native-image.properties} file using
 * the {@code Args} property:
 * <pre>
 * Args = --features=io.netty.handler.codec.http.svm.HttpNativeImageFeature
 * </pre>
 * <p>
 * <b>Class Initialization Strategy:</b>
 * <ul>
 *   <li>Most Netty classes are initialized at build time for optimal performance</li>
 *   <li>Specific encoder and decoder classes are initialized at runtime to ensure proper
 *       initialization of stateful components that depend on runtime environment</li>
 *   <li>This hybrid approach balances performance with correctness</li>
 * </ul>
 *
 * @see NativeImageBuildOptions
 * @see RuntimeClassInitialization
 */
public class HttpNativeImageFeature implements Feature {

    /**
     * Configures class initialization timing for HTTP codec and WebSocket classes before the
     * native image analysis phase begins.
     * <p>
     * This method sets up a hybrid initialization strategy:
     * <ul>
     *   <li>Build-time initialization for most {@code io.netty} classes to improve startup performance</li>
     *   <li>Runtime initialization for specific stateful encoders/decoders that require runtime context</li>
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

        // Build-time initialization
        RuntimeClassInitialization.initializeAtBuildTime(
                "io.netty"
        );

        // Run-time initialization
        RuntimeClassInitialization.initializeAtRunTime(
                "io.netty.handler.codec.http.HttpObjectEncoder",
                "io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder",
                "io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder"
        );
    }
}
