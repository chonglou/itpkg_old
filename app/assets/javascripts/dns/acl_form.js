$(function () {
    $("select#dns_acl_country").change(function () {
        $.get(
            "/dns/regions",
            {country: this.value},
            function (data) {
                var w = $("select#dns_acl_region");
                w.empty();
                w.append(data);
            });

    });
});