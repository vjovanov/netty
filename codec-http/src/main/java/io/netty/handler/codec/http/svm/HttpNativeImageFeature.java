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

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

public class HttpNativeImageFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
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
