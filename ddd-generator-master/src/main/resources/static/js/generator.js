$(function () {
    $("#urlInfoId").hide();
    $('input:radio[name="optionsRadios"]').change(function () {
        if ($("#optionsRadios1").is(":checked")) {
            $("#urlInfoId").hide();
        }
        if ($("#optionsRadios2").is(":checked")) {
            $("#urlInfoId").show();
        }
    });

    $.post("sys/generator/config", {},
        function (data) {
            $("#packageName").val(data.package);
            $("#moduleName").val(data.moduleName);
            $("#tablePrefix").val(data.tablePrefix);
        }, "json");

    $("#jqGrid").jqGrid({
        url: 'sys/generator/list',
        datatype: "json",
        colModel: [
            {label: '表名', name: 'tableName', width: 100, key: true},
            {label: 'Engine', name: 'engine', width: 70},
            {label: '表备注', name: 'tableComment', width: 100},
            {label: '创建时间', name: 'createTime', width: 100}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50, 100, 200],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        generatorType: 1,
        q: {
            tableName: null
        },
        form: {
            groupId: null,
            artifact: null,
            version: '',
            packageName: '',
            springBootVersion: ''

        }
    },
    methods: {
        query: function () {
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'tableName': vm.q.tableName},
                page: 1
            }).trigger("reloadGrid");
        },
        generator: function () {
            var tableNames = getSelectedRows();
            if (tableNames == null) {
                return;
            }
            vm.generatorType = 1;
            $("#myModalLabel").html("单例生成");
            $("#baseInfo").hide();
            $('#myModal').modal('show');
            //location.href = "sys/generator/code?tables=" + tableNames.join();
        },
        generator2: function () {
            var tableNames = getSelectedRows();
            console.log(tableNames);
            if (tableNames != null && tableNames.length < 2) {
                alert("请选择两条以上的记录");
                return;
            }
            if (tableNames != null && tableNames.length >= 2) {
                var str = "";
                for (var i = 0; i < tableNames.length; i++) {
                    str = str + "<option>" + tableNames[i] + "</option>"
                }
                $("#tableListId").html(str);
                vm.generatorType = 2;
                $("#myModalLabel").html("主子生成");
                $("#baseInfo").show();
                $('#myModal').modal('show');
            }
        },
        generator3: function () {
            var tableNames = getSelectedRows();
            var key1 = $("#tableListId").val();
            if (vm.generatorType == 2 && (key1 == null || key1 == "" || key1 == '')) {
                alert("请选择主表");
                return;
            }
            var key2 = $("#firstname").val();
            if (vm.generatorType == 2 && (key2 == null || key2 == "" || key2 == '')) {
                alert("请输入主表关联字段");
                return;
            }
            var key3 = $("#packageName").val();
            if (key3 == null || key3 == "" || key3 == '') {
                alert("请输入包名[package]");
                return;
            }
            var key4 = $("#moduleName").val();
            if (key4 == null || key4 == "" || key4 == '') {
                alert("请输入模块名[moduleName]");
                return;
            }
            var key5 = $("#tablePrefix").val();
            if (key5 == null || key5 == "" || key5 == '') {
                alert("请输入表名的前缀[tablePrefix]");
                return;
            }

            var key6 = 0;
            var key7 = $("#adminUrl").val();
            var key8 = $("#frontUrl").val();
            var interfacesPath = $("#interfacesPath").val();
            var applicationPath = $("#applicationPath").val();
            var domainPath = $("#domainPath").val();
            var infrastructurePath = $("#infrastructurePath").val();
            if ($("#optionsRadios2").is(":checked")) {
                key6 = 1;
                if (interfacesPath == null || interfacesPath == "" || interfacesPath == '') {
                    alert("请输入interfaces模块后端路径[interfacesPath]");
                    return;
                }
                if (applicationPath == null || applicationPath == "" || applicationPath == '') {
                    alert("请输入application模块后端路径[applicationPath]");
                    return;
                }

                if (domainPath == null || domainPath == "" || domainPath == '') {
                    alert("请输入domain模块后端路径[domainPath]");
                    return;
                }
                if (infrastructurePath == null || infrastructurePath == "" || infrastructurePath == '') {
                    alert("请输入infrastructure模块后端路径[infrastructurePath]");
                    return;
                }
                if (key7 == null || key7 == "" || key7 == '') {
                    alert("请输入后端路径[adminUrl]");
                    return;
                }
                if (key8 == null || key8 == "" || key8 == '') {
                    alert("请输入前端路径[frontUrl]");
                    return;
                }
            }
            var key9 = 1;
            if ($("#checkRadios1").is(":checked")) {
                key9 = 1;
            } else {
                key9 = 0;
            }

            var key10 = 1;
            if ($("#sqlRadios1").is(":checked")) {
                key10 = 1;
            } else {
                key10 = 0;
            }

            var urlKey = "";
            if (vm.generatorType == 1) {
                if ($("#optionsRadios2").is(":checked")) {
                    urlKey = "1code";
                } else {
                    urlKey = "code";
                }
            }
            if (vm.generatorType == 2) {
                if ($("#optionsRadios2").is(":checked")) {
                    urlKey = "1code2";
                } else {
                    urlKey = "code2";
                }
            }
            var lastUrl = "sys/generator/" + urlKey + "?key=" + key2
                + "&first=" + key1
                + "&packageName=" + key3
                + "&moduleName=" + key4
                + "&tablePrefix=" + key5
                + "&isAuto=" + key6
                + "&interfacesPath=" + interfacesPath
                + "&applicationPath=" + applicationPath
                + "&domainPath=" + domainPath
                + "&infrastructurePath=" + infrastructurePath
                + "&adminUrl=" + key7
                + "&frontUrl=" + key8
                + "&frontCheck=" + key9
                + "&sqlAuto=" + key10
                + "&tables=" + tableNames.join();
            lastUrl = encodeURI(lastUrl);

            if ($("#optionsRadios2").is(":checked")) {
                $.post(lastUrl, {},
                    function (data) {
                        console.log('111111');
                        if (data.code == 0) {
                            alert("生成成功");
                        } else {
                            alert("生成失败");
                        }
                    }, "json");
            } else {
                location.href = lastUrl;
            }
        },
        create: function () {
            console.log(this.form);
            var lastUrl = 'pro/generator' + "?groupId=" + this.form.groupId
                + "&artifact=" + this.form.artifact
                +"&version="+this.form.version
                +"&packageName="+this.form.packageName
                +"&springBootVersion="+this.form.springBootVersion;
            lastUrl = encodeURI(lastUrl);
            location.href = lastUrl;
        }
    }
});

