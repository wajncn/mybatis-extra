package com.wmeimob.fastboot.plus.service;

import cn.hutool.core.util.ReflectUtil;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import tk.mybatis.mapper.entity.Example;
import com.wmeimob.fastboot.plus.config.WmeimobPluSetting;
import com.wmeimob.fastboot.plus.core.BaseEntity;
import com.wmeimob.fastboot.plus.core.Mapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 公共service
 *
 * @author: 王进
 **/
public interface CommonPlusService<T extends BaseEntity> {

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    default boolean retBool(Integer result) {
        return null != result && result >= 1;
    }


    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    default Long retCount(Integer result) {
        return (null == result) ? 0L : result;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    Mapper<T> getBaseMapper();


    /**
     * 获取 entity 的 class
     *
     * @return {@link Class<T>}
     */
    Class<T> getEntityClass();


    /**
     * 批量插入数据
     *
     * @param entitys
     * @return entitys
     */
    boolean saveBatch(@NonNull List<T> entitys);

    /**
     * 如果属性有id则修改.否则新增
     *
     * @param entity 实体对象 不能为null
     * @return entity 实体对象
     */
    default boolean saveOrUpdate(@NonNull T entity) {
        if (entity.getId() == null) {
            return this.save(entity);
        } else {
            return this.updateById(entity);
        }
    }


    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     * @return entity 实体对象
     */
    default boolean save(@NonNull T entity) {
        return this.retBool(getBaseMapper().insertSelective(entity));
    }


    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    default boolean removeById(@NonNull Serializable id) {
        return this.retBool(getBaseMapper().deleteByPrimaryKey(id));
    }


    /**
     * 根据 ID 选择修改
     *
     * @param entity 实体对象
     */
    default boolean updateById(T entity) {
        return this.retBool(getBaseMapper().updateByPrimaryKeySelective(entity));
    }


    /**
     * list
     *
     * @return
     */
    default List<T> list() {
        return this.listByExample(null);
    }


    /**
     * 通过ID查询
     *
     * @param id id
     * @return entity
     */
    default T getById(@NonNull Long id) {
        Example example = this.getExample();
        example.createCriteria().andEqualTo("id", id);
        return this.getBaseMapper().selectOneByExample(example);
    }


    /**
     * 查询一条记录
     *
     * @param entity 实体对象
     * @return entity
     */
    default T getOne(@NonNull T entity) {
        Field field = ReflectUtil.getField(entity.getClass(), WmeimobPluSetting.getLogicDeleteKey());
        if (field != null) {
            field.setAccessible(true);
            if (Boolean.class.isAssignableFrom(field.getType())) {
                try {
                    field.setBoolean(entity, false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (Long.class.isAssignableFrom(field.getType())) {
                try {
                    field.setLong(entity, 0L);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (Integer.class.isAssignableFrom(field.getType())) {
                try {
                    field.setLong(entity, 0);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.getBaseMapper().selectOne(entity);
    }


    /**
     * 获取当前实体对象的Example
     *
     * @return Example
     */
    default Example getExample() {
        Class<T> entityClass = this.getEntityClass();
        Example example = new Example(entityClass);

        //加入逻辑删除
        if (ReflectUtil.getField(entityClass, WmeimobPluSetting.getLogicDeleteKey()) != null) {
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(WmeimobPluSetting.getLogicDeleteKey(), 0);
            example.and(criteria);
        }
        return example;
    }


    /**
     * 通过Example模式查询没有删除逇数据.
     * 如果属性没有del字段.则忽略
     * 参数为null则查询所有
     *
     * @param example 可以为null
     * @return List<T>
     */
    default List<T> listByExample(@Nullable Example example) {
        Class<T> entityClass = this.getEntityClass();
        Mapper<T> mapper = this.getBaseMapper();
        if (example == null) {
            example = new Example(entityClass);
        }

        //加入逻辑删除
        if (ReflectUtil.getField(entityClass, WmeimobPluSetting.getLogicDeleteKey()) != null) {
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(WmeimobPluSetting.getLogicDeleteKey(), 0);
            example.and(criteria);
        }
        return mapper.selectByExample(example);
    }


}
