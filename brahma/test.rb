require File.dirname(__FILE__)+'/brahma'
require File.dirname(__FILE__)+'/site/models'
require File.dirname(__FILE__)+'/site/utils'
require 'test/unit'
require 'rack/test'


class AppTest < Test::Unit::TestCase
  include Rack::Test::Methods
  def app
    Sinatra::Application
  end

  def test_index
    get "/"
  end
  def test_utils
    puts "\n"
    u = Brahma::Site::Utils.instance

    puts u.uuid
    puts u.rand_str(8)
    puts u.md5("123456")
    puts u.sha512("123456")

    obj1 = Brahma::Site::SmtpCfg.new("127.0.0.1", 123, "aaa", "ppp")
    str = u.obj2str(obj1)
    obj2 = u.str2obj(str)
    puts obj1, obj2, obj1.port
    assert_equal obj1.port, obj2.port

    s = "12345678sfew9"
    e = u.password s
    assert_equal u.check(s, e), true

    e = u.encrypt(s)
    puts s, e.length
    assert_equal s, u.decrypt(e)
  end
end