require 'grape'
require_relative '../models'
require_relative '../client'

module Itpkg
  class API < Grape::API
    version 'v1', using: :header, vendor: 'brahma'
    format :json

    helpers do
      def current_client
        @current_client ||= Itpkg::Client.authorize!(env)
      end
      def authenticate!
        error!('401 Unauthorized', 401) unless current_client
      end
    end

    resources :status do
      desc '当前状态'
      get do
        Itpkg::Response.new(true).to_h
      end
    end

    resources :tasks do
      desc '取得任务'
      get do
        authenticate!
        Itpkg::Response.new true
      end

      desc '返回结果'
      params do
        requires :result, type: String, desc: '结果'
      end
      post do
        authenticate!
        Itpkg::Response.new true
      end

    end

  end
end