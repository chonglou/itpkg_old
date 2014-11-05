Rails.application.routes.draw do

  namespace :email do
    resources :domains
    resources :users
  end

  namespace :vpn do
    resources :users
  end

  resources :projects

  get 'personal'=>'personal#index'

  get 'document/*name'=>'home#document', as: :document_show
  get 'home'=>'home#index'
  post 'search' => 'home#search'

  devise_for :users
  root 'home#index'


end
