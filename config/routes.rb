Rails.application.routes.draw do



  #--------------- My Add -----------------
  resources :monitor_nodes
  resources :logging_nodes
  namespace :email do
    resources :domains
    resources :users
    resources :aliases
  end

  namespace :vpn do
    get 'logs' => 'logs#index'
    resources :users, expect:[:show]

    get 'setup/files'
    post 'setup/grant'

  end

  resources :projects

  get 'personal'=>'personal#index'

  get 'document/*name'=>'home#document', as: :document_show
  get 'home'=>'home#index'
  post 'search' => 'home#search'

  devise_for :users
  root 'home#index'


end
