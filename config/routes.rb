require 'sidekiq/web'

Rails.application.routes.draw do


  #--------------- My Add -----------------

  ###################### mail.localhost.localdomain ##############
  constraints subdomain: 'mail' do
    #------mail_box--------

    get 'mail_boxes/sign_out'

    post 'mail_boxes/search'

    %w(sign_in password).each do |a|
      get "mail_boxes/#{a}"
      post "mail_boxes/#{a}"
    end

    resources :mail_boxes, only:[:index, :new, :create]

    get '/' => 'mail_boxes#index', as: :mail_home
  end


  ###################### www.localhost.localdomain ##############
  constraints subdomain: 'www' do
    #-------wiki---------
    resources :wikis

    #------status----------
    get '/status/user/:id', to: 'status#user', as: :get_status_user
    post '/status/user/:id', to: 'status#user', as: :post_status_user
    namespace :status do
      %w(workers logs versions users).each { |a| get a }

    end

    #----------- docker-----------
    resources :nodes
    resources :node_types do
      post 'build'
      post 'push'
      resources :templates, expect: [:index, :show], controller: 'node_types/templates'
      resources :vars, expect: [:index, :show], controller: 'node_types/vars'
      resources :ports, expect: [:index, :show], controller: 'node_types/ports'
      resources :volumes, expect: [:index, :show], controller: 'node_types/volumes'
    end


    #------------callback---------
    namespace :callback do
      post 'git'
      get 'confirm'
    end

    #-------------Repositories-----------
    resources :repositories do
      resources :users, expect: [:edit, :update], controller: 'repositories/users'
      %w(commits changes tree file).each { |a| get a }
    end

    #-------------DNS-------------
    get 'dns' => 'dns#index'
    namespace :dns do
      get 'regions'
      resources :records, expect: [:show]
      resources :acls, expect: [:show]
    end


    #---------email ------------------
    get 'email' => 'email#index'
    namespace :email do
      resources :domains, expect: [:show]
      resources :users, expect: [:show]
      resources :aliases, only: [:destroy, :new, :create, :index]
    end

    #----------vpn-----------
    get 'vpn' => 'vpn#index'
    namespace :vpn do
      get 'logs' => 'logs#index'
      resources :users, expect: [:show]
    end

    #--------team work-----------
    resources :projects do
      resources :documents, controller: 'projects/documents' do
        get 'download'
        get 'viewer'
      end
      resources(:stories, controller: 'projects/stories') do
        resources :tasks, controller: 'projects/tasks'
      end
    end

    #----------others---------
    get 'personal' => 'personal#index'
    namespace :personal do
      get 'logs'

      post 'generate_keys'

      %w(public_key contact).each do |n|
        get n
        post n
      end

    end


    get 'document/*name' => 'home#document', as: :document_show
    post 'search' => 'home#search'
    devise_for :users#, controllers: {registrations: 'registrations'}


    authenticate :user, lambda { |u| u.is_admin? } do
      mount Sidekiq::Web => '/sidekiq'
    end

    get '/' => 'home#index', as: :www_home
  end

  root 'home#index'

end
