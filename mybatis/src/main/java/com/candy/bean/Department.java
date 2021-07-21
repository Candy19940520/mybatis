package com.candy.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author Candy
 * @create 2021-05-24 16:21
 */
@Data
public class Department implements Serializable {

    private static final long serialVersionUID = -605156462007630266L;
    private Integer departmentId;

    private String departmentName;

    private List<Employee> employeeList;

}
