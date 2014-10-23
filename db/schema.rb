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

ActiveRecord::Schema.define(version: 20141023183853) do

  create_table "email_aliases", force: true do |t|
    t.integer  "domain_id",               null: false
    t.string   "source",      limit: 128, null: false
    t.string   "destination", limit: 128, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "email_domains", force: true do |t|
    t.string   "name",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "email_domains", ["name"], name: "index_email_domains_on_name", unique: true, using: :btree

  create_table "email_users", force: true do |t|
    t.integer  "domain_id",  null: false
    t.string   "passwd",     null: false
    t.string   "email",      null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "email_users", ["email"], name: "index_email_users_on_email", unique: true, using: :btree

  create_table "permissions", force: true do |t|
    t.string   "resource",   null: false
    t.string   "role",       null: false
    t.string   "operation",  null: false
    t.date     "startup",    null: false
    t.date     "shutdown",   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "permissions", ["operation"], name: "index_permissions_on_operation", using: :btree
  add_index "permissions", ["resource"], name: "index_permissions_on_resource", using: :btree
  add_index "permissions", ["role"], name: "index_permissions_on_role", using: :btree

  create_table "users", force: true do |t|
    t.string   "email",                  default: "", null: false
    t.string   "encrypted_password",     default: "", null: false
    t.string   "reset_password_token"
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",          default: 0,  null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip"
    t.string   "last_sign_in_ip"
    t.string   "confirmation_token"
    t.datetime "confirmed_at"
    t.datetime "confirmation_sent_at"
    t.string   "unconfirmed_email"
    t.integer  "failed_attempts",        default: 0,  null: false
    t.string   "unlock_token"
    t.datetime "locked_at"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "avatar"
  end

  add_index "users", ["confirmation_token"], name: "index_users_on_confirmation_token", unique: true, using: :btree
  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree
  add_index "users", ["unlock_token"], name: "index_users_on_unlock_token", unique: true, using: :btree

  create_table "vpn_logs", force: true do |t|
    t.string   "flag",     limit: 1,  default: "O", null: false
    t.string   "username", limit: 32,               null: false
    t.string   "message",                           null: false
    t.datetime "created",                           null: false
  end

  add_index "vpn_logs", ["username"], name: "index_vpn_logs_on_username", using: :btree

  create_table "vpn_users", force: true do |t|
    t.string   "name",       limit: 32,                 null: false
    t.string   "passwd",                                null: false
    t.string   "email",                                 null: false
    t.boolean  "enable",                default: false, null: false
    t.date     "start_date",                            null: false
    t.date     "end_date",                              null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "vpn_users", ["name"], name: "index_vpn_users_on_name", unique: true, using: :btree

end
