Rails.application.routes.draw do

  #--------------个人中心------------------------
  get 'personal'=>'personal#index'
  get 'personal/company'
  post 'personal/company'

  #---------------防火墙-----------------------
  namespace :firewall do
    resources :outputs, :nats, :inputs, :devices
  end
  get 'firewall' =>'firewall#index'
  #--------------DNS--------------------------
  namespace :dns do
    resources :domains, :records
  end
  get 'dns' =>'dns#index'
  #--------------VPN--------------------------
  namespace :vpn do
    resources :users
  end
  get 'vpn' =>'vpn#index'
  #---------------监控管理-----------------------
  namespace :monitor do
  end
  get 'monitor' =>'monitor#index'
  #---------------CDN管理-----------------------
  namespace :cdn do
  end
  get 'cdn' =>'cdn#index'
  #---------------终端管理-----------------------
  resources :clients
  #---------站点其它-------------------------------------------
  get 'about_me' => 'main#about_me'
  get 'main' => 'main#index'
  post 'search' => 'main#search'
  get 'archive/:year/:month/:day' => 'main#archive'
  get 'archive/:year/:month' => 'main#archive'

  root 'main#index'
  mount BrahmaBodhi::Engine, at: '/core'
end
