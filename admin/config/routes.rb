Rails.application.routes.draw do

  #--------------个人中心------------------------
  get 'personal' => 'personal#index'
  get 'personal/company'
  post 'personal/company'
  #--------------邮件服务器管理------------------
  namespace :email do
    resources :users, :domains
    get 'help'
  end
  get 'email' => 'email#index'
  #---------------防火墙-----------------------
  namespace :firewall do
    resources :outputs, :nats, :inputs, :devices
    get 'help'
  end
  get 'firewall' => 'firewall#index'
  #--------------DNS--------------------------
  namespace :dns do
    resources :domains, :records
    get 'help'
  end
  get 'dns' => 'dns#index'
  #--------------VPN--------------------------
  namespace :vpn do
    resources :users do
      get 'state', on: :member
      post 'state', on: :member
    end
    get 'help'
    get 'user/:client_id' => 'users#index'
  end
  get 'vpn/show/:client_id' => 'vpn#show'
  get 'vpn/info/:client_id' => 'vpn#info'
  post 'vpn/info/:client_id' => 'vpn#info'
  get 'vpn' => 'vpn#index'
  #---------------监控管理-----------------------
  namespace :monitor do
    get 'help'
  end
  get 'monitor' => 'monitor#index'
  #---------------CDN管理-----------------------
  namespace :cdn do
    get 'help'
  end
  get 'cdn' => 'cdn#index'
  #---------------终端管理-----------------------
  resources :clients do
    %w(state reset).each do |act|
      get act, on: :member
      post act, on: :member
    end
  end
  #---------站点其它-------------------------------------------
  get 'about_me' => 'main#about_me'
  get 'main' => 'main#index'
  post 'search' => 'main#search'
  get 'archive/:year/:month/:day' => 'main#archive'
  get 'archive/:year/:month' => 'main#archive'

  root 'main#index'
  mount BrahmaBodhi::Engine, at: '/core'
end
