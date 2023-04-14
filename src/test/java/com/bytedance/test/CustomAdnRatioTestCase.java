package com.bytedance.test;

import com.bytedance.msdk.util.GMStackUtil;

import org.junit.Assert;
import org.junit.Test;

public class CustomAdnRatioTestCase {
    @Test
    public void testCustomAdnRatio() {
        boolean hit_zero = GMStackUtil.hitCustomAdnSampleRatio(0);
        boolean hit_one = GMStackUtil.hitCustomAdnSampleRatio(1);
        Assert.assertEquals(true, hit_one);
        Assert.assertEquals(false, hit_zero);
    }
}
