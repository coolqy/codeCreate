package com.aimin.boot.service;

import com.aimin.boot.dao.GeneratorDao;
import com.aimin.boot.dao.MongoDBGeneratorDao;
import com.aimin.boot.factory.MongoDBCollectionFactory;
import com.aimin.boot.utils.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @author Zhang Liqiang
 * @email 18945085165@163.com
 * @date 2021/11/30
 * @description: 代码生成器
 **/
@Service
public class SysGeneratorService {

    @Autowired
    private GeneratorDao generatorDao;


    public PageUtils queryList(Query query) {
        Page<?> page = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String, Object>> list = generatorDao.queryList(query);
        int total = (int) page.getTotal();
        if (generatorDao instanceof MongoDBGeneratorDao) {
            total = MongoDBCollectionFactory.getCollectionTotal(query);
        }
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    public Map<String, String> queryTable(String tableName) {
        return generatorDao.queryTable(tableName);
    }

    public List<Map<String, String>> queryColumns(String tableName) {
        return generatorDao.queryColumns(tableName);
    }


    public byte[] generatorCode(String[] tableNames,
                                boolean isAuto,
                                boolean frontCheck,
                                boolean sqlAuto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GenUtils.generatorCode(table, isAuto, frontCheck, sqlAuto, columns, zip);
        }

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }


    public byte[] generatorCode2(String[] tableNames,
                                 String key,
                                 String first,
                                 boolean isAuto,
                                 boolean frontCheck,
                                 boolean sqlAuto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        List subList = new ArrayList<Map<String, Object>>();
        for (String tableName : tableNames) {
            if (!tableName.equals(first)) {
                //查询表信息
                Map<String, String> table = queryTable(tableName);
                //查询列信息
                List<Map<String, String>> columns = queryColumns(tableName);
                //生成代码
                Map<String, Object> map = GenUtils2.generatorCode(table, columns, zip, key, first, isAuto, frontCheck, sqlAuto, null);
                subList.add(map);
            }
        }

        //查询表信息
        Map<String, String> table = queryTable(first);
        //查询列信息
        List<Map<String, String>> columns = queryColumns(first);
        //生成代码
        GenUtils2.generatorCode(table, columns, zip, key, first, isAuto, frontCheck, sqlAuto, subList);

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    public byte[] generatorCode3(String groupId, String artifact, String version, String packageName, String springBootVersion) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        //配置信息
        Configuration config = GenUtilsCommon.getConfig();
        // 生成工程框架
        GenUtils3.generatorFixCode(config, groupId, artifact, version, packageName, springBootVersion, zip);
        String[] tableNames = config.getStringArray("tables");
        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GenUtils3.generatorCode(config, groupId, artifact, version, packageName, springBootVersion, table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}

