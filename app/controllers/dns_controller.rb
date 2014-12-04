
# rndc-confgen > /etc/rndc.conf
# sed -n '15,23p' /etc/rndc.conf | awk '{$1="";print $0}' > /etc/named.conf
# dig soa example.com @localhost


# "GRANT SELECT ON `#{db}`.`dns_records` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
#     "GRANT SELECT ON `#{db}`.`dns_xfrs` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
#     "GRANT UPDATE ON `#{db}`.`dns_counts` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
#     'FLUSH PRIVILEGES'
# "DROP USER 'dns'@'#{host}'", 'FLUSH PRIVILEGES'

class DnsController < ApplicationController
   before_action :must_admin!

   def regions
     render 'regions', layout:false
   end
  def index
     @items=[
        {
            url: dns_acls_path,
            logo: 'flat/256/calendar68.png',
            label: t('links.dns_acl.list')
        },
        {
            url: dns_records_path,
            logo: 'flat/256/verification5.png',
            label: t('links.dns_record.list')
        }
    ]

  end
end
