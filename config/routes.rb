Rails.application.routes.draw do
  mount BrahmaBodhi::Engine, at:'/core'
  get 'about_me' => 'main#about_me'
  %w(404 422 500 505).each { |e| match "/#{e}" => 'main#errors', id: e, via: [:get, :post, :put, :patch, :delete] }

end
