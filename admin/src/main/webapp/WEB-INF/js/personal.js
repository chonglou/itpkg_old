function bind_personal_bar_click() {
    $('a#personal_bar-logout').click(function () {
        if(window.confirm("您确认要退出系统么？")){
            new Ajax("/personal/logout");
        }

    });
    $('a#personal_bar-self').click(function () {
        window.location.href = '/personal/self';
    });
}