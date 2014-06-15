Rails.application.routes.draw do

  #--------------个人中心------------------------
  get 'personal' => 'personal#index'
  get 'personal/company'
  post 'personal/company'
  #--------------邮件服务器管理------------------
  namespace :email do
    resources :users do
      get 'state', on: :member
      post 'state', on: :member
    end
    resources :domains
    get 'help', to: redirect('/help/email')
    get 'user/:client_id' => 'users#index'
    get 'domain/:client_id' => 'domains#index'
  end

  get 'email/show/:client_id' => 'email#show'
  get 'email/info/:client_id' => 'email#info'
  post 'email/info/:client_id' => 'email#info'
  get 'email' => 'email#index'
  #---------------防火墙-----------------------
  namespace :firewall do
    resources :outputs, :nats, :inputs, :devices
    get 'help', to: redirect('/help/firewall')
  end
  get 'firewall' => 'firewall#index'
  #--------------DNS--------------------------
  namespace :dns do
    resources :domains, :records
    get 'help', to: redirect('/help/dns')
    get 'record/:client_id' => 'records#index'
    get 'domain/:client_id' => 'domains#index'
  end
  get 'dns/show/:client_id' => 'dns#show'
  get 'dns/info/:client_id' => 'dns#info'
  post 'dns/info/:client_id' => 'dns#info'
  get 'dns' => 'dns#index'
  #--------------VPN--------------------------
  namespace :vpn do
    resources :users do
      get 'state', on: :member
      post 'state', on: :member
    end
    get 'help', to: redirect('/help/vpn')
    get 'user/:client_id' => 'users#index'
  end
  get 'vpn/show/:client_id' => 'vpn#show'
  get 'vpn/info/:client_id' => 'vpn#info'
  post 'vpn/info/:client_id' => 'vpn#info'
  get 'vpn' => 'vpn#index'
  #---------------监控管理-----------------------
  namespace :monitor do
    get 'help', to: redirect('/help/monitor')
  end
  get 'monitor' => 'monitor#index'
  #---------------CDN管理-----------------------
  namespace :cdn do
    get 'help', to: redirect('/help/cdn')
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
  get 'help/:name' => 'main#help'

  root 'main#index'
  mount BrahmaBodhi::Engine, at: '/core'
end
