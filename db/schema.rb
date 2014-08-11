# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140811220404) do

  create_table "brahma_bodhi_attachments", force: true do |t|
    t.integer  "user_id",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "file"
  end

  create_table "brahma_bodhi_friend_links", force: true do |t|
    t.string   "logo"
    t.string   "domain",  null: false
    t.string   "name",    null: false
    t.datetime "created", null: false
  end

  create_table "brahma_bodhi_logs", force: true do |t|
    t.integer  "user_id",           default: 0, null: false
    t.string   "message",                       null: false
    t.integer  "flag",    limit: 1, default: 0, null: false
    t.datetime "created",                       null: false
  end

  create_table "brahma_bodhi_notices", force: true do |t|
    t.text     "content",   null: false
    t.datetime "last_edit", null: false
    t.datetime "created",   null: false
  end

  create_table "brahma_bodhi_permissions", force: true do |t|
    t.string   "resource",              null: false
    t.string   "role",                  null: false
    t.string   "operation",             null: false
    t.datetime "startup",               null: false
    t.datetime "shutdown",              null: false
    t.datetime "created",               null: false
    t.integer  "version",   default: 0, null: false
  end

  add_index "brahma_bodhi_permissions", ["operation"], name: "index_brahma_bodhi_permissions_on_operation", using: :btree
  add_index "brahma_bodhi_permissions", ["resource"], name: "index_brahma_bodhi_permissions_on_resource", using: :btree
  add_index "brahma_bodhi_permissions", ["role"], name: "index_brahma_bodhi_permissions_on_role", using: :btree

  create_table "brahma_bodhi_settings", force: true do |t|
    t.string   "key",                 null: false
    t.binary   "val",                 null: false
    t.integer  "version", default: 0, null: false
    t.datetime "created",             null: false
  end

  add_index "brahma_bodhi_settings", ["key"], name: "index_brahma_bodhi_settings_on_key", unique: true, using: :btree

  create_table "brahma_bodhi_users", force: true do |t|
    t.string   "open_id",                          null: false
    t.string   "username",                         null: false
    t.integer  "flag",       limit: 1, default: 0, null: false
    t.integer  "state",      limit: 1, default: 0, null: false
    t.binary   "contact",                          null: false
    t.datetime "last_login"
    t.datetime "created",                          null: false
  end

  add_index "brahma_bodhi_users", ["open_id"], name: "index_brahma_bodhi_users_on_open_id", unique: true, using: :btree

  create_table "cdn_hosts", force: true do |t|
    t.integer  "client_id", null: false
    t.datetime "created",   null: false
  end

  create_table "clients", force: true do |t|
    t.integer  "user_id",                       null: false
    t.string   "name",                          null: false
    t.text     "details"
    t.integer  "flag",    limit: 2, default: 0, null: false
    t.integer  "state",   limit: 2, default: 0, null: false
    t.string   "serial",                        null: false
    t.string   "secret",                        null: false
    t.datetime "created",                       null: false
  end

  add_index "clients", ["serial"], name: "index_clients_on_serial", unique: true, using: :btree

  create_table "companies", force: true do |t|
    t.string   "name",    null: false
    t.text     "details"
    t.integer  "user_id", null: false
    t.datetime "created", null: false
  end

  create_table "dns_domains", force: true do |t|
    t.string   "name",                  null: false
    t.integer  "host_id",               null: false
    t.integer  "ttl",     default: 300, null: false
    t.datetime "created",               null: false
  end

  create_table "dns_hosts", force: true do |t|
    t.integer  "client_id",                     null: false
    t.string   "dns1",      default: "8.8.8.8", null: false
    t.string   "dns2",      default: "8.8.4.4", null: false
    t.datetime "created",                       null: false
  end

  create_table "dns_records", force: true do |t|
    t.string   "name",                            null: false
    t.integer  "flag",      limit: 2, default: 0, null: false
    t.string   "value",                           null: false
    t.integer  "domain_id",                       null: false
    t.integer  "priority",  limit: 2, default: 0, null: false
    t.datetime "created",                         null: false
  end

  create_table "email_domains", force: true do |t|
    t.integer  "host_id", null: false
    t.string   "name",    null: false
    t.datetime "created", null: false
  end

  create_table "email_hosts", force: true do |t|
    t.integer  "client_id", null: false
    t.datetime "created",   null: false
  end

  create_table "email_users", force: true do |t|
    t.integer  "domain_id",                       null: false
    t.string   "username",                        null: false
    t.string   "password",                        null: false
    t.integer  "state",     limit: 2, default: 0, null: false
    t.datetime "created",                         null: false
  end

  create_table "firewall_devices", force: true do |t|
    t.string   "mac",                           null: false
    t.string   "name",                          null: false
    t.integer  "state",   limit: 2, default: 0, null: false
    t.integer  "host_id",                       null: false
    t.text     "details"
    t.integer  "ip",      limit: 2,             null: false
    t.datetime "created",                       null: false
  end

  create_table "firewall_hosts", force: true do |t|
    t.integer  "client_id", null: false
    t.text     "wan",       null: false
    t.text     "lan",       null: false
    t.text     "dmz",       null: false
    t.datetime "created",   null: false
  end

  create_table "firewall_inputs", force: true do |t|
    t.string   "name",                             null: false
    t.integer  "host_id",                          null: false
    t.integer  "s_port",                           null: false
    t.integer  "protocol", limit: 2, default: 0,   null: false
    t.string   "s_ip",               default: "*", null: false
    t.datetime "created",                          null: false
  end

  create_table "firewall_limits", force: true do |t|
    t.string   "name",     null: false
    t.integer  "host_id",  null: false
    t.integer  "max_up",   null: false
    t.integer  "max_down", null: false
    t.integer  "min_down", null: false
    t.integer  "min_up",   null: false
    t.datetime "created",  null: false
  end

  create_table "firewall_nats", force: true do |t|
    t.string   "name",                           null: false
    t.integer  "host_id",                        null: false
    t.integer  "s_port",                         null: false
    t.integer  "protocol", limit: 2, default: 0, null: false
    t.integer  "d_port",                         null: false
    t.integer  "d_ip",                           null: false
    t.datetime "created",                        null: false
  end

  create_table "firewall_output_devices", force: true do |t|
    t.integer  "output_id", null: false
    t.integer  "device_id", null: false
    t.datetime "created",   null: false
  end

  create_table "firewall_outputs", force: true do |t|
    t.integer  "host_id",  null: false
    t.string   "name",     null: false
    t.string   "keyword",  null: false
    t.string   "weekly",   null: false
    t.time     "startup",  null: false
    t.time     "shutdown", null: false
    t.datetime "created",  null: false
  end

  create_table "issues", force: true do |t|
    t.integer  "project_id",                       null: false
    t.string   "title",                            null: false
    t.text     "body",                             null: false
    t.integer  "user_id",                          null: false
    t.integer  "flag",       limit: 1, default: 0, null: false
    t.integer  "state",      limit: 2, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "monitor_hosts", force: true do |t|
    t.integer  "client_id", null: false
    t.datetime "created",   null: false
  end

  create_table "project_users", force: true do |t|
    t.integer  "project_id", default: 0, null: false
    t.integer  "member_id",  default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "projects", force: true do |t|
    t.string   "name",       null: false
    t.string   "title",      null: false
    t.text     "details",    null: false
    t.integer  "owner_id",   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "replies", force: true do |t|
    t.integer  "issue_id",   null: false
    t.integer  "user_id",    null: false
    t.text     "content",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "vpn_hosts", force: true do |t|
    t.integer  "client_id", null: false
    t.datetime "created",   null: false
  end

  create_table "vpn_users", force: true do |t|
    t.integer  "host_id",                        null: false
    t.string   "username",                       null: false
    t.string   "password",                       null: false
    t.integer  "state",    limit: 2, default: 0, null: false
    t.datetime "created",                        null: false
  end

end
