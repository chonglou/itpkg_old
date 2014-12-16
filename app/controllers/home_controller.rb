require 'itpkg/utils/string_helper'

class HomeController < ApplicationController
  def index
    redirect_to user_signed_in? ? personal_path : new_user_session_path
  end
  def search
    flash[:alert] = t('labels.not_support')
    redirect_to document_show_path(name:'help')
  end

  def document
    name = "#{Rails.root}/doc/#{I18n.locale}/#{params[:name]}.md"
    if File.exist?(name)
      @title, @content = File.open(name, 'r') do |f|
        t = f.readline
        f.seek 0
        [t, Itpkg::StringHelper.md2html(f.read)]
      end
    else
      render status: 404
    end
  end
end
