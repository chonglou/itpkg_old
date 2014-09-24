class MainController < ApplicationController
  def about_me
    render 'brahma_bodhi/main/about_me'
  end

   def errors
      redirect_to brahma_bodhi.main_errors_path(id:params[:id]), status: 301
    end

end
