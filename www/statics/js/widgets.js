function Ajax(id, url, type, data, success, async) {
    var _init = function () {
        if (type == undefined) {
            type = "GET";
        }
        if (data == undefined) {
            data = {};
        }
        if (type != "GET") {
            data['_xsrf'] = get_cookie();
        }
        if (success == undefined) {
            success = function (result) {
                $("div#" + id).html(result);
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
            cache: false,
            error: function () {
                alert("HTTP请求失败！");
            }
        });
    };
    _init();
}

function get_cookie() {
    var r = document.cookie.match("\\b" + "_xsrf" + "=([^;]*)\\b");

    return r ? r[1] : undefined;
}

function log(data) {
    for (var i in data) {
        console.log(i + "\t" + data[i]);
    }
}