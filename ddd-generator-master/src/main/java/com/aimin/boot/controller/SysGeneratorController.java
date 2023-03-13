package com.aimin.boot.controller;

import com.aimin.boot.service.SysGeneratorService;
import com.aimin.boot.utils.GenUtilsCommon;
import com.aimin.boot.utils.PageUtils;
import com.aimin.boot.utils.Query;
import com.aimin.boot.utils.Result;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Zhang Liqiang
 * @email 18945085165@163.com
 * @date 2021/11/30
 * @description: 代码生成器
 **/
@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {
    @Autowired
    private SysGeneratorService sysGeneratorService;

    public static String packageName = null;
    public static String moduleName = null;
    public static String tablePrefix = null;
    public static String adminUrl = null;
    public static String frontUrl = null;
    public static String interfacesPath = null;
    public static String applicationPath = null;
    public static String domainPath = null;
    public static String infrastructurePath = null;

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils pageUtil = sysGeneratorService.queryList(new Query(params));

        return Result.ok().put("page", pageUtil);
    }

    /**
     * 生成代码
     */
    @RequestMapping("/code")
    public void code(String tables,
                     String packageName,
                     String moduleName,
                     String tablePrefix,
                     boolean isAuto,
                     String interfacesPath,
                     String applicationPath,
                     String domainPath,
                     String infrastructurePath,
                     String adminUrl,
                     String frontUrl,
                     boolean frontCheck,
                     boolean sqlAuto,
                     HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix,  interfacesPath, applicationPath, domainPath, infrastructurePath, adminUrl, frontUrl);

        byte[] data = sysGeneratorService.generatorCode(tables.split(","), isAuto, frontCheck, sqlAuto);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 生成代码
     */
    @RequestMapping("/code2")
    public void code2(String tables,
                      String key,
                      String first,
                      String packageName,
                      String moduleName,
                      String tablePrefix,
                      boolean isAuto,
                      String interfacesPath,
                      String applicationPath,
                      String domainPath,
                      String infrastructurePath,
                      String adminUrl,
                      String frontUrl,
                      boolean frontCheck,
                      boolean sqlAuto,
                      HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix,  interfacesPath, applicationPath, domainPath, infrastructurePath, adminUrl, frontUrl);
        System.out.println("tables"+tables);
        byte[] data = sysGeneratorService.generatorCode2(tables.split(","), key, first, isAuto, frontCheck, sqlAuto);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }


    /**
     * 获取配置
     */
    @ResponseBody
    @RequestMapping("/config")
    public Result config() {
        //配置信息
        Configuration config = GenUtilsCommon.getConfig();
        return Result.ok().put("package", config.getString("package"))
                .put("moduleName", config.getString("moduleName"))
                .put("tablePrefix", config.getString("tablePrefix"));
    }


    /**
     * 生成代码
     */
    @ResponseBody
    @RequestMapping("/1code")
    public Result acode(String tables,
                        String packageName,
                        String moduleName,
                        String tablePrefix,
                        boolean isAuto,
                        String interfacesPath,
                        String applicationPath,
                        String domainPath,
                        String infrastructurePath,
                        String adminUrl,
                        String frontUrl,
                        boolean frontCheck,
                        boolean sqlAuto,
                        HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix,  interfacesPath, applicationPath, domainPath, infrastructurePath, adminUrl, frontUrl);
        sysGeneratorService.generatorCode(tables.split(","), isAuto, frontCheck, sqlAuto);
        return Result.ok();
    }

    /**
     * 生成代码
     */
    @ResponseBody
    @RequestMapping("/1code2")
    public Result acode2(String tables,
                         String key,
                         String first,
                         String packageName,
                         String moduleName,
                         String tablePrefix,
                         boolean isAuto,
                         String interfacesPath,
                         String applicationPath,
                         String domainPath,
                         String infrastructurePath,
                         String adminUrl,
                         String frontUrl,
                         boolean frontCheck,
                         boolean sqlAuto,
                         HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix, interfacesPath, applicationPath, domainPath, infrastructurePath, adminUrl, frontUrl);
        sysGeneratorService.generatorCode2(tables.split(","), key, first, isAuto, frontCheck, sqlAuto);
        return Result.ok();
    }


    private void handStaticData(String packageName,
                                String moduleName,
                                String tablePrefix,
                                String interfacesPath,
                                String applicationPath,
                                String domainPath,
                                String infrastructurePath,
                                String adminUrl,
                                String frontUrl) {
        if (!StringUtils.isNullOrEmpty(packageName)) {
            SysGeneratorController.packageName = packageName;
        }
        if (!StringUtils.isNullOrEmpty(moduleName)) {
            SysGeneratorController.moduleName = moduleName;
        }
        if (!StringUtils.isNullOrEmpty(tablePrefix)) {
            SysGeneratorController.tablePrefix = tablePrefix;
        }
        if (!StringUtils.isNullOrEmpty(adminUrl)) {
            SysGeneratorController.adminUrl = adminUrl;
        }
        if (!StringUtils.isNullOrEmpty(interfacesPath)) {
            SysGeneratorController.interfacesPath = interfacesPath;
        }
        if (!StringUtils.isNullOrEmpty(applicationPath)) {
            SysGeneratorController.applicationPath = applicationPath;
        }
        if (!StringUtils.isNullOrEmpty(domainPath)) {
            SysGeneratorController.domainPath = domainPath;
        }
        if (!StringUtils.isNullOrEmpty(infrastructurePath)) {
            SysGeneratorController.infrastructurePath = infrastructurePath;
        }
        if (!StringUtils.isNullOrEmpty(frontUrl)) {
            SysGeneratorController.frontUrl = frontUrl;
        }
    }

}
