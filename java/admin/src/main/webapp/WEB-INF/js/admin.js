$(document).ready(function () {
    $("a[id^='left_nav-控制面板-']").each(function () {
        $(this).click(function () {
            var url = $(this).attr("id").split("-")[2];
            alert(url);
        });
    });
});