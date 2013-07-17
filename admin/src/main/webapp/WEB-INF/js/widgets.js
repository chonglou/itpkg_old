function Ajax(url, type, data, success, async) {
    var _init = function () {
        if (type == undefined) {
            type = "GET";
        }
        if (data == undefined) {
            data = [];
        }
        if (success == undefined) {
            success = function (result) {
                if (result.ok) {
                    switch (result.type) {
                        case "form":
                            new FormWindow(result);
                            break;
                        case "grid":
                            new GridWindow(result);
                            break;
                        case "redirect":
                            window.location.href = result.data[0];
                            break;
                        default:
                            new MessageDialog("尚未支持");
                    }
                }
                else {
                    new MessageDialog(result.data);
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
            dataType: "json",
            cache: false,
            error: function () {
                new MessageDialog("HTTP请求失败!");
            }
        });
    };
    _init();
}

function GridWindow(grid) {
    var _grid_id;
    var _id = function (id) {
        return "grid-" + _grid_id + "-" + id;
    };
    var _c_id = function(id){
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
            if(grid.action){
                if (i % (grid.cols.length+1) == 0) {
                    content += "<tr class='grid-tr'>";
                }
            }
            else{

                if (i % grid.cols.length == 0) {
                    content += "<tr class='grid-tr'>";
                }
            }
            content += "<td>" + grid.items[i] + "</td>";
            i++;
            if (grid.action) {
                if ((i+1) % (grid.cols.length+1) == 0) {
                    content += "<td class='grid-td-opt'>";
                    if (grid.view) {
                        content += "<button title='查看' id='" + _id("view") +"-"+grid.items[i]+ "'>查看</button>";
                    }
                    if (grid.edit) {
                        content += "<button title='编辑' id='" + _id("edit") +"-"+grid.items[i]+ "'>编辑</button>";
                    }
                    if (grid.delete) {
                        content += "<button title='删除' id='" + _id("delete") +"-"+grid.items[i]+ "'>删除</button>";
                    }
                    content += "</td>";
                    content += "</tr>";
                    i++;
                }
            }
            else{
                if (i % (grid.cols.length) == 0) {
                    content += "</tr>";
                }
            }
        }
        content += "</tbody></table>";

        new HtmlDiv("grid-" + grid.id, content);

        if (grid.action != undefined) {
            if (grid.add) {
                var addBtn=$("button#" + _id("add"));
                addBtn.addClass("btn btn-primary btn-mini");
                addBtn.click(function () {
                    new Ajax(grid.action + "/add");
                });
            }
            if(grid.view){

                $("button[id^='"+_id("view")+"']").each(function(){
                    $(this).addClass("btn btn-info btn-mini");
                    $(this).click(function(){
                        new Ajax(grid.action+"/"+_c_id($(this).attr("id")), "PUT");
                    });
                });
            }
            if (grid.edit) {
                $("button[id^='"+_id("edit")+"']").each(function(){
                    $(this).addClass("btn btn-warning btn-mini");
                    $(this).click(function(){
                        new Ajax(grid.action+"/"+_c_id($(this).attr("id")));
                    });
                });
            }
            if (grid.delete) {
                $("button[id^='"+_id("delete")+"']").each(function(){
                    $(this).addClass("btn btn-danger btn-mini");
                    $(this).click(function(){
                        new Ajax(grid.action+"/"+_c_id($(this).attr("id")), "DELETE");
                    });
                });
            }
        }
    };
    _init();
}

