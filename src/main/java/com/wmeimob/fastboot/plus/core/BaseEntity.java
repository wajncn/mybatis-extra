package com.wmeimob.fastboot.plus.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wajncn
 */
@Setter
@Getter
public class BaseEntity implements Serializable {

    @Id
    protected Long id;

    @Column(name = "gmt_create")
    protected Date gmtCreate;

    @Column(name = "gmt_modified")
    protected Date gmtModified;
}
