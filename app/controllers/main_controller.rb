require 'brahma/services/site'

class MainController < ApplicationController
  def index
    main = Brahma::SettingService.get 'site.index'
    if main && !['/main', '', '/'].include?(main)
      redirect_to "#{main}?locale=#{I18n.locale}", status: 301
    end
  end

  def notices
    render 'brahma_bodhi/main/notices'
  end

  def about_me
    render 'brahma_bodhi/main/about_me'
  end

  def errors
    redirect_to brahma_bodhi.main_errors_path(id: params[:id]), status: 301
  end

end
