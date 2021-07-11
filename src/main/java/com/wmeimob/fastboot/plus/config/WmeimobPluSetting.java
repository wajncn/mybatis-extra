package com.wmeimob.fastboot.plus.config;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wajncn
 */
@Slf4j
public class WmeimobPluSetting {

    static String logicDeleteKey = "del";

    static void setLogicDeleteKey(String logicDeleteKey) {
        WmeimobPluSetting.logicDeleteKey = logicDeleteKey;
        log.info("自定义逻辑删除应用成功 key为:{}", logicDeleteKey);
    }

    public static String getLogicDeleteKey() {
        return logicDeleteKey;
    }
}

