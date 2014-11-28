require 'rails_helper'

describe 'Vpn', type: :feature do

  def check_for_admin(path)
    visit path
    expect(page).to have_text(I18n.t('labels.must_admin'))
    login :manager1

    visit path
    expect(page).to have_text(I18n.t('labels.must_admin'))
    logout

    login :admin
    visit path
  end
  it 'index' do
    check_for_admin vpn_path

    logout
  end


end
