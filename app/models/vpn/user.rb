class Vpn::User < ActiveRecord::Base
  belongs_to :host
  enum state: {submit: 0, enable: 1, disable: 2}
end
