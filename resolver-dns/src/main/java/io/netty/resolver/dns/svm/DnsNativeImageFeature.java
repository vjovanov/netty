/*
 * Copyright 2024 The Netty Project
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
package io.netty.resolver.dns.svm;

import io.netty.util.internal.svm.NativeImageBuildOptions;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

/**
 * GraalVM native image Feature for the Netty resolver-dns module.
 * <p>
 * This Feature configures class initialization timing for DNS resolver classes to ensure
 * proper native image generation and runtime behavior. It is registered automatically via
 * the property-based configuration in {@code native-image.properties} rather than through
 * the legacy service loader mechanism.
 * <p>
 * <b>Registration:</b> This Feature is registered in the module's
 * {@code META-INF/native-image/io.netty/netty-resolver-dns/native-image.properties} file using
 * the {@code Args} property:
 * <pre>
 * Args = --features=io.netty.resolver.dns.svm.DnsNativeImageFeature
 * </pre>
 * <p>
 * <b>Class Initialization Strategy:</b>
 * <ul>
 *   <li>DNS resolver classes are initialized at runtime to ensure proper initialization
 *       of DNS server addresses, resolv.conf parsing, and hosts file resolution</li>
 *   <li>This is critical because DNS configuration is platform and environment-specific
 *       and must be read at runtime, not baked into the native image at build time</li>
 *   <li>Name resolution behavior depends on the actual runtime environment's network configuration</li>
 * </ul>
 *
 * @see NativeImageBuildOptions
 * @see RuntimeClassInitialization
 */
public class DnsNativeImageFeature implements Feature {
    /**
     * Configures class initialization timing for DNS resolver classes before the native image
     * analysis phase begins.
     * <p>
     * This method defers the initialization of DNS resolver classes to runtime to ensure
     * they can properly read DNS configuration from the target environment. This includes:
     * <ul>
     *   <li>DNS server address discovery and configuration</li>
     *   <li>Resolv.conf file parsing (on Unix-like systems)</li>
     *   <li>Hosts file entries resolution</li>
     *   <li>DNS name resolver initialization and builder configuration</li>
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
        RuntimeClassInitialization.initializeAtRunTime(
                "io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider",
                "io.netty.resolver.dns.DnsServerAddressStreamProviders$DefaultProviderHolder",
                "io.netty.resolver.dns.DnsNameResolver",
                "io.netty.resolver.dns.DnsNameResolverBuilder",
                "io.netty.resolver.HostsFileEntriesResolver",
                "io.netty.resolver.dns.ResolvConf$ResolvConfLazy");
    }
}
