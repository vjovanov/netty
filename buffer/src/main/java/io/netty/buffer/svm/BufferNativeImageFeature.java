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
package io.netty.buffer.svm;

import io.netty.util.internal.svm.NativeImageBuildOptions;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

/**
 * GraalVM native image Feature for the Netty buffer module.
 * <p>
 * This Feature configures class initialization timing for buffer-related classes to ensure
 * proper native image generation and runtime behavior. It is registered automatically via
 * the property-based configuration in {@code native-image.properties} rather than through
 * the legacy service loader mechanism.
 * <p>
 * <b>Registration:</b> This Feature is registered in the module's
 * {@code META-INF/native-image/io.netty/netty-buffer/native-image.properties} file using
 * the {@code Args} property:
 * <pre>
 * Args = --features=io.netty.buffer.svm.BufferNativeImageFeature
 * </pre>
 * <p>
 * <b>Class Initialization Strategy:</b>
 * <ul>
 *   <li>Buffer allocation and management classes are initialized at runtime to ensure proper
 *       initialization of allocators and reference counting mechanisms in the target environment</li>
 *   <li>This prevents issues with pre-initialized state that may not be valid across different
 *       runtime contexts</li>
 * </ul>
 *
 * @see NativeImageBuildOptions
 * @see RuntimeClassInitialization
 */
public class BufferNativeImageFeature implements Feature {

    /**
     * Configures class initialization timing for buffer-related classes before the native image
     * analysis phase begins.
     * <p>
     * This method defers the initialization of key buffer classes to runtime to ensure they are
     * properly initialized in the actual execution environment. This is critical for classes that
     * manage memory allocation, reference counting, and buffer pooling.
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
                "io.netty.buffer.AbstractReferenceCountedByteBuf",
                "io.netty.buffer.AdaptiveByteBufAllocator",
                "io.netty.buffer.ByteBufAllocator",
                "io.netty.buffer.ByteBufUtil",
                "io.netty.buffer.PooledByteBufAllocator"
        );
    }
}
