Rails.application.routes.draw do

  %w(help about_us).each {|l| get l => "home##{l}"}
  get 'home'=>'home#index'
  post 'search' => 'home#search'

  devise_for :users
  root 'home#index'


end
