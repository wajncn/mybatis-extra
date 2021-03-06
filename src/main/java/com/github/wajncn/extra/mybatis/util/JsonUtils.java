package com.github.wajncn.extra.mybatis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Json utilities.
 *
 * @author wajncn
 **/
public class JsonUtils {

    /**
     * Default json mapper.
     */
    public final static ObjectMapper DEFAULT_JSON_MAPPER = createDefaultJsonMapper();

    private JsonUtils() {
    }


    /**
     * Creates a default json mapper.
     *
     * @return object mapper
     */
    public static ObjectMapper createDefaultJsonMapper() {
        return createDefaultJsonMapper(null);
    }

    /**
     * Creates a default json mapper.
     *
     * @param strategy property naming strategy
     * @return object mapper
     */
    @NonNull
    public static ObjectMapper createDefaultJsonMapper(@Nullable PropertyNamingStrategy strategy) {
        // Create object mapper
        ObjectMapper mapper = new ObjectMapper();
        // Configure
        // 转换为格式化的json
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 下面配置解决LocalDateTime序列化的问题
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        // Set property naming strategy
        if (strategy != null) {
            mapper.setPropertyNamingStrategy(strategy);
        }
        return mapper;
    }

    /**
     * Converts json to the object specified type.
     *
     * @param json json content must not be blank
     * @param type object type must not be null
     * @param <T>  target object type
     * @return object specified type
     * @throws IOException throws when fail to convert
     */
    @NonNull
    public static <T> T jsonToObject(@NonNull String json, @NonNull Class<T> type) {
        return jsonToObject(json, type, DEFAULT_JSON_MAPPER);
    }


    @NonNull
    public static <T> List<T> jsonToList(@NonNull String json, @NonNull Class<T> type) {
        try {
            return JsonUtils.DEFAULT_JSON_MAPPER.readValue(json, JsonUtils.DEFAULT_JSON_MAPPER.getTypeFactory()
                    .constructParametricType(ArrayList.class, type));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts json to the object specified type.
     *
     * @param json         json content must not be blank
     * @param type         object type must not be null
     * @param objectMapper object mapper must not be null
     * @param <T>          target object type
     * @return object specified type
     * @throws IOException throws when fail to convert
     */
    @NonNull
    public static <T> T jsonToObject(@NonNull String json, @NonNull Class<T> type, @NonNull ObjectMapper objectMapper) {
        Assert.hasText(json, "Json content must not be blank");
        Assert.notNull(type, "Target type must not be null");
        Assert.notNull(objectMapper, "Object mapper must not null");
        try {
            return objectMapper.readValue(json, type);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts object to json format.
     *
     * @param source source object must not be null
     * @return json format of the source object
     * @throws JsonProcessingException throws when fail to convert
     */
    @NonNull
    public static String objectToJson(@NonNull Object source) {
        return objectToJson(source, DEFAULT_JSON_MAPPER);
    }

    /**
     * Converts object to json format.
     *
     * @param source       source object must not be null
     * @param objectMapper object mapper must not be null
     * @return json format of the source object
     */
    @NonNull
    public static String objectToJson(@NonNull Object source, @NonNull ObjectMapper objectMapper) {
        Assert.notNull(source, "Source object must not be null");
        Assert.notNull(objectMapper, "Object mapper must not null");
        try {
            return objectMapper.writeValueAsString(source);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a map to the object specified type.
     *
     * @param sourceMap source map must not be empty
     * @param type      object type must not be null
     * @param <T>       target object type
     * @return the object specified type
     * @throws IOException throws when fail to convert
     */
    @NonNull
    public static <T> T mapToObject(@NonNull Map<String, ?> sourceMap, @NonNull Class<T> type) throws IOException {
        return mapToObject(sourceMap, type, DEFAULT_JSON_MAPPER);
    }

    /**
     * Converts a map to the object specified type.
     *
     * @param sourceMap    source map must not be empty
     * @param type         object type must not be null
     * @param objectMapper object mapper must not be null
     * @param <T>          target object type
     * @return the object specified type
     * @throws IOException throws when fail to convert
     */
    @NonNull
    public static <T> T mapToObject(@NonNull Map<String, ?> sourceMap, @NonNull Class<T> type, @NonNull ObjectMapper objectMapper) throws IOException {
        Assert.notEmpty(sourceMap, "Source map must not be empty");

        // Serialize the map
        String json = objectToJson(sourceMap, objectMapper);

        // Deserialize the json format of the map
        return jsonToObject(json, type, objectMapper);
    }

    /**
     * Converts a source object to a map
     *
     * @param source source object must not be null
     * @return a map
     * @throws IOException throws when fail to convert
     */
    @NonNull
    public static Map<String, ?> objectToMap(@NonNull Object source) throws IOException {
        return objectToMap(source, DEFAULT_JSON_MAPPER);
    }

    /**
     * Converts a source object to a map
     *
     * @param source       source object must not be null
     * @param objectMapper object mapper must not be null
     * @return a map
     * @throws IOException throws when fail to convert
     */
    @NonNull
    public static Map<String, ?> objectToMap(@NonNull Object source, @NonNull ObjectMapper objectMapper) throws IOException {

        // Serialize the source object
        String json = objectToJson(source, objectMapper);

        // Deserialize the json
        return jsonToObject(json, Map.class, objectMapper);
    }
}