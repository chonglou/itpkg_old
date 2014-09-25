Rails.application.routes.draw do
  #-------个人中心
  get 'personal' => 'personal#index'

  #-------其它
  root 'main#index'
  get 'main' => 'main#index'
  get 'notices' => 'main#notices'
  get 'about_me' => 'main#about_me'
  post 'search' => 'search#index'
  get 'archive/:year/:month/:day' => 'archive#show', as: :archive_by_day
  get 'archive/:year/:month' => 'archive#show', as: :archive_by_month
  %w(404 422 500 505).each { |e| match "/#{e}" => 'main#errors', id: e, via: [:get, :post, :put, :patch, :delete] }
  mount BrahmaBodhi::Engine, at:'/core'
end
