class Dns::Acl < ActiveRecord::Base
  validates :country, :region, presence: true
  validates :country, uniqueness: {scope: :region}
  def to_s
    "#{self.region == '*' ? 'ALL' : GeoIP::RegionName[self.country][self.region]}, #{GeoIP::CountryName[GeoIP::CountryCode.index(self.country)]}"
  end
end
