//= require brahma_bodhi/init

$(function () {
    var uri = window.location.pathname;
    if (uri == '/') {
        uri = '/main'
    }
    Brahma.active_nav_link(uri);
});