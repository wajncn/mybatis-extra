package com.wmeimob.fastboot.plus.core;


import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用mapper
 *
 * @author wangjin
 */
public interface CommonPlusMapper<T extends BaseEntity> extends tk.mybatis.mapper.common.Mapper<T>, MySqlMapper<T> {
}