function FormWindow(form) {
    var _form_id;
    var _field = function (id, label, input) {
        if (label == undefined) {
            return input;
        }
        var content = "<div class='control-group'>";
        content += "<label class='control-label' ";
        if (id != undefined) {
            content += " for='fm-" + _form_id + "-" + id + "'";
        }
        content += ">" + label + "：</label>";
        content += "<div class='controls'>";
        content += input;
        content += "</div>";
        content += "</div>";
        return content;
    };

    var _id = function (id) {
        return "fm-" + _form_id + "-" + id;
    };
    var _hidden_field = function (id, value) {
        return  "<input type='hidden' id='" + _id(id) + "' value='" + value + "'/>";
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
        var content = "<div class='form-actions'>";
        content += _button("submit", "提交", "danger");
        content += _button("reset", "重写", "info");
        for (var i in buttons) {
            var btn = form.buttons[i];
            content += _button(btn.id, btn.label, btn.type);
        }
        content += "</div>";
        return content;
    };
    var _init = function () {
        _form_id = form.id;

        var content = "<form class='form-horizontal' method='" + form.method + "'  action='" + form.action + "'>";
        content += "<fieldset><legend>" + form.title + "</legend>";
        content += "<div id='" + _id("alert") + "'></div>";
        content += _hidden_field("created", form.created);
        for (var i in form.fields) {
            var field = form.fields[i];
            var input;
            switch (field.type) {
                case "text":
                    input = "<input class='input-xlarge focused' style='width: " + field.width + "px' type='text' id='" + _id(field.id) + "' ";
                    if (field.value != undefined) {
                        input += "value='" + field.value + "' ";
                    }
                    if (field.readonly) {
                        input += "readonly ";
                    }
                    input += " />";
                    if (field.required) {
                        input += " *";
                    }
                    break;
                case "textarea":
                    input = "<textarea id='" + _id(field.id) + "' style='width: " + field.width + "px;height: " + field.height + "px;' ";
                    if (field.readonly) {
                        input += "readonly ";
                    }
                    input += ">";
                    if (field.value != undefined) {
                        input += field.value;
                    }
                    input += "</textarea>";
                    if (field.required) {
                        input += " *";
                    }
                    break;
                case "select":
                    input = "<select style='width: " + field.width + "px' id='" + _id(field.id) + "' ";
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
                        input += ">" + item.label + "</option>";

                    }
                    input += "</select>";
                    break;
                case "password":
                    input = "<input type='password' id='" + _id(field.id) + "' ";
                    if (field.value != undefined) {
                        input += "value='" + field.value + "' ";
                    }
                    input += " /> *";
                    break;
                case "radio":
                    input = _hidden_field(field.id, field.value);
                    var k = 1;
                    for (var j in field.options) {
                        var item = field.options[j];
                        input += "<input type='radio' name='" + _id(field.id) + "'  value='" + item.value + "' ";
                        if (item.value == field.value) {
                            input += "checked='true' ";
                        }
                        input += "/>" + item.label + " &nbsp;"

                        if (k % field.cols == 0) {
                            input += "<br/>"
                        }
                        k++;
                    }
                    break;

                case  "checkbox":
                    input = _hidden_field(field.id, field.value);
                    var k = 1;
                    for (var j in field.options) {
                        var item = field.options[j];
                        input += "<input type='checkbox' name='" + _id(field.id) + "' value='" + item.value + "' ";
                        if (item.selected) {
                            input += "checked='true' ";
                        }
                        input += "/>" + item.label + " &nbsp;";

                        if (k % field.cols == 0) {
                            input += "<br/>"
                        }
                        k++;
                    }
                    break;

                default:
                    input = _hidden_field(field.id, field.value);
                    break;
            }
            content += _field(field.id, field.label, input);
        }

        if (form.captcha) {
            var input = "<input type='text'  style='width: 80px;' id='" + _id("captcha") + "'/>* &nbsp;";
            input += "<img id='" + _id('captcha_img') + "' src='/captcha.jpg?_=" + Math.random() + "' alt='点击更换验证码'/>";
            content += _field("captcha", "验证码", input);

        }

        var reload_captcha = function () {
            $('img#' + _id('captcha_img')).attr("src", "/captcha.jpg?_=" + Math.random());
        };

        content += _button_group(form.buttons);
        content += "</fieldset></form>";
        //alert(content);
        new HtmlDiv("fm-" + form.id, content);
        $('img#' + _id('captcha_img')).click(reload_captcha);
        $('button#' + _id("reset")).click(function () {
            for (var i in form.fields) {
                var field = form.fields[i];
                switch (field.type) {
                    case "text":
                    case "password":
                    case "textarea":
                        $("input#" + _id(field.id)).val(field.value == undefined ? "" : field.value);
                        break;
                    default:
                        break;
                }
            }
            if (form.captcha) {
                $("input#" + _id("captcha")).val('');
                reload_captcha();
            }
        });

        $('button#' + _id("submit")).click(function () {
            var data = {};
            for (var i in form.fields) {
                var field = form.fields[i];
                switch (field.type) {
                    case "text":
                    case "textarea":
                    case "password":
                        data[field.id] = $('input#' + _id(field.id)).val();
                    default:
                        break;
                }
            }
            if (form.captcha) {
                data['captcha'] = $('input#' + _id('captcha')).val();
            }
            new Ajax(form.action, "POST", data);
            reload_captcha();
        });

    };

    _init();
}

function HtmlDiv(id, content) {
    var _init = function () {
        var root = "div#" + id;
        if ($(root).length == 0) {
            $("div#gl_root").append("<div id='" + id + "'></div>");
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
            name + "：</h4>" + (messages instanceof Array ? messages.join("<br/>") : messages) + "</div>");

    };
    _init();
}
