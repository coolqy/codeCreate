package com.aimin.boot.config;

import com.aimin.boot.dao.*;
import com.aimin.boot.utils.AiminException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Zhang Liqiang
 * @email 18945085165@163.com
 * @date 2021/11/30
 * @description: 数据库配置
 **/
@Configuration
public class DbConfig {
    @Value("${aimin.database: mysql}")
    private String database;
    @Autowired
    private MySQLGeneratorDao mySQLGeneratorDao;
    @Autowired
    private OracleGeneratorDao oracleGeneratorDao;
    @Autowired
    private SQLServerGeneratorDao sqlServerGeneratorDao;
    @Autowired
    private PostgreSQLGeneratorDao postgreSQLGeneratorDao;

    private static boolean mongo = false;

    @Bean
    @Primary
    @Conditional(MongoNullCondition.class)
    public GeneratorDao getGeneratorDao() {
        if ("mysql".equalsIgnoreCase(database)) {
            return mySQLGeneratorDao;
        } else if ("oracle".equalsIgnoreCase(database)) {
            return oracleGeneratorDao;
        } else if ("sqlserver".equalsIgnoreCase(database)) {
            return sqlServerGeneratorDao;
        } else if ("postgresql".equalsIgnoreCase(database)) {
            return postgreSQLGeneratorDao;
        } else {
            throw new AiminException("不支持当前数据库：" + database);
        }
    }

    @Bean
    @Primary
    @Conditional(MongoCondition.class)
    public GeneratorDao getMongoDBDao(MongoDBGeneratorDao mongoDBGeneratorDao) {
        mongo = true;
        return mongoDBGeneratorDao;
    }

    public static boolean isMongo() {
        return mongo;
    }

}
