function Ajax(url, type, data, success, async, parent) {
    var _init = function () {
        if (type == undefined) {
            type = "GET";
        }
        if (data == undefined) {
            data = [];
        }
        if (parent == undefined) {
            parent = "gl_root";
        }
        if (success == undefined) {
            success = function (result) {
                if (result.ok) {
                    switch (result.type) {
                        case "form":
                            new FormWindow(result, parent);
                            break;
                        case "grid":
                            new GridWindow(result, parent);
                            break;
                        case "redirect":
                            window.location.href = result.data[0];
                            break;
                        case "message":
                            new MessageDialog("操作成功", "success");
                            break;
                        default:
                            new MessageDialog("尚未支持");
                    }
                }
                else if (result.data) {
                    new MessageDialog(result.data);
                }
                else {
                    $("div#" + parent).html(result);
                }

            }
        }
        if (async == undefined) {
            async = true;
        }
        $.ajax({
            url: url,
            type: type,
            data: data,
            success: success,
            async: async,
            //dataType: "json",
            cache: false,
            error: function () {
                new MessageDialog("HTTP请求失败!");
            }
        });
    };
    _init();
}


function GridWindow(grid, parent) {
    var _grid_id;
    var _id = function (id) {
        return "grid-" + _grid_id + "-" + id;
    };
    var _c_id = function (id) {
        return id.split('-')[3];
    };
    var _init = function () {
        _grid_id = grid.id;
        var content = "<h4>" + grid.name;
        if (grid.add) {
            content += "[<button title='新增' id='" + _id("add") + "'>新增</button>]";
        }
        content += "</h4><hr/><table width='98%' align='center'><thead><tr class='grid-tr'>";

        for (var i in grid.cols) {
            var col = grid.cols[i];
            content += "<td";
            if (col.width != undefined) {
                content += " width='" + col.width + "'";
            }
            content += "><b>" + col.label + "</b></td>"
        }
        if (grid.action) {
            content += "<td>操作</td>";
        }
        content += "</tr></thead><tbody>";
        for (var i = 0; i < grid.items.length;) {
            if (grid.action) {
                if (i % (grid.cols.length + 1) == 0) {
                    content += "<tr class='grid-tr'>";
                }
            }
            else {

                if (i % grid.cols.length == 0) {
                    content += "<tr class='grid-tr'>";
                }
            }
            content += "<td>" + grid.items[i] + "</td>";
            i++;
            if (grid.action) {
                if ((i + 1) % (grid.cols.length + 1) == 0) {
                    content += "<td class='grid-td-opt'>";
                    if (grid.view) {
                        content += "<button title='查看' id='" + _id("view") + "-" + grid.items[i] + "'>查看</button>";
                    }
                    if (grid.edit) {
                        content += "<button title='编辑' id='" + _id("edit") + "-" + grid.items[i] + "'>编辑</button>";
                    }
                    if (grid.delete) {
                        content += "<button title='删除' id='" + _id("delete") + "-" + grid.items[i] + "'>删除</button>";
                    }
                    content += "</td>";
                    content += "</tr>";
                    i++;
                }
            }
            else {
                if (i % (grid.cols.length) == 0) {
                    content += "</tr>";
                }
            }
        }
        content += "</tbody></table>";

        new HtmlDiv("grid-" + grid.id, content, parent);

        if (grid.action != undefined) {
            if (grid.add) {
                var addBtn = $("button#" + _id("add"));
                addBtn.addClass("btn btn-primary btn-mini");
                addBtn.click(function () {
                    new Ajax(grid.action + "/add");
                });
            }
            if (grid.view) {

                $("button[id^='" + _id("view") + "']").each(function () {
                    $(this).addClass("btn btn-info btn-mini");
                    $(this).click(function () {
                        new Ajax(grid.action + "/" + _c_id($(this).attr("id")), "PUT");
                    });
                });
            }
            if (grid.edit) {
                $("button[id^='" + _id("edit") + "']").each(function () {
                    $(this).addClass("btn btn-warning btn-mini");
                    $(this).click(function () {
                        new Ajax(grid.action + "/" + _c_id($(this).attr("id")));
                    });
                });
            }
            if (grid.delete) {
                $("button[id^='" + _id("delete") + "']").each(function () {
                    $(this).addClass("btn btn-danger btn-mini");
                    $(this).click(function () {
                        new Ajax(grid.action + "/" + _c_id($(this).attr("id")), "DELETE");
                    });
                });
            }
        }
    };
    _init();
}

