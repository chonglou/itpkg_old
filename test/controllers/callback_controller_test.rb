require 'test_helper'

class CallbackControllerTest < ActionController::TestCase
  test "should get git" do
    get :git
    assert_response :success
  end

end
