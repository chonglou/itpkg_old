class PersonalController < ApplicationController
  before_action :authenticate_user!

  def index
    @items=[
        {
            url:projects_path,
            logo:'flat/256/businessman3.png',
            label:t('links.projects')
        },

        {
            url:edit_user_registration_path,
            logo:'flat/256/profile8.png',
            label:t('links.personal.info')
        }
    ]
  end
end
