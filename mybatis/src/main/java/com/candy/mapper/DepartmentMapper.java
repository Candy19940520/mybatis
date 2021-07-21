package com.candy.mapper;

import com.candy.bean.Department;

/**
 * <p>
 *  Mapper 接口
 * </p>
 * @author Candy
 * @since 2021-05-13
 */
public interface DepartmentMapper {

    Department getDepartmentById(Integer departmentId);

    Department getDepartment(Integer departmentId);


}
