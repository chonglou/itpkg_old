Rails.application.routes.draw do

  #--------------- My Add -----------------

  #----------- docker-----------
  resources :nodes
  resources :node_types do
    resources :templates
  end


  #------------callback---------
  namespace :callback do
    post 'git'
    get 'confirm'
  end

  #-------------Repositories-----------
  resources :repositories do
    resources :users, expect:[:edit, :update], controller:'repositories/users'
    %w(commits changes tree file).each {|a| get a}
  end

  #-------------DNS-------------
  get 'dns' => 'dns#index'
  namespace :dns do
    resources :hosts, expect:[:show]
    resources :records, expect:[:show]
  end


  #---------email ------------------
  get 'email' => 'email#index'
  namespace :email do
    resources :domains, expect:[:show]
    resources :users, expect:[:show]
    resources :aliases, only:[:destroy, :new, :create, :index]
    # todo
    resources :hosts do
    get 'install_sh'
  end
  end

  #----------vpn-----------
  get 'vpn' => 'vpn#index'
  namespace :vpn do
    get 'logs' => 'logs#index'
    resources :users, expect:[:show]
    # todo
    resources :hosts do
      get 'install_sh'
    end
  end

  #--------team work-----------
  resources :projects do
    resources :documents
  end

  #----------others---------

  get 'settings' => 'settings#index'

  get 'personal'=>'personal#index'
  namespace :personal do
    post 'update_public_key'
    post 'generate_keys'
  end


  get 'document/*name'=>'home#document', as: :document_show
  get 'home'=>'home#index'
  post 'search' => 'home#search'

  devise_for :users
  root 'home#index'


end
