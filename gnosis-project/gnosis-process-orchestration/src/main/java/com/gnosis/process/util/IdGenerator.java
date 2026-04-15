package com.gnosis.process.util;

import cn.hutool.core.util.IdUtil;

public class IdGenerator {

    public static String nextId() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
