package com.hyomyang.springaiboot.ai.logger;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;


@Slf4j
public class TestLogger implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {

        log.info("✅ PASSED: " + context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("❌ FAILED: " + context.getDisplayName(), cause);
    }
}
