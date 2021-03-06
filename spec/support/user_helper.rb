module UserHelper
  def login(uid)
    @user = FactoryGirl.create(uid)
    @user.confirm!
    visit new_user_session_path
    fill_in 'Email', with:@user.email
    fill_in 'Password', with:@user.password
    click_button 'Sign In'
    puts '#'*80
  end
  def logout
    visit destroy_user_session_path
  end
end
