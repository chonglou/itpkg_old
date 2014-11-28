require 'rails_helper'

feature 'Document' do
  scenario 'Help' do
    visit document_show_path(name: 'help')
    expect(page).to have_text('Help Center')
  end
  scenario 'About Us' do
    visit document_show_path(name: 'about_us')
    expect(page).to have_text('About Us')
  end
end
