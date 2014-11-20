# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)

Setting.init = Time.now

ROOT=1
Permission.create role: "user://#{ROOT}", resource: 'SYSTEM', operation: 'ROOT', start_date: Date.today.strftime, end_date: '9999-12-31'
Permission.create role: "user://#{ROOT}", resource: 'SYSTEM', operation: 'ADMIN', start_date: Date.today.strftime, end_date: '9999-12-31'
n1 = Notice.create user_id: ROOT, body: 'IT-PACKAGE System is online now!'
n2 = Notice.create user_id: ROOT, body: 'IT-PACKAGE 系统正式上线!'
Translation.create flag: 'notice', en: n1.id, 'zh-CN' => n2.id

require 'itpkg/linux/certificate'
Certificate.create Linux::Certificate.root(10)

# todo
#require 'itpkg/constants'
#Itpkg::TEMPLATES_TO_LOAD.each { |t| Template.create t }

