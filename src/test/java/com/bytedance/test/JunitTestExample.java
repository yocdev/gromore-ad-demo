package com.bytedance.test;

import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.google.common.truth.Truth;

import org.junit.Test;

public class JunitTestExample {
    @Test
    public void testExample() {
        GMMediationAdSdk.setPangleData("hello, world!");
        String pangleData = GMMediationAdSdk.getPangleData();
        Truth.assertThat(pangleData).isEqualTo("hello, world!");
    }
}
