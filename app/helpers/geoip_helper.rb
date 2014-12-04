
module GeoipHelper
  def geoip_country_options
    options = []
    size=GeoIP::CountryCode.size-1
    1.upto(size) do |i|
      options << [GeoIP::CountryName[i], GeoIP::CountryCode[i]]
    end
    options
  end
  def geoip_region_options(country)
    options=[ %w(ALL *)]
    regions = GeoIP::RegionName[country]
    regions.each{|k,v| options << [v, k]} if regions
    options
  end
end