function FormWindow(form, parent) {
    var _form_id;
    var _field = function (id, label, input) {
        if (label == undefined) {
            return input;
        }
        var content = "<div class='form-group'><label ";
        if (id != undefined) {
            content += " for='fm-" + _form_id + "-" + id + "' ";
        }
        content += " class='col-lg-2 control-label'>" + label + "：</label>";
        content += "<div class='col-lg-10'>";
        content += input;
        content += "</div></div>";
        return content;
    };

    var _id = function (id) {
        return "fm-" + _form_id + "-" + id;
    };
    var _hidden_field = function (id, value) {
        var s = "<input type='hidden' id='" + _id(id) + "'";
        if (value != undefined) {
            s += "value='" + value + "'";
        }
        s += "/>";
        return   s;
    };

    var _button = function (id, label, type) {
        var btn = "<button type='button' id='" + _id(id) + "' class='btn ";
        if (type != undefined) {
            btn += "btn-" + type;
        }
        btn += "'>" + label + "</button> ";
        return btn;
    };
    var _button_group = function (buttons) {
        var content = "<div class='form-group'><div class='col-lg-2'></div>";
        content += "<div class='col-lg-10 btn-group'>";
        content += _button("submit", "提交", "danger");
        content += _button("reset", "重写", "info");
        for (var i in buttons) {
            var btn = form.buttons[i];
            content += _button(btn.id, btn.label, btn.type);
        }
        content += "</div></div>";
        return content;
    };
    var _init = function () {
        _form_id = form.id;

        var content = "<form class='form-horizontal' method='" + form.method + "'  action='" + form.action + "'>";
        content += "<fieldset><legend>" + form.title + "</legend>";
        content += _hidden_field("created", form.created);
        for (var i in form.fields) {
            var field = form.fields[i];
            var input;
            switch (field.type) {
                case "text":
                    input = "<input class='form-control' style='width: " + field.width + "px' type='text' id='" + _id(field.id) + "' ";
                    if (field.value != undefined) {
                        input += "value='" + field.value + "' ";
                    }
                    if (field.readonly) {
                        input += "readonly ";
                    }
                    input += " />";
                    if (field.required) {
                        field.label += "(*)";
                    }
                    break;
                case "textarea":

                    input = "<textarea ";
                    if (!field.html) {
                        input += "class='form-control'";
                    }
                    input += " id='" + _id(field.id) + "' style='width: " + field.width + "px;height: " + field.height + "px;' ";
                    if (field.readonly) {
                        input += "readonly ";
                    }
                    input += ">";
                    if (field.value != undefined) {
                        input += field.value;
                    }
                    input += "</textarea>";
                    if (field.required) {
                        field.label += "(*)";
                    }
                    break;
                case "select":
                    input = "<select style='width: "
                        + field.width
                        + "px;' class='form-control' id='"
                        + _id(field.id) + "' ";
                    if (field.readonly) {
                        input += "disabled "
                    }
                    input += ">";
                    for (var j in field.options) {
                        var item = field.options[j];

                        input += "<option value='" + item.value + "' ";
                        if (item.value == field.value) {
                            input += "selected='selected'"
                        }
                        input += ">" + item.label + " &nbsp; </option>";

                    }
                    input += "</select>";
                    break;
                case "password":
                    input = "<input style='width: " +
                        field.width +
                        "px' class='form-control' type='password' id='"
                        + _id(field.id) + "' ";
                    if (field.value != undefined) {
                        input += "value='" + field.value + "' ";
                    }
                    input += " />";
                    field.label += "(*)";
                    break;
                case "radio":
                    //input = _hidden_field(field.id, field.value);
                    input = "";
                    var k = 1;
                    for (var j in field.options) {
                        var item = field.options[j];
                        input += "<label class='radio-inline'><input class='form-control' type='radio' name='" + _id(field.id) + "'  value='" + item.value + "' ";
                        if (item.value == field.value) {
                            input += "checked='true' ";
                        }
                        input += "/>" + item.label + "</label>";

                        if (k % field.cols == 0) {
                            input += "<br/>"
                        }
                        k++;
                    }
                    break;

                case  "checkbox":
                    //input = _hidden_field(field.id, field.value);
                    input = "";
                    var k = 1;
                    for (var j in field.options) {
                        var item = field.options[j];
                        input += "<label class='checkbox-inline'><input  type='checkbox' name='" + _id(field.id) + "' value='" + item.value + "' ";
                        if (item.selected) {
                            input += "checked='true' ";
                        }
                        input += "/>" + item.label + " </label>";

                        if (k % field.cols == 0) {
                            input += "<br/>"
                        }
                        k++;
                    }
                    break;
                case "agree":
                    //input = "<textarea disabled style='width: 400px;height: 100px' >" + field.text + "</textarea>";
                    //input += "<br/>"
                    input = field.text;
                    input += "<div class='checkbox'><label><input id='"
                        + _id(field.id)
                        + "' type='checkbox'>我同意</label></div>";
                    field.label += "(*)";
                    break;

                default:
                    input = _hidden_field(field.id, field.value);
                    break;
            }
            content += _field(field.id, field.label, input);
        }

        if (form.captcha) {
            var input = "<div class='form-group'>";
            input += "<label class='col-lg-2 control-label' for='" + _id('captcha') + "'>验证码(*)：</label>";
            switch (gl_captcha) {
                case "kaptcha":
                    input += "<div class='col-lg-1'><input class='form-control' type='text'  style='width: 80px;' id='"
                        + _id("captcha") + "'/></div>";
                    input += "<div class='col-lg-9'><img id='"
                        + _id('captcha_img')
                        + "' src='/captcha?_="
                        + Math.random()
                        + "' alt='点击更换验证码'/></div>";
                    break;
                case "reCaptcha":
                    input += "<div class='col-lg-10' id='" + _id('captcha') + "'></div>";
                    break;
            }
            input += "</div>";
            content += input;

        }

        var reload_captcha = function () {
            switch (gl_captcha) {
                case "kaptcha":
                    $('img#' + _id('captcha_img')).attr("src", "/captcha?_=" + Math.random());
                    break;
            }
        };

        content += _button_group(form.buttons);
        content += "</fieldset></form>";
        //alert(content);
        new HtmlDiv("fm-" + form.id, content, parent);


        if (form.captcha) {
            switch (gl_captcha) {
                case "kaptcha":
                    $('img#' + _id('captcha_img')).click(reload_captcha);
                    break;
                case "reCaptcha":
                    showRecaptcha(_id("captcha"));
                    break;
            }
        }
        for (var i in form.fields) {
            var field = form.fields[i];
            if (field.type == "textarea" && field.html) {
                var editor = new UE.ui.Editor();
                editor.render(_id(field.id));
                //UE.getEditor('myEditor')
            }
        }

        $('button#' + _id("reset")).click(function () {
            for (var i in form.fields) {
                var field = form.fields[i];
                switch (field.type) {
                    case "text":
                    case "password":
                        $("input#" + _id(field.id)).val(field.value == undefined ? "" : field.value);
                        break;
                    case "textarea":
                        $("textarea#" + _id(field.id)).val(field.value == undefined ? "" : field.value);
                        break;
                    case "radio":
                        $("input:radio[name='" + _id(field.id) + "'][value='" + field.value + "']").prop('checked', true);
                        break;
                    case "select":
                        $("select#" + _id(field.id)).val(field.value == undefined ? "" : field.value);
                        break;
                    case "agree":
                        $("input#" + _id(field.id)).prop('checked', false);
                        break;
                    default:
                        break;
                }
            }
            if (form.captcha) {
                switch (gl_captcha) {
                    case "kaptcha":
                        $("input#" + _id("captcha")).val('');
                        reload_captcha();
                        break;
                    case "reCaptcha":
                        $("input#recaptcha_response_field").val('');
                        break;
                }
            }
        });

        $('button#' + _id("submit")).click(function () {
            var data = {};
            for (var i in form.fields) {
                var field = form.fields[i];
                switch (field.type) {
                    case "hidden":
                    case "text":
                    case "password":
                        data[field.id] = $('input#' + _id(field.id)).val();
                        break;
                    case "textarea":
                        data[field.id] = $('textarea#' + _id(field.id)).val();
                        break;
                    case "radio":
                        data[field.id] = $("input[name='" + _id(field.id) + "']:checked").val();
                        break;
                    case "select":
                        data[field.id] = $('select#' + _id(field.id)).val();
                        break;
                    case "agree":
                        if ($("input#" + _id(field.id)).is(':checked')) {
                            data[field.id] = true;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (form.captcha) {
                switch (gl_captcha) {
                    case "kaptcha":
                        data['captcha'] = $('input#' + _id('captcha')).val();
                        break;
                    case "reCaptcha":
                        data['challenge'] = $('input#recaptcha_challenge_field').val();
                        data['captcha'] = $('input#recaptcha_response_field').val();
                        break;
                }
            }
            new Ajax(form.action, "POST", data);
            reload_captcha();
        });

    };

    _init();
}

function HtmlDiv(id, content, parent) {
    var _init = function () {
        var root = "div#" + id;
        if ($(root).length == 0) {
            $("div#" + parent).append("<div id='" + id + "'></div>");
        }
        $(root).html(content);
    };
    _init();
}
function MessageDialog(messages, type) {
    var _init = function () {
        var name;
        switch (type) {
            case "error":
                name = "错误";
                break;
            case "success":
                name = "成功";
                break;
            case "info":
                name = "提示";
                break;
            default:
                type = "block";
                name = "警告";
                break;
        }
        if (type == undefined) {
            type = "error";
        }
        $("div#gl_message").html("<div class='alert alert-block'><button type='button' class='close' data-dismiss='alert'>&times;</button><h4>" +
            name + "[" + (new Date()).pattern("yyyy-MM-dd hh:mm:ss.S") + "]：</h4>" + (messages instanceof Array ? messages.join("<br/>") : messages) + "</div>");

    };
    _init();
}


function showRecaptcha(element) {
    Recaptcha.create(
        gl_reCaptcha_key, element, {
            theme: "red",
            callback: Recaptcha.focus_response_field}
    );
}

Date.prototype.pattern=function(fmt) {
    var o = {
        "M+" : this.getMonth()+1, //月份
        "d+" : this.getDate(), //日
        "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时
        "H+" : this.getHours(), //小时
        "m+" : this.getMinutes(), //分
        "s+" : this.getSeconds(), //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S" : this.getMilliseconds() //毫秒
    };
    var week = {
        "0" : "/u65e5",
        "1" : "/u4e00",
        "2" : "/u4e8c",
        "3" : "/u4e09",
        "4" : "/u56db",
        "5" : "/u4e94",
        "6" : "/u516d"
    };
    if(/(y+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    if(/(E+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);
    }
    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}
