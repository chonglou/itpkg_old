Rails.application.routes.draw do

  get 'about_me' =>'home#about_me'
  post 'search' => 'home#search'

  devise_for :users
  root 'home#index'


end
