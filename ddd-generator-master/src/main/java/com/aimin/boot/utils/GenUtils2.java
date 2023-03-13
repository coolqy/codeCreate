package com.aimin.boot.utils;

import com.aimin.boot.entity.ColumnEntity;
import com.aimin.boot.entity.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Zhang Liqiang
 * @email 18945085165@163.com
 * @date 2021/11/30
 * @description:
 **/
public class GenUtils2 {

    /**
     * 生成代码
     */
    public static Map<String, Object> generatorCode(Map<String, String> table,
                                                    List<Map<String, String>> columns,
                                                    ZipOutputStream zip,
                                                    String foreignKey,
                                                    String firstTable,
                                                    boolean isAuto,
                                                    boolean frontCheck,
                                                    boolean sqlAuto,
                                                    List<Map<String, Object>> subList) {
        //配置信息
        Configuration config = GenUtilsCommon.getConfig();
        boolean hasBigDecimal = false;
        boolean hasList = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        //表名转换成Java类名
        String className = GenUtilsCommon.tableToJava(tableEntity.getTableName(), config.getStringArray("tablePrefix"));
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            //列名转换成Java属性名
            String attrName = GenUtilsCommon.columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), GenUtilsCommon.columnToJava(columnEntity.getDataType()));
            columnEntity.setAttrType(attrType);


            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                hasBigDecimal = true;
            }
            if (!hasList && "array".equals(columnEntity.getExtra())) {
                hasList = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columsList.add(columnEntity);
        }
        tableEntity.setColumns(columsList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "com.aimin" : mainPath;
        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        boolean isFirst = false;
        if (tableEntity.getTableName().equals(firstTable)) {
            isFirst = true;
        }
        map.put("subList", subList);
        map.put("isFirst", isFirst);
        map.put("frontCheck", frontCheck);
        map.put("firstTable", firstTable);
        map.put("foreignKey", foreignKey);
        map.put("foreignColumnB", GenUtilsCommon.columnToJava(foreignKey));
        map.put("foreignColumn", StringUtils.uncapitalize(GenUtilsCommon.columnToJava(foreignKey)));
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasList", hasList);
        map.put("mainPath", mainPath);
        map.put("package", config.getString("package"));
        map.put("commonPackage", config.getString("commonPackage"));
        map.put("moduleName", config.getString("moduleName"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = GenUtilsCommon.getTemplates(true, isFirst);
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            if (isAuto) {
                try {
                    GenUtilsCommon.genetatorAuto(GenUtilsCommon.getFileName(template, tableEntity.getClassName(), config.getString("package"), config.getString("moduleName"), StringUtils.uncapitalize(GenUtilsCommon.tableToJava(firstTable, config.getStringArray("tablePrefix")))), sw, sqlAuto);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AiminException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
                }
            } else {
                try {
                    //添加到zip
                    zip.putNextEntry(new ZipEntry(GenUtilsCommon.getFileName(template, tableEntity.getClassName(), config.getString("package"), config.getString("moduleName"), StringUtils.uncapitalize(GenUtilsCommon.tableToJava(firstTable, config.getStringArray("tablePrefix"))))));
                    IOUtils.write(sw.toString(), zip, "UTF-8");
                    IOUtils.closeQuietly(sw);
                    zip.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AiminException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
                }
            }
        }

        return map;
    }
}

