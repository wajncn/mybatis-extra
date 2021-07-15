package com.wmeimob.fastboot.plus.service.impl;

import com.wmeimob.fastboot.plus.core.BaseEntity;
import com.wmeimob.fastboot.plus.core.CommonPlusMapper;
import com.wmeimob.fastboot.plus.service.CommonPlusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * 公共service
 *
 * @author: 王进
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class CommonPlusServiceImpl<M extends CommonPlusMapper<T>, T extends BaseEntity> implements CommonPlusService<T> {

    @Autowired
    protected M baseMapper;

    protected Class<T> entityClass = currentModelClass();
    protected Class<M> mapperClass = currentMapperClass();


    /**
     * 获取对应 entity 的 CommonPlusMapper
     *
     * @return CommonPlusMapper
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
    public boolean save(@Nullable List<T> entitys) {
        if (entitys == null || entitys.isEmpty()) {
            return false;
        }
        return this.retBool(this.getBaseMapper().insertList(entitys));
    }

    /**
     * 批量插入数据
     *
     * @param entitys
     * @return entitys
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(@Nullable List<T> entitys) {
        if (entitys == null || entitys.isEmpty()) {
            return false;
        }
        entitys.forEach(this::updateById);
        return true;
    }


    protected Class<M> currentMapperClass() {
        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(ClassUtils.getUserClass(this.getClass()), CommonPlusServiceImpl.class);
        return null == typeArguments ? null : (Class<M>) typeArguments[0];
    }

    protected Class<T> currentModelClass() {
        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(ClassUtils.getUserClass(this.getClass()), CommonPlusServiceImpl.class);
        return null == typeArguments ? null : (Class<T>) typeArguments[1];
    }
}
