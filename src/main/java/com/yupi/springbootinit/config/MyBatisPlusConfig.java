package com.yupi.springbootinit.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * MyBatis Plus 配置
 */
@Configuration
@MapperScan("com.yupi.springbootinit.mapper")
public class MyBatisPlusConfig {

    /**
     * 拦截器配置
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Resource
    private DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 设置MyBatis-Plus的全局配置
        // MybatisConfiguration configuration = new MybatisConfiguration();
        // configuration.setMapUnderscoreToCamelCase(true); // 下划线转驼峰命名
        // factoryBean.setConfiguration(configuration);

        return factoryBean.getObject();
    }
}