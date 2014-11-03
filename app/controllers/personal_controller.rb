class PersonalController < ApplicationController
  before_action :authenticate_user!

  def index
    @icons=[
        {
            url:projects_path,
            logo:'flat/256/businessman3.png',
            label:t('links.projects')
        }
    ]
  end
end
