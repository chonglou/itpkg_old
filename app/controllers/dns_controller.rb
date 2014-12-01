# INSERT INTO `dns_records`
# (`id`,`zone`,`host`,`type`,`data`,`ttl`,`mx_priority`,`refresh`,`retry`,`expire`,`minimum`,`serial`,`resp_person`,`primary_ns`)
# VALUES
# (1, 'example.com', '@', 'SOA', NULL, 180, NULL, 10800, 7200, 604800, 86400, 2011091101, 'admins.mail.hotmail.com', '77.84.21.84'),
# (2, 'example.com', '@', 'NS', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (3, 'example.com', '@', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (4, 'example.com', 'www', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (5, 'xn--unicode-example.com', '@', 'SOA', NULL, 180, NULL, 10800, 7200, 604800, 86400, 2011091101, 'admins.mail.hotmail.com', '77.84.21.84'),
# (6, 'xn--unicode-example.com', '@', 'NS', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (7, 'xn--unicode-example.com', '@', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (8, 'xn--unicode-example.com', 'www', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
# ;

# rndc-confgen > /etc/rndc.conf
# sed -n '15,23p' /etc/rndc.conf | awk '{$1="";print $0}' > /etc/named.conf
# dig soa example.com @localhost


# "GRANT SELECT ON `#{db}`.`dns_records` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
#     "GRANT SELECT ON `#{db}`.`dns_xfrs` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
#     "GRANT UPDATE ON `#{db}`.`dns_counts` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
#     'FLUSH PRIVILEGES'
# "DROP USER 'dns'@'#{host}'", 'FLUSH PRIVILEGES'

class DnsController < ApplicationController
  def index
  end
end
