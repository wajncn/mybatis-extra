package com.wmeimob.fastboot.plus.service.impl;

import com.wmeimob.fastboot.plus.core.BaseEntity;
import com.wmeimob.fastboot.plus.core.Mapper;
import com.wmeimob.fastboot.plus.service.CommonPlusService;
import com.wmeimob.fastboot.plus.util.ReflectionKit;
import com.wmeimob.fastboot.plus.util.SqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公共service
 *
 * @author: 王进
 **/
@SuppressWarnings("unchecked")
public class CommonPlusServiceImpl<M extends Mapper<T>, T extends BaseEntity> implements CommonPlusService<T> {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected M baseMapper;

    protected Class<T> entityClass = currentModelClass();
    protected Class<M> mapperClass = currentMapperClass();


    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public M getBaseMapper() {
        return baseMapper;
    }

    /**
     * 获取 entity 的 class
     *
     * @return {@link Class<T>}
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * 批量插入数据
     *
     * @param entitys
     * @return entitys
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<T> entitys) {
        return SqlHelper.retBool(this.getBaseMapper().insertList(entitys));
    }


    protected Class<M> currentMapperClass() {
        return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), CommonPlusServiceImpl.class, 0);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), CommonPlusServiceImpl.class, 1);
    }
}
