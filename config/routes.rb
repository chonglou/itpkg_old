Rails.application.routes.draw do


  #--------------- My Add -----------------
  resources :monitor_nodes
  resources :logging_nodes
  namespace :email do
    resources :domains
    resources :users
    resources :aliases
  end

  get 'vpn' => 'vpn#index'
  namespace :vpn do
    get 'logs' => 'logs#index'
    resources :users, expect:[:show]
    resources :hosts do
      get 'install_sh'
    end
  end

  resources :projects

  get 'settings' => 'settings#index'

  get 'personal'=>'personal#index'

  get 'document/*name'=>'home#document', as: :document_show
  get 'home'=>'home#index'
  post 'search' => 'home#search'

  devise_for :users
  root 'home#index'


end
