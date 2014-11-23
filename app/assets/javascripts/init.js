$(function () {
    var uri = window.location.pathname;
    if (uri == '/') {
        uri = '/home'
    }
    Itpkg.active_nav_link(uri);

    $('pre code').each(function(i, block) {
        hljs.highlightBlock(block);
    });
});


