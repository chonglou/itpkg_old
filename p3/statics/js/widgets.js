$.ajaxSetup({
    beforeSend: function (xhr, settings) {

        function get_cookie() {
            var r = document.cookie.match("\\b" + "_xsrf" + "=([^;]*)\\b");
            return r ? r[1] : undefined;
        }

        if (settings.type == 'POST' || settings.type == 'PUT' || settings.type == 'DELETE') {
            xhr.setRequestHeader("X-CSRFToken", get_cookie());
        }
    }
});


function Ajax(id, url, type, data, success, async) {
    var _init = function () {
        if (type == undefined) {
            type = "GET";
        }
        if (data == undefined) {
            data = {};
        }
        if (success == undefined) {
            success = function (result) {
                if(result.goto == undefined){
                    $("div#" + id).html(result);
                }
                else{
                    window.location.href = result.goto;
                }

            }
        }
        if (async == undefined) {
            async = true;
        }

        if (!async) {
            $('body').css('cursor', 'wait');
        }

        $.ajax({
            url: url,
            type: type,
            data: data,
            success: success,
            async: async,
            cache: false,

            complete: function () {
                $('body').css('cursor', 'default');
            },

            error: function () {
                alert("HTTP请求失败！");
            }
        });
    };
    _init();
}


