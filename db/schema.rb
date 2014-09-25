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

ActiveRecord::Schema.define(version: 20140925193327) do

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
    t.text     "content",             null: false
    t.datetime "last_edit",           null: false
    t.datetime "created",             null: false
    t.integer  "tid",                 null: false
    t.string   "lang",      limit: 5, null: false
  end

  add_index "brahma_bodhi_notices", ["lang"], name: "index_brahma_bodhi_notices_on_lang", using: :btree

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
    t.datetime "created",             null: false
    t.integer  "version", default: 0, null: false
    t.string   "lang"
  end

  add_index "brahma_bodhi_settings", ["key"], name: "index_brahma_bodhi_settings_on_key", using: :btree

  create_table "brahma_bodhi_translations", force: true do |t|
    t.integer  "zh-CN"
    t.integer  "en"
    t.string   "flag",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "brahma_bodhi_translations", ["flag"], name: "index_brahma_bodhi_translations_on_flag", using: :btree

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

  create_table "monitor_groups", force: true do |t|
    t.string   "name",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "monitor_groups", ["name"], name: "index_monitor_groups_on_name", unique: true, using: :btree

  create_table "monitor_nodes", force: true do |t|
    t.integer  "group_id",                         null: false
    t.string   "title",                            null: false
    t.text     "profile",                          null: false
    t.integer  "flag",       limit: 2, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "ops_groups", force: true do |t|
    t.string   "name",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "ops_groups", ["name"], name: "index_ops_groups_on_name", using: :btree

  create_table "ops_logs", force: true do |t|
    t.integer  "node_id",    null: false
    t.integer  "user_id",    null: false
    t.string   "message",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "ops_nodes", force: true do |t|
    t.integer  "group_id",                         null: false
    t.string   "title",                            null: false
    t.text     "profile",                          null: false
    t.integer  "flag",       limit: 2, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "team_activities", force: true do |t|
    t.integer  "issue_id",   null: false
    t.integer  "user_id",    null: false
    t.text     "message",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "team_documents", force: true do |t|
    t.integer  "project_id", null: false
    t.integer  "user_id",    null: false
    t.string   "name",       null: false
    t.string   "ext",        null: false
    t.string   "title",      null: false
    t.integer  "size",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "team_documents", ["name"], name: "index_team_documents_on_name", unique: true, using: :btree

  create_table "team_issues", force: true do |t|
    t.string   "name",                             null: false
    t.text     "details",                          null: false
    t.integer  "project_id",                       null: false
    t.integer  "creator_id",                       null: false
    t.integer  "worker_id",                        null: false
    t.integer  "point",                            null: false
    t.integer  "value",                            null: false
    t.datetime "start_up",                         null: false
    t.datetime "shut_down",                        null: false
    t.integer  "status",     limit: 2, default: 0, null: false
    t.integer  "flag",       limit: 2, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "team_project_members", force: true do |t|
    t.integer  "project_id",                       null: false
    t.integer  "member_id",                        null: false
    t.integer  "flag",       limit: 2, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "team_projects", force: true do |t|
    t.integer  "manager_id",           null: false
    t.string   "name",                 null: false
    t.string   "title",                null: false
    t.text     "detail",               null: false
    t.integer  "status",     limit: 2, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "team_projects", ["name"], name: "index_team_projects_on_name", unique: true, using: :btree

  create_table "wikis", force: true do |t|
    t.string   "title",                  null: false
    t.text     "body",                   null: false
    t.integer  "version",    default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
