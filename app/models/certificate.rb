class Certificate < ActiveRecord::Base
  attr_encrypted :key, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt
end
