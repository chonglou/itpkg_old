class Router::Firewall::Nat < ActiveRecord::Base
  belongs_to :host
  enum protocol:{tcp:0,udp:1}
end
