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

ActiveRecord::Schema.define(version: 20140525173441) do

  create_table "brahma_bodhi_attachments", force: true do |t|
    t.integer  "user_id",                            null: false
    t.integer  "size",                               null: false
    t.string   "file_name",                          null: false
    t.string   "content_type",                       null: false
    t.string   "original_filename",                  null: false
    t.binary   "content",           limit: 16777215, null: false
    t.datetime "last_edit",                          null: false
    t.datetime "created",                            null: false
  end

  add_index "brahma_bodhi_attachments", ["file_name"], name: "index_brahma_bodhi_attachments_on_file_name", unique: true, using: :btree

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
    t.string   "token",                            null: false
    t.integer  "flag",       limit: 1, default: 0, null: false
    t.integer  "state",      limit: 1, default: 0, null: false
    t.string   "username",                         null: false
    t.binary   "contact"
    t.datetime "last_login"
    t.datetime "created",                          null: false
  end

  add_index "brahma_bodhi_users", ["open_id"], name: "index_brahma_bodhi_users_on_open_id", unique: true, using: :btree
  add_index "brahma_bodhi_users", ["token"], name: "index_brahma_bodhi_users_on_token", unique: true, using: :btree

end
