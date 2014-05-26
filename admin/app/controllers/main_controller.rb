require 'brahma/services/site'
require 'brahma/utils/string_helper'

class MainController < ApplicationController
  def index
    @notices =notices2html Brahma::NoticeService.least(20)
  end

  def search
    flash[:alert] = ["暂无搜索[#{params[:key]}]的结果"]
    redirect_to '/main'
  end

  def archive
    year = params[:year].to_i
    month = params[:month].to_i
    day = params[:day] ? params[:day].to_i : nil

    if year && month
      @notices = notices2html Brahma::NoticeService.range(year, month, day)
      render 'main/index'
    else
      not_found
    end
  end

  def about_me
    render 'brahma_bodhi/main/about_me'
  end

  private
  def notices2html(notices)
    notices.each { |t| t.content = Brahma::Utils::StringHelper.md2html t.content }
  end

end
