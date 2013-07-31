function bind_personal_bar_click() {
    $('a#personal_bar-logout').click(function () {
        if (window.confirm("您确认要退出系统么？")) {
            new Ajax("/personal/logout");
        }

    });
    $('a#personal_bar-self').click(function () {
        window.location.href = '/personal/self';
    });
}


function showHostWanFields(id, type) {
    var staticFS = ['address', 'netmask', 'gateway', 'dns1', 'dns2'];
    var pppoeFS = ['username', 'password'];
    var showF = function (fields, flag) {
        for (var i in fields) {
            var fid = $("input#fm-" + id + "-" + fields[i]).parent().parent();
            if (flag) {
                fid.show();
            } else {
                fid.hide();
            }
        }
    };
    showF(staticFS, false);
    showF(pppoeFS, false);
    switch (type) {
        case 'STATIC':
            showF(staticFS, true);
            break;
        case 'DHCP':
            break;
        case "PPPOE":
            showF(pppoeFS, true);
            break;
    }
}