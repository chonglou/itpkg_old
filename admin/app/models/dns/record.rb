class Dns::Record < ActiveRecord::Base
  belongs_to :domain
  enum flag: {a: 0, mx: 1, ns: 2, cname: 3, txt: 4}
end
