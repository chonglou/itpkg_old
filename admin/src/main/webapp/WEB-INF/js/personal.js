
function bind_personal_bar_click(){
    $('a#personal_bar-logout').click(function(){
        new Ajax("/personal/logout");
    });
    $('a#personal_bar-self').click(function(){
        window.location.href='/personal/self';
    });
}