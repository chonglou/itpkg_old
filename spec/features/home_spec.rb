require 'rails_helper'

describe 'Home', type: :feature do

  context 'non login' do
    it 'root' do
      visit root_path
      expect(page).to have_text('Sign In')
    end
  end

  context 'has login' do

    before(:all) {login :employee1}
    after(:all) {logout}

    it 'root (login)' do
      save_and_open_page
      expect(page).to have_text('Welcome')
    end
  end
end