package com.candy.TypeHandler;

import com.candy.bean.EmpStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Candy
 * @create 2021-06-04 16:19
 * 自定义的类型处理器
 */
public class MyTypeHandler implements TypeHandler<EmpStatus> {

    //定义当前数据如何保存到数据库中
    @Override
    public void setParameter(PreparedStatement ps, int i, EmpStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i,parameter.getCode().toString());
    }

    //设置参数返回一个枚举对象;因为Bean封装的是一个枚举对象属性
    @Override
    public EmpStatus getResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        EmpStatus empStatusByCode = EmpStatus.getEmpStatusByCode(code);
        return empStatusByCode;
    }

    @Override
    public EmpStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        EmpStatus empStatusByCode = EmpStatus.getEmpStatusByCode(code);
        return empStatusByCode;
    }

    //存储过程
    @Override
    public EmpStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        EmpStatus empStatusByCode = EmpStatus.getEmpStatusByCode(code);
        return empStatusByCode;
    }
}
