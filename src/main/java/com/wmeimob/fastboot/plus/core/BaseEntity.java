package com.wmeimob.fastboot.plus.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wmeimob.fastboot.plus.util.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wajncn
 */
@Setter
@Getter
public abstract class BaseEntity implements Serializable {

    protected Serializable id;

    @Column(name = "gmt_create")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime gmtCreate;

    @Column(name = "gmt_modified")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime gmtModified;

    @Override
    public String toString() {
        return JsonUtils.objectToJson(this);
    }
}
