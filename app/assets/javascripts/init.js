$(function () {
    var uri = window.location.pathname;
    if (uri == '/') {
        uri = '/home'
    }
    Itpkg.active_nav_link(uri);
});
