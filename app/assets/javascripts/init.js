$(function () {
    var uri = window.location.pathname;
    if (uri == '/') {
        uri = '/home'
    }
    else if(uri.indexOf('/users/')==0 || uri.indexOf('/projects') == 0){
        uri = '/personal'
    }
    Itpkg.active_nav_link(uri);
});
