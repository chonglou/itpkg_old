# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)
INIT='site.init'
unless Setting.find_by(key: 'site.init')
  ROOT=1
  Permission.create role: "user://#{ROOT}", resource: 'SYSTEM', operation: 'ROOT', start_date: Date.today.strftime, end_date: '9999-12-31'
  Permission.create role: "user://#{ROOT}", resource: 'SYSTEM', operation: 'ADMIN', start_date: Date.today.strftime, end_date: '9999-12-31'
  n1 = Notice.create user_id: ROOT, body: 'IT-PACKAGE System is online now!'
  n2 = Notice.create user_id: ROOT, body: 'IT-PACKAGE 系统正式上线!'
  Translation.create flag: 'notice', en: n1.id, 'zh-CN' => n2.id
  Setting.create key:'site.init', val:Time.now
end
