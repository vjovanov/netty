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
