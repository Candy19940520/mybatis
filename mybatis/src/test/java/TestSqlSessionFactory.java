import com.candy.bean.Department;
import com.candy.bean.Employee;
import com.candy.mapper.DepartmentMapper;
import com.candy.mapper.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Candy
 * @create 2021-05-13 17:46
 */
public class TestSqlSessionFactory {


    /**
     * 1.通过配置文件获取SqlSessionFactory
     * 2.通过SqlSessionFactory获取SqlSession（和数据库得一次会话就是SqlSession）
     * 3.通过SqlSession得到对呀得Mapper执行SQL
     * @throws Exception
     */
    @Test
    public void sqlSessionFactory() throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        Employee employee;
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
//            employee = mapper.selectOne(4);
//            System.out.println(employee);
//
//            Employee employee1 = mapper.selectEmployee(1, "Tom");
//            System.out.println(employee1);

//            Map<Integer, Employee> employeeMaps = mapper.getEmployeeMaps();
//            System.out.println(employeeMaps);

//            Employee employeeAndDepartment = mapper.getEmployeeAndDepartment(26);
//            System.out.println(employeeAndDepartment);

//            List<Employee> employeeAndDepartmentTwo = mapper.getEmployeeAndDepartmentTwo();
//            System.out.println(employeeAndDepartmentTwo);

//            Employee employee1 = mapper.getEmployeeAndDepartmentById(22);
//            System.out.println(employee1);

//            Employee employeeSelect = new Employee();
//            employeeSelect.setEmployeeId(1);
//            employeeSelect.setLastName("Tom");
//            List<Employee> employees = mapper.selectEmployeeList(employeeSelect);
//            System.out.println(employees);


//            List<Employee> foreach = mapper.getForeach(Arrays.asList(1,2,3,4));
//            for (Employee foreach1 : foreach) {
//                System.out.println(foreach1);
//            }


            Employee employeeAdd = new Employee();
            employeeAdd.setLastName("孙健");
            employeeAdd.setEmail("sunjian@qq.com");
            employeeAdd.setGender("1");
            employeeAdd.setAge(30);
            employeeAdd.setCreateTime(new Date());
            employeeAdd.setDepartmentId(2);

            Employee employeeAdd2 = new Employee();
            employeeAdd2.setLastName("孙健2");
            employeeAdd2.setEmail("sunjian2@qq.com");
            employeeAdd2.setGender("1");
            employeeAdd2.setAge(31);
            employeeAdd2.setCreateTime(new Date());
            employeeAdd2.setDepartmentId(1);

            List<Employee> employeeList = new ArrayList<>();
            employeeList.add(employeeAdd);
            employeeList.add(employeeAdd2);
            mapper.addEmployeeList(employeeList);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void resultMapTest() throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);


//        Department department = mapper.getDepartment(1);
//        System.out.println(department);

        Department departmentById = mapper.getDepartmentById(1);
        System.out.println(departmentById);
    }

    @Test
    public void testInsert() throws Exception {
        //获取的SqlSessionFactory不会自动提交数据
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);

        Employee employee = new Employee();
        employee.setLastName("张凯");
        employee.setEmail("zhangkai@qq.com");
        employee.setGender("1");
        employee.setAge(30);
        employee.setCreateTime(new Date());
        int count = mapper.insertEmployee(employee);
        System.out.println("影响的行数："+count+"---->"+employee);
        sqlSession.commit();
    }
    @Test
    public void testUpdate() throws Exception {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        Employee employee = new Employee();
        employee.setEmployeeId(26);
        employee.setLastName("张凯22");
        employee.setEmail("zhangkai@qq.com");
        employee.setGender("1");
        employee.setAge(30);
        employee.setUpdateTime(new Date());
        int count = mapper.updateEmployee(employee);
        System.out.println("影响的行数："+count);
        sqlSession.commit();
    }

    private SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    private <T> T getMapper(Class<T> clazz) throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(clazz);
    }


    @Test
    public void testBind() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);

        DepartmentMapper mapper1 = sqlSession.getMapper(DepartmentMapper.class);
        Department departmentById = mapper1.getDepartmentById(11);

        List<Employee> employeeList = mapper.selectBind("A");
        System.out.println(employeeList);
    }
}
