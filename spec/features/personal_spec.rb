require 'rails_helper'

describe 'Personal', type: :feature do
  context 'no login' do
    it 'Register' do
      visit new_user_registration_path
      label = 'test'
      fill_in 'Label', with: label
      fill_in 'Email', with: 'test@aaa.com'
      fill_in 'Password', with: '12345678'
      fill_in 'Password confirmation', with: '12345678'
      click_button 'Register'

      expect(page).to have_text(I18n.t('devise.registrations.signed_up_but_unconfirmed'))
      #User.destroy label:label
    end

    it 'Register (exist label)' do
      visit new_user_registration_path
      fill_in 'Label', with: 'e1'
      fill_in 'Email', with: 'test1@aaa.com'
      fill_in 'Password', with: '12345678'
      fill_in 'Password confirmation', with: '12345678'
      click_button 'Register'

      expect(page).to have_text('Label is invalid')

    end

    it 'Register (no label)' do
      visit new_user_registration_path
      fill_in 'Email', with: 'test2@aaa.com'
      fill_in 'Password', with: '12345678'
      fill_in 'Password confirmation', with: '12345678'
      click_button 'Register'

      expect(page).to have_text("Label can't be blank")
    end

    it 'Index (non login)' do
      visit personal_path
      expect(page).to have_text(I18n.t('devise.failure.unauthenticated'))
    end
  end

  def admin_paths
    [
        clients_path,
        email_path,
        vpn_path,
        dns_path,
        nginx_hosts_path,
        templates_path,
        settings_path
    ]
  end

  context 'no admin' do
    it 'Index (non admin)' do
      login :manager1

      visit personal_path

      expect(page).to have_content(edit_user_registration_path)
      admin_paths.each { |p| expect(page).to have_no_content(p) }
      logout
    end
  end

  context 'admin' do
    it 'Index (admin)' do
      login :admin
      visit personal_path
      paths.each { |p| expect(page).to have_content(p) }
      logout
    end
  end

end