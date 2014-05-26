Rails.application.routes.draw do

  #---------------IT-PACKAGE-----------------------
  namespace :itpkg do
    namespace :dns do
      resources :domains, :records
    end
    namespace :firewall do
      resources :outputs, :nats, :inputs, :devices
    end
    resources :hosts
  end
  #---------------监控管理-----------------------
  namespace :monitor do
    resources :hosts
  end
  #---------------CDN管理-----------------------
  namespace :cdn do
    resources :hosts
  end
  #---------------终端管理-----------------------
  resources :clients

  #---------站点其它-------------------------------------------
  get 'about_me' => 'main#about_me'
  get 'main' => 'main#index'
  root 'main#index'
  mount BrahmaBodhi::Engine, at:'/core'
end
