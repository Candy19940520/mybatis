package com.candy.mapper;

import com.candy.bean.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 * @author Candy
 * @since 2021-05-13
 */
public interface EmployeeMapper {

    Employee getEmployeeAndDepartmentById(Integer employeeId);
    Employee getEmployeeAndDepartment(Integer employeeId);

    List<Employee> getEmployeeAndDepartmentTwo();

    //键是主键ID(也可以是别的字段)，值是Bean对象
    @MapKey("lastName")
    Map<Integer,Employee> getEmployeeMaps();

    Employee selectEmployee(@Param("employeeId") Integer employeeId,@Param("lastName") String lastName);

    Employee selectOne(Integer employeeId);

    int insertEmployee(Employee employee);
    int updateEmployee(Employee employee);
    int deleteEmployee(Integer employeeId);

    List<Employee> getDepartmentIds(Integer departmentId);

    List<Employee> selectEmployeeList(Employee employee);

    List<Employee> getForeach(@Param("employeeIds") List<Integer> employeeIds);

    int addEmployeeList(@Param("employeeList") List<Employee> employeeList);

    List<Employee> selectBind(String lastName);

}
