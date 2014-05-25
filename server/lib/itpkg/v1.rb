require 'grape'
require_relative '../models'

module Itpkg
  class API < Grape::API
    version 'v1', using: :header, vendor: 'brahma'
    format :json

    resources :tasks do
      desc '取得任务'
      params do
        requires :id, type: string, desc: 'Client ID'
        requires :key, type: string, desc: 'Client KEY'
      end
      get do
        Itpkg::Response.new true
      end

      desc '返回结果'
      params do
        requires :id, type: string, desc: 'Client ID'
        requires :key, type: string, desc: 'Client KEY'
        requires :result, type: string, desc: '结果'
      end
      post do
        Itpkg::Response.new true
      end

    end

  end
end