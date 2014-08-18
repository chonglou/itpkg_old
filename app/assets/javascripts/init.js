//= require brahma_bodhi/init

$(function(){
    var uri = window.location.pathname;
    if(uri == '/'){
        uri = '/main'
    }
    var ops = ['/vpn/', '/firewall', '/email', '/monitor', '/dns', '/cdn'];
    for(var i in ops){
        if(uri.indexOf(ops[i]) ==0){
            uri = '/ops';
            break;
        }
    }
    var pm = ['/projects'];
    for(var i in ops){
        if(uri.indexOf(ops[i]) ==0){
            uri = '/pm';
            break;
        }
    }
    Brahma.active_nav_link(uri);
});
