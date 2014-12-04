$(function(){
    $("select#dns_record_flag").change(function(){
        var hidden = function(id){
            $('#dns_record_'+id).parent().parent().hide();
        };
        var set_host=function(v){
            $('input#dns_record_host').val(v);
        };
        $("[id^='dns_record_']").parent().parent().show();
        switch( this.value){
            case 'SOA':
                hidden('host');
                hidden('data');
                hidden('mx_priority');
                set_host('@');
                break;
            case 'NS':
                hidden('host');
                hidden('mx_priority');
                set_host('@');
                break;
            case 'A':
                hidden('mx_priority');
                set_host('');
                break;
            case 'MX':
                hidden('mx_priority');
                set_host('');
                break;
        }
    });
});