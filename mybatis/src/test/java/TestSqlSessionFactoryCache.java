import com.candy.bean.Employee;
import com.candy.mapper.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Candy
 * @create 2021-05-26 11:29
 */
public class TestSqlSessionFactoryCache {

    @Test
    public void testCache() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);


        Employee employee = mapper.getEmployeeAndDepartmentById(20);
        System.out.println(employee);
        sqlSession.clearCache();

        Employee employee1 = mapper.getEmployeeAndDepartmentById(20);
        System.out.println(employee1==employee);
        sqlSession.close();
    }

    @Test
    public void testCache2() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);

        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        EmployeeMapper mapper2 = sqlSession2.getMapper(EmployeeMapper.class);

        Employee employee = mapper.getEmployeeAndDepartmentById(20);
        System.out.println(employee);
        sqlSession.close();//必须会话关闭才行
//        sqlSession.clearCache();

        //第二次查询是从缓存中取出，并没有发送新的SQL
        Employee employee2 = mapper2.getEmployeeAndDepartmentById(20);
        System.out.println(employee2);
        sqlSession2.close();
    }

}
