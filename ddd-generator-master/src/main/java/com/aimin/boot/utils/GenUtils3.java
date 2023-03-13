package com.aimin.boot.utils;

import com.aimin.boot.entity.ColumnEntity;
import com.aimin.boot.entity.TableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
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
@Slf4j
public class GenUtils3 {

    private static final String RESOURCES_PATH = "/src/main/resources";

    public static void generatorCode(Configuration config, String groupId, String artifact, String version, String packageName, String springBootVersion, Map<String, String> table, List<Map<String, String>> columns, ZipOutputStream zip) throws IOException {


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

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("artifact", artifact);
        map.put("groupId", groupId);
        map.put("version", version);
        map.put("package", packageName);
        map.put("springBootVersion", springBootVersion);
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        map.put("isFirst", false);
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasList", hasList);
        map.put("mainPath", groupId);
        map.put("package", packageName);
        map.put("commonPackage", packageName);
        map.put("moduleName", "boot");
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("dataBaseName", config.getString("dataBaseName"));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = GenUtilsCommon.getTemplates(true, true);

        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                // 生成固定代码
                String fixFileName = getFileName(template, artifact, packageName, artifact, tableEntity.getClassName());
                if (StringUtils.isNotBlank(fixFileName)) {
                    zip.putNextEntry(new ZipEntry(fixFileName));
                    IOUtils.write(sw.toString(), zip, "UTF-8");
                }
            } catch (IOException e) {
                log.error("渲染模板失败", e);
            } finally {
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            }
        }

    }

    public static void generatorFixCode(Configuration config, String groupId, String artifact, String version, String packageName, String springBootVersion, ZipOutputStream zip) throws IOException {

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("artifact", artifact);
        map.put("groupId", groupId);
        map.put("version", version);
        map.put("package", packageName);
        map.put("springBootVersion", springBootVersion);
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        map.put("mainPath", groupId);
        map.put("package", packageName);
        map.put("commonPath", packageName.replace(".", "/"));
        map.put("moduleName", "boot");
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("dataBaseName", config.getString("dataBaseName"));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = GenUtilsCommon.getTemplates(true, true);

        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                // 生成固定代码
                String fixFileName = getFixFileName(template, artifact, packageName, artifact);
                if (StringUtils.isNotBlank(fixFileName)) {
                    zip.putNextEntry(new ZipEntry(fixFileName));
                    IOUtils.write(sw.toString(), zip, "UTF-8");
                }
            } catch (IOException e) {
                log.error("渲染模板失败", e);
            } finally {
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            }
        }

    }

    private static String getFileName(String template, String projectNamePath, String packageName, String moduleName, String className) {

        String src = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + packageName.replace(".", File.separator) + File.separator + "boot";

        if (template.contains("Command.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "web" + File.separator
                    + "command" + File.separator
                    + className + "Command.java";
        }
        if (template.contains("template/Controller.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "web" + File.separator
                    + className + "Controller.java";
        }
        if (template.contains("template/Converter.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "converter" + File.separator
                    + className + "Converter.java";
        }
        if (template.contains("template/DO.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "entity" + File.separator
                    + className + "DO.java";
        }
        if (template.contains("DTO.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "facade" + File.separator
                    + "dto" + File.separator
                    + className + "DTO.java";
        }
        if (template.contains("DTOAssembler.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "facade" + File.separator
                    + "assembler" + File.separator
                    + className + "DTOAssembler.java";
        }
        if (template.contains("template/Entity.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "model" + File.separator
                    + "entity" + File.separator
                    + className + ".java";
        }
        if (template.contains("EntityId.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "model" + File.separator
                    + "types" + File.separator
                    + className + "Id.java";
        }
        if (template.contains("Factory.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "factory" + File.separator
                    + className + "Factory.java";
        }
        if (template.contains("Mapper.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "mapper" + File.separator
                    + className + "Mapper.java";
        }
        if (template.contains("Mapper.xml.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "mapper" + File.separator
                    + "xml" + File.separator
                    + className + "Mapper.xml";
        }
        if (template.contains("Repository.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "repository" + File.separator
                    + className + "Repository.java";
        }
        if (template.contains("RepositoryImpl.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "repository" + File.separator
                    + "impl" + File.separator
                    + className + "RepositoryImpl.java";
        }


        if (template.contains("Service.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-application" + src + File.separator + "application" + File.separator
                    + className + "Service.java";
        }
        if (template.contains("ServiceFacade.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "facade" + File.separator
                    + className + "ServiceFacade.java";
        }
        if (template.contains("ServiceFacadeImpl.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "facade" + File.separator
                    + "impl" + File.separator
                    + className + "ServiceFacadeImpl.java";
        }
        if (template.contains("ServiceImpl.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-application" + src + File.separator + "application" + File.separator
                    + "impl" + File.separator
                    + className + "ServiceImpl.java";
        }
        if (template.contains("UpdateSpecification.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "specification" + File.separator
                    + className + "UpdateSpecification.java";
        }

        if (template.contains("menu.sql.vm")) {
            return className.toLowerCase() + "_menu.sql";
        }

        if (template.contains("menu-all.sql.vm")) {
            return className.toLowerCase() + "_menu_all.sql";
        }

        if (template.contains("index.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + ".vue";
        }

        if (template.contains("add-or-update.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-add-or-update.vue";
        }

        if (template.contains("ServiceFacadeAll.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "facade" + File.separator
                    + className + "AllServiceFacade.java";
        }

        if (template.contains("template/indexAll.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + ".vue";
        }

        if (template.contains("add-or-update-all.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-add-or-update-all.vue";
        }

        if (template.contains("index-update.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-update.vue";
        }


        if (template.contains("CommandAll.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "web" + File.separator
                    + "command" + File.separator
                    + className + "AllCommand.java";
        }

        if (template.contains("ServiceFacadeImplAll.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + src + File.separator + "interfaces" + File.separator
                    + "facade" + File.separator
                    + "impl" + File.separator
                    + className + "AllServiceFacadeImpl.java";
        }


        return null;
    }

    private static String getFixFileName(String template, String projectNamePath, String packageName, String moduleName) {

        if (template.contains("parent/pom.xml.vm")) {
            return projectNamePath + File.separator + "pom.xml";
        }
        if (template.contains("starter/pom.xml.vm")) {
            return projectNamePath + File.separator + moduleName + "-starter" + File.separator + "pom.xml";
        }
        if (template.contains("interfaces/pom.xml.vm")) {
            return projectNamePath + File.separator + moduleName + "-interfaces" + File.separator + "pom.xml";
        }
        if (template.contains("infrastructure/pom.xml.vm")) {
            return projectNamePath + File.separator + moduleName + "-infrastructure" + File.separator + "pom.xml";
        }

        if (template.contains("domain/pom.xml.vm")) {
            return projectNamePath + File.separator + moduleName + "-domain" + File.separator + "pom.xml";
        }
        if (template.contains("application/pom.xml.vm")) {
            return projectNamePath + File.separator + moduleName + "-application" + File.separator + "pom.xml";
        }
        if (template.contains("application.yml.vm")) {
            return projectNamePath + File.separator + moduleName + "-starter" + RESOURCES_PATH + File.separator + "application.yml";
        }
        if (template.contains("application-dev.yml.vm")) {
            return projectNamePath + File.separator + moduleName + "-starter" + RESOURCES_PATH + File.separator + "application-dev.yml";
        }

        if (template.contains("logback-spring.xml.vm")) {
            return projectNamePath + File.separator + moduleName + "-starter" + RESOURCES_PATH + File.separator + "logback-spring.xml";
        }

        String src = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (template.contains("ApplicationRunner.java.vm")) {
            return projectNamePath + File.separator + moduleName + "-starter" + src + packageName.replace(".", File.separator) + File.separator + "ApplicationRunner.java";
        }


        src += packageName.replace(".", File.separator) + File.separator + "boot";


        if (template.contains("PageConverter.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "converter" + File.separator
                    + "PageConverter.java";
        }

        if (template.contains("group/AddGroup.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "validator" + File.separator
                    + "group" + File.separator
                    + "AddGroup.java";
        }

        if (template.contains("group/Group.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "validator" + File.separator
                    + "group" + File.separator
                    + "Group.java";
        }

        if (template.contains("group/UpdateGroup.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "validator" + File.separator
                    + "group" + File.separator
                    + "UpdateGroup.java";
        }
        if (template.contains("Assert.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "validator" + File.separator
                    + "Assert.java";
        }
        if (template.contains("ValidatorUtils.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "validator" + File.separator
                    + "ValidatorUtils.java";
        }

        if (template.contains("AppException.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "exception" + File.separator
                    + "AppException.java";
        }
        if (template.contains("AppExceptionHandler.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "exception" + File.separator
                    + "AppExceptionHandler.java";
        }

        if (template.contains("Query.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "mybatis" + File.separator
                    + "Query.java";
        }

        if (template.contains("SQLFilter.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + src + File.separator + "infrastructure" + File.separator
                    + "util" + File.separator
                    + "xss" + File.separator
                    + "SQLFilter.java";
        }

        if (template.contains("user-select.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + "user" + File.separator + "user-select.vue";
        }

        if (template.contains("AbsEntity.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "Entity.java";
        }
        if (template.contains("AbstractSpecification.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "AbstractSpecification.java";
        }
        if (template.contains("template/Specification.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "Specification.java";
        }

        if (template.contains("StatusEnum.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "StatusEnum.java";
        }

        if (template.contains("AndSpecification.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "AndSpecification.java";
        }

        if (template.contains("NotSpecification.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "NotSpecification.java";
        }

        if (template.contains("OrSpecification.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "OrSpecification.java";
        }

        if (template.contains("ValueObject.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "ValueObject.java";
        }

        if (template.contains("Page.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-domain" + src + File.separator + "domain" + File.separator
                    + "shared" + File.separator
                    + "Page.java";
        }

        if (template.contains("BaseDO.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator
                    + packageName.replace(".", File.separator)
                    + File.separator + "common" + File.separator + "BaseDO.java";
        }

        if (template.contains("Result.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator
                    + packageName.replace(".", File.separator)
                    + File.separator + "common" + File.separator + "Result.java";
        }

        if (template.contains("CommonConstant.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-infrastructure" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator
                    + packageName.replace(".", File.separator)
                    + File.separator + "common" + File.separator + "CommonConstant.java";
        }

        if (template.contains("AbstractController.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator
                    + packageName.replace(".", File.separator)
                    + File.separator + "common" + File.separator + "AbstractController.java";
        }


        if (template.contains("SwaggerConfig.java.vm")) {
            return projectNamePath + File.separator + moduleName
                    + "-interfaces" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator
                    + packageName.replace(".", File.separator)
                    + File.separator + "common" + File.separator + "config" + File.separator + "SwaggerConfig.java";
        }

        return null;
    }
}

