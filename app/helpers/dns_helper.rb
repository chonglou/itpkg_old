module DnsHelper
  def dns_country_options
    options = []
    size=GeoIP::CountryCode.size-1
    1.upto(size) do |i|
      options << [GeoIP::CountryName[i], GeoIP::CountryCode[i]]
    end
    options
  end

  def dns_region_options(country)
    options=[%w(ALL *)]
    regions = GeoIP::RegionName[country]
    regions.each { |k, v| options << [v, k] } if regions
    options
  end


  def dns_flag_options
    [%w(A A), %w(NS NS), %w(SOA SOA), %w(MX MX)]
  end

  def dns_ttl_options
    [1, 3, 5, 10, 60, 120].map { |m| ["#{m*60}s", m*60] }
  end

  def dns_acl_options
    options = [%w(DEFAULT 0)]
    Dns::Acl.all.each{|a| options << [a.to_s, a.id]}
    options
  end

  def dns_mx_priority_options
    1.upto(10).map{|i| [i, i]}
  end
end