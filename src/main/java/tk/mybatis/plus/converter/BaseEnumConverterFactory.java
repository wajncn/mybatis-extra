package tk.mybatis.plus.converter;

import tk.mybatis.plus.core.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * 通用枚举转换器 可以接受枚举的字符串或者{@link BaseEnum#getValue()}的实现
 *
 * @author: wajncn
 **/
@SuppressWarnings("unchecked")
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum<?>> {

    /**
     * 目标类型与对应转换器的Map
     */
    private static final Map<Class, Converter> CONVERTER_MAP = new HashMap<>(64);

    @Override
    public <T extends BaseEnum<?>> Converter<String, T> getConverter(Class<T> aClass) {
        Converter converter = CONVERTER_MAP.get(aClass);
        if (converter == null) {
            converter = new IntegerStrToEnumConverter<>(aClass);
            CONVERTER_MAP.put(aClass, converter);
        }
        return converter;
    }


    /**
     * 将int对应的字符串转换为目标类型的转换器
     *
     * @param <T> 目标类型（BaseEnum的实现类）
     */
    static class IntegerStrToEnumConverter<T extends BaseEnum<?>> implements Converter<String, T> {
        private Map<String, T> enumMap = new HashMap<>();

        private IntegerStrToEnumConverter(Class<T> enumType) {
            T[] enums = enumType.getEnumConstants();
            if (enums == null) {
                return;
            }
            for (T e : enums) {
                enumMap.put(e.getValue() + "", e);
                //从枚举字面量反序列回枚
                //是Spring默认的方案
                //此处添加可避免下面convert方法抛出IllegalArgumentException异常后被系统捕获再次调用默认方案
                enumMap.put(((Enum) e).name() + "", e);
            }
        }

        @Override
        public T convert(String source) {
            T result = enumMap.get(source);
            if (result == null) {
                //抛出该异常后，会调用 spring 的默认转换方案，即使用 枚举字面量进行映射
                throw new IllegalArgumentException("No element matches " + source);
            }
            return result;
        }
    }

}
 