package com.github.wajncn.ext.mybatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类主要解决索引异常的错误提示消息.
 * 直接将索引的name和错误信息赋予DUPLICATE_MESSAGE_MAP即可
 * 如果没有设置错误消息,则提示程序解析的默认消息.
 * 否则提示DUPLICATE_KEY_MESSAGE
 *
 * @author 王进
 **/
public final class DuplicateKeyExceptionFactory {
    private static final Logger log = LoggerFactory.getLogger(DuplicateKeyExceptionFactory.class);

    private static final Map<String, String> DUPLICATE_MESSAGE_MAP = new HashMap<>();
    private static final String DUPLICATE_KEY_MESSAGE = "请检查数据是否重复";
    private static final Pattern P = Pattern.compile(
            "SQLIntegrityConstraintViolationException: (.*) for key '(.*)'");


    public static class DkeMessage {
        private final String indexName;
        private final String errorMessage;

        DkeMessage(final String indexName, final String errorMessage) {
            this.indexName = indexName;
            this.errorMessage = errorMessage;
        }

        public static DuplicateKeyExceptionFactory.DkeMessage.DkeMessageBuilder builder() {
            return new DuplicateKeyExceptionFactory.DkeMessage.DkeMessageBuilder();
        }

        public String getIndexName() {
            return this.indexName;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public String toString() {
            return "DuplicateKeyExceptionFactory.DkeMessage(indexName=" + this.getIndexName() + ", errorMessage=" + this.getErrorMessage() + ")";
        }

        public static class DkeMessageBuilder {
            private String indexName;
            private String errorMessage;

            DkeMessageBuilder() {
            }

            public DuplicateKeyExceptionFactory.DkeMessage.DkeMessageBuilder indexName(final String indexName) {
                this.indexName = indexName;
                return this;
            }

            public DuplicateKeyExceptionFactory.DkeMessage.DkeMessageBuilder errorMessage(final String errorMessage) {
                this.errorMessage = errorMessage;
                return this;
            }

            public DuplicateKeyExceptionFactory.DkeMessage build() {
                return new DuplicateKeyExceptionFactory.DkeMessage(this.indexName, this.errorMessage);
            }

            public String toString() {
                return "DuplicateKeyExceptionFactory.DkeMessage.DkeMessageBuilder(indexName=" + this.indexName + ", errorMessage=" + this.errorMessage + ")";
            }
        }
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
