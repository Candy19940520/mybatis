package com.candy.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 
 * </p>
 *
 * @author Candy
 * @since 2021-05-13
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 5290991031819996566L;
    private Integer employeeId;

    private String lastName;

    private String email;

    private String gender;

    private Integer age;

    private Date createTime;

    private Date updateTime;

    /**
     * 是否删除-1删除  0未删除
     */
    private Integer isDelete;
    private Integer departmentId;

    private Department department;

}
