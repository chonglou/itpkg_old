require 'itpkg/utils/string_helper'

class HomeController < ApplicationController
  def index
    u = current_user
    if u
    else

    end
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
