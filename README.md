# mybatis-expand(基于mybatis拓展handler)

## 针对mybatis增加了通用枚举处理器,通用list处理器,Optional处理器。

```java

package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Optional;

@Table(name = "userinfo")
public class Userinfo {
    public Userinfo(String optionalString) {
        this.optionalString = optionalString;
    }

    public Userinfo() {
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "optional_string")
    private String optionalString;

    @Column(name = "optional_integer")
    private Integer optionalInteger;

    @Column(name = "optional_int")
    private int optionalInt;

    @Column(name = "optional_long")
    private long optionalLong;

    @Column(name = "optional_decimal")
    private BigDecimal optionalDecimal;

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }

    public Userinfo setId(Integer id) {
        this.id = id;
        return this;
    }

    public Optional<String> getOptionalString() {
        return Optional.ofNullable(optionalString);
    }

    public Userinfo setOptionalString(String optionalString) {
        this.optionalString = optionalString;
        return this;
    }

    public Optional<Integer> getOptionalInteger() {
        return Optional.ofNullable(optionalInteger);
    }

    public Userinfo setOptionalInteger(Integer optionalInteger) {
        this.optionalInteger = optionalInteger;
        return this;
    }

    public int getOptionalInt() {
        return optionalInt;
    }

    public Userinfo setOptionalInt(int optionalInt) {
        this.optionalInt = optionalInt;
        return this;
    }

    public long getOptionalLong() {
        return optionalLong;
    }

    public Userinfo setOptionalLong(long optionalLong) {
        this.optionalLong = optionalLong;
        return this;
    }

    public Optional<BigDecimal> getOptionalDecimal() {
        return Optional.ofNullable(optionalDecimal);
    }

    public Userinfo setOptionalDecimal(BigDecimal optionalDecimal) {
        this.optionalDecimal = optionalDecimal;
        return this;
    }
}

```

> 可以支持实体对象的get方法为optional. 详细参考:OptionalTypeHandler处理器   
> 也可以支持自定义枚举类(通用枚举). 实现BaseEnum接口即可. 详细参考:MybatisEnumTypeHandler处理器

## get方法的template

```textmate
#if($field.modifierStatic)
static ##
#end
#if ($field.primitive)
$field.type ##
#else
java.util.Optional<$field.type> ##
#end
#if($field.recordComponent)
    ${field.name}##
#else
    #set($name = $StringUtil.capitalizeWithJavaBeanConvention($StringUtil.sanitizeJavaIdentifier($helper.getPropertyName($field, $project))))
    #if ($field.boolean && $field.primitive)
    is${name}##
    #else
    get${name}##
    #end
#end
() {
#if ($field.primitive)
return $field.name;
#else
return java.util.Optional.ofNullable($field.name);
#end
}
```
