package com.wmeimob.fastboot.plus.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wajncn
 */
@Setter
@Getter
public class BaseEntity implements Serializable {

    @Id
    protected Serializable id;

    @Column(name = "gmt_create")
    protected LocalDateTime gmtCreate;

    @Column(name = "gmt_modified")
    protected LocalDateTime gmtModified;
}
