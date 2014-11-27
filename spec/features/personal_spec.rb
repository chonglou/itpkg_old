require 'rails_helper'

feature 'User sign in' do
  scenario 'Register' do
    visit new_user_registration_path
    fill_in 'Label', with:'test'
    fill_in 'Email', with:'test@aaa.com'
    fill_in 'Password', with:'12345678'
    fill_in 'Password confirmation', with:'12345678'
    click_button 'Register'

    expect(page).to have_text('A message with a confirmation link has been sent to your email address')
  end

  scenario 'Register (exist label)' do
    visit new_user_registration_path
    fill_in 'Label', with:'e1'
    fill_in 'Email', with:'test1@aaa.com'
    fill_in 'Password', with:'12345678'
    fill_in 'Password confirmation', with:'12345678'
    click_button 'Register'

    expect(page).to have_text('Label is invalid')
  end

  scenario 'Register (no label)' do
    visit new_user_registration_path
    fill_in 'Email', with:'test2@aaa.com'
    fill_in 'Password', with:'12345678'
    fill_in 'Password confirmation', with:'12345678'
    click_button 'Register'

    expect(page).to have_text("Label can't be blank")
  end

end