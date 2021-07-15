package tk.mybatis.plus.core;


import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用mapper
 *
 * @author wangjin
 */
public interface CommonPlusMapper<T> extends tk.mybatis.mapper.common.Mapper<T>, MySqlMapper<T> {
}
