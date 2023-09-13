//package com.ipharmacare.collect.data.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//
//import javax.sql.DataSource;
//
//
//@Configuration
//@MapperScan(basePackages = {"com.ipharmacare.collect.data.mapper"}, sqlSessionFactoryRef = "clickHouseSqlSessionFactory")
//public class CollectDataSourceConfig {
//
//    @Bean("clickHouseDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
//    public DataSource collectDataSource() {
//        DataSource ds = DataSourceBuilder.create().type(HikariDataSource.class).build();
//        return ds;
//    }
//
//    @Bean(name = "clickHouseSqlSessionFactory")
//    public SqlSessionFactory clickHouseSqlSessionFactory(@Qualifier("clickHouseDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sqlBean = new SqlSessionFactoryBean();
//        sqlBean.setDataSource(dataSource);
//        sqlBean.setMapperLocations(new PathMatchingResourcePatternResolver()
//                .getResources("classpath*:/mapper-clickHouse/*.xml"));
//        return sqlBean.getObject();
//    }
//
//    @Bean(name = "clickHouseSqlSessionTemplate")
//    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("clickHouseSqlSessionFactory") SqlSessionFactory sqlSessionFactory)
//            throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}
//
