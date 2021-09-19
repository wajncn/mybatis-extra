package com.github.wajncn.ext.mybatis.core;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类主要解决索引异常的错误提示消息.  直接将索引的name和错误信息赋予DUPLICATE_MESSAGE_MAP即可
 * 如果没有设置错误消息,则提示程序解析的默认消息.
 * 否则提示DUPLICATE_KEY_MESSAGE
 *
 * @author 王进
 **/
@Slf4j
public final class DuplicateKeyExceptionFactory {

    private static final Map<String, String> DUPLICATE_MESSAGE_MAP = new HashMap<>();
    private static final String DUPLICATE_KEY_MESSAGE = "请检查数据是否重复";
    private static final Pattern P = Pattern.compile(
            "SQLIntegrityConstraintViolationException: (.*) for key '(.*)'");


    @Getter
    @Builder
    @ToString
    public static class DkeMessage {
        private final String indexName;
        private final String errorMessage;
    }

    /**
     * 添加自定义的索引异常提示语
     *
     * @param message message
     */
    public static void addMessage(@NonNull DkeMessage message) {
        DUPLICATE_MESSAGE_MAP.put(message.getIndexName(), message.getErrorMessage());
    }

    /**
     * 根据索引名称获取消息.
     *
     * @param indexName indexName
     * @ message
     */
    public static String getMessage(@NonNull String indexName) {
        return DUPLICATE_MESSAGE_MAP.getOrDefault(indexName, DUPLICATE_KEY_MESSAGE);
    }

    /**
     * 根据异常消息解析索引和错误消息
     *
     * @param message 异常的消息 e.getMessage
     * @return message
     */
    public static String parseMessage(@Nullable String message) {
        if (message == null) {
            return DUPLICATE_KEY_MESSAGE;
        }
        Matcher matcher = P.matcher(message);
        if (!matcher.find()) {
            return DUPLICATE_KEY_MESSAGE;
        }
        return Optional.ofNullable(DUPLICATE_MESSAGE_MAP.get(matcher.group(2)))
                .orElse(matcher.group(1).replace("Duplicate entry", "重复的属性:"));
    }
}
