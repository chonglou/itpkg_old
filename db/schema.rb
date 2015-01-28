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

ActiveRecord::Schema.define(version: 20150115055327) do

  create_table "certificates", force: :cascade do |t|
    t.text     "cert",               limit: 65535, null: false
    t.text     "csr",                limit: 65535
    t.text     "encrypted_key",      limit: 65535, null: false
    t.string   "encrypted_key_salt", limit: 255,   null: false
    t.string   "encrypted_key_iv",   limit: 255,   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "confirmations", force: :cascade do |t|
    t.string   "subject",              limit: 255,               null: false
    t.integer  "status",               limit: 4,     default: 0, null: false
    t.string   "token",                limit: 36,                null: false
    t.text     "encrypted_extra",      limit: 65535,             null: false
    t.string   "encrypted_extra_salt", limit: 255,               null: false
    t.string   "encrypted_extra_iv",   limit: 255,               null: false
    t.integer  "user_id",              limit: 4,     default: 0, null: false
    t.datetime "deadline",                                       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "confirmations", ["token"], name: "index_confirmations_on_token", unique: true, using: :btree

  create_table "contacts", force: :cascade do |t|
    t.integer  "user_id",    limit: 4,     null: false
    t.string   "logo",       limit: 255,   null: false
    t.string   "username",   limit: 255,   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "qq",         limit: 255
    t.string   "wechat",     limit: 255
    t.string   "phone",      limit: 255
    t.string   "fax",        limit: 255
    t.string   "address",    limit: 255
    t.string   "weibo",      limit: 255
    t.string   "linkedin",   limit: 255
    t.string   "facebook",   limit: 255
    t.string   "skype",      limit: 255
    t.text     "others",     limit: 65535
  end

  add_index "contacts", ["username"], name: "index_contacts_on_username", using: :btree

  create_table "dns_acls", force: :cascade do |t|
    t.string   "country",    limit: 255
    t.string   "region",     limit: 255, default: "*", null: false
    t.string   "city",       limit: 255, default: "*", null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_acls", ["city"], name: "index_dns_acls_on_city", using: :btree
  add_index "dns_acls", ["country", "region", "city"], name: "index_dns_acls_on_country_and_region_and_city", unique: true, using: :btree
  add_index "dns_acls", ["country"], name: "index_dns_acls_on_country", using: :btree
  add_index "dns_acls", ["region"], name: "index_dns_acls_on_region", using: :btree

  create_table "dns_counts", force: :cascade do |t|
    t.string   "zone",       limit: 255,             null: false
    t.integer  "count",      limit: 4,   default: 0, null: false
    t.integer  "code",       limit: 4,   default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_counts", ["zone"], name: "index_dns_counts_on_zone", using: :btree

  create_table "dns_records", force: :cascade do |t|
    t.string   "zone",        limit: 255,                   null: false
    t.string   "host",        limit: 255,   default: "@",   null: false
    t.string   "flag",        limit: 8,                     null: false
    t.text     "data",        limit: 65535
    t.integer  "ttl",         limit: 4,     default: 86400, null: false
    t.integer  "mx_priority", limit: 4
    t.integer  "refresh",     limit: 4
    t.integer  "retry",       limit: 4
    t.integer  "expire",      limit: 4
    t.integer  "minimum",     limit: 4
    t.integer  "serial",      limit: 8
    t.string   "resp_person", limit: 255
    t.string   "primary_ns",  limit: 255
    t.integer  "code",        limit: 4,     default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_records", ["flag"], name: "index_dns_records_on_flag", using: :btree
  add_index "dns_records", ["host", "zone", "flag", "code"], name: "index_dns_records_on_host_and_zone_and_flag_and_code", unique: true, using: :btree
  add_index "dns_records", ["host", "zone"], name: "index_dns_records_on_host_and_zone", using: :btree
  add_index "dns_records", ["host"], name: "index_dns_records_on_host", using: :btree
  add_index "dns_records", ["zone"], name: "index_dns_records_on_zone", using: :btree

  create_table "dns_xfrs", force: :cascade do |t|
    t.string   "zone",       limit: 255,             null: false
    t.string   "client",     limit: 255,             null: false
    t.integer  "code",       limit: 4,   default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_xfrs", ["client"], name: "index_dns_xfrs_on_client", using: :btree
  add_index "dns_xfrs", ["zone", "client"], name: "index_dns_xfrs_on_zone_and_client", unique: true, using: :btree
  add_index "dns_xfrs", ["zone"], name: "index_dns_xfrs_on_zone", using: :btree

  create_table "documents", force: :cascade do |t|
    t.integer  "project_id", limit: 4,                 null: false
    t.string   "name",       limit: 36,                null: false
    t.string   "ext",        limit: 5
    t.integer  "status",     limit: 2,     default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "avatar",     limit: 255,               null: false
    t.integer  "size",       limit: 4,     default: 0, null: false
    t.text     "details",    limit: 65535
  end

  create_table "email_aliases", force: :cascade do |t|
    t.integer  "domain_id",   limit: 4,   null: false
    t.string   "source",      limit: 128, null: false
    t.string   "destination", limit: 128, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "email_domains", force: :cascade do |t|
    t.string   "name",       limit: 255, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "email_domains", ["name"], name: "index_email_domains_on_name", unique: true, using: :btree

  create_table "email_users", force: :cascade do |t|
    t.integer  "domain_id",  limit: 4,   null: false
    t.string   "password",   limit: 255, null: false
    t.string   "email",      limit: 255, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "email_users", ["email"], name: "index_email_users_on_email", unique: true, using: :btree

  create_table "finance_scores", force: :cascade do |t|
    t.integer  "project_id", limit: 4,                              null: false
    t.integer  "user_id",    limit: 4,                              null: false
    t.decimal  "val",                    precision: 10, default: 0, null: false
    t.integer  "tag_id",     limit: 4,                              null: false
    t.string   "title",      limit: 255,                            null: false
    t.decimal  "balance",                precision: 10, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "finance_tags", force: :cascade do |t|
    t.integer  "project_id", limit: 4,  null: false
    t.string   "name",       limit: 32, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "histories", force: :cascade do |t|
    t.string   "url",        limit: 255,   null: false
    t.text     "data",       limit: 65535, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "histories", ["url"], name: "index_histories_on_url", using: :btree

  create_table "logging_nodes", force: :cascade do |t|
    t.integer  "flag",       limit: 2,   default: 0, null: false
    t.string   "name",       limit: 255,             null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "vip",        limit: 15,              null: false
  end

  add_index "logging_nodes", ["name"], name: "index_logging_nodes_on_name", using: :btree
  add_index "logging_nodes", ["vip"], name: "index_logging_nodes_on_vip", unique: true, using: :btree

  create_table "logging_searches", force: :cascade do |t|
    t.string   "name",       limit: 255,                null: false
    t.string   "message",    limit: 255, default: ".*", null: false
    t.string   "vip",        limit: 255, default: ".*", null: false
    t.string   "hostname",   limit: 255, default: ".*", null: false
    t.string   "tag",        limit: 255, default: ".*", null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "logs", force: :cascade do |t|
    t.integer  "user_id", limit: 4,   null: false
    t.string   "message", limit: 255, null: false
    t.datetime "created",             null: false
  end

  create_table "monitor_nodes", force: :cascade do |t|
    t.integer  "flag",               limit: 2,     default: 0,                     null: false
    t.string   "name",               limit: 255,                                   null: false
    t.text     "encrypted_cfg",      limit: 65535,                                 null: false
    t.string   "encrypted_cfg_salt", limit: 255,                                   null: false
    t.string   "encrypted_cfg_iv",   limit: 255,                                   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "vip",                limit: 15,                                    null: false
    t.integer  "status",             limit: 2,     default: 0,                     null: false
    t.integer  "space",              limit: 4,     default: 60,                    null: false
    t.datetime "next_run",                         default: '9999-12-31 23:59:59', null: false
  end

  add_index "monitor_nodes", ["name"], name: "index_monitor_nodes_on_name", using: :btree
  add_index "monitor_nodes", ["vip"], name: "index_monitor_nodes_on_vip", unique: true, using: :btree

  create_table "node_types", force: :cascade do |t|
    t.string   "name",       limit: 255,   null: false
    t.text     "dockerfile", limit: 65535, null: false
    t.integer  "creator_id", limit: 4,     null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "node_types", ["name"], name: "index_node_types_on_name", unique: true, using: :btree

  create_table "node_users", force: :cascade do |t|
    t.integer  "node_id",    limit: 4, null: false
    t.integer  "user_id",    limit: 4, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "node_users", ["node_id", "user_id"], name: "index_node_users_on_node_id_and_user_id", unique: true, using: :btree

  create_table "nodes", force: :cascade do |t|
    t.integer  "creator_id",          limit: 4,                 null: false
    t.integer  "node_type_id",        limit: 4,                 null: false
    t.integer  "name",                limit: 4,                 null: false
    t.text     "encrypted_keys",      limit: 65535,             null: false
    t.string   "encrypted_keys_salt", limit: 255,               null: false
    t.string   "encrypted_keys_iv",   limit: 255,               null: false
    t.text     "encrypted_cfg",       limit: 65535,             null: false
    t.string   "encrypted_cfg_salt",  limit: 255,               null: false
    t.string   "encrypted_cfg_iv",    limit: 255,               null: false
    t.integer  "status",              limit: 4,     default: 0, null: false
    t.string   "nid",                 limit: 255,               null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nodes", ["nid"], name: "index_nodes_on_nid", unique: true, using: :btree

  create_table "notices", force: :cascade do |t|
    t.integer  "user_id",    limit: 4,     null: false
    t.text     "body",       limit: 65535, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "nt_ports", force: :cascade do |t|
    t.integer  "node_type_id", limit: 4,                null: false
    t.integer  "s_port",       limit: 4,                null: false
    t.boolean  "tcp",          limit: 1, default: true, null: false
    t.integer  "t_port",       limit: 4,                null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_ports", ["node_type_id", "tcp", "t_port"], name: "index_nt_ports_on_node_type_id_and_tcp_and_t_port", unique: true, using: :btree

  create_table "nt_templates", force: :cascade do |t|
    t.string   "name",         limit: 255,                         null: false
    t.text     "body",         limit: 65535,                       null: false
    t.string   "mode",         limit: 3,     default: "400",       null: false
    t.string   "owner",        limit: 16,    default: "root:root", null: false
    t.integer  "node_type_id", limit: 4,                           null: false
    t.integer  "version",      limit: 4,     default: 0,           null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_templates", ["mode"], name: "index_nt_templates_on_mode", using: :btree
  add_index "nt_templates", ["name"], name: "index_nt_templates_on_name", using: :btree
  add_index "nt_templates", ["node_type_id", "name"], name: "index_nt_templates_on_node_type_id_and_name", unique: true, using: :btree
  add_index "nt_templates", ["owner"], name: "index_nt_templates_on_owner", using: :btree

  create_table "nt_vars", force: :cascade do |t|
    t.integer  "node_type_id", limit: 4,                 null: false
    t.string   "name",         limit: 255,               null: false
    t.integer  "flag",         limit: 4,     default: 0, null: false
    t.text     "def_v",        limit: 65535
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_vars", ["node_type_id", "name"], name: "index_nt_vars_on_node_type_id_and_name", unique: true, using: :btree

  create_table "nt_volumes", force: :cascade do |t|
    t.integer  "node_type_id", limit: 4,   null: false
    t.string   "s_path",       limit: 255, null: false
    t.string   "t_path",       limit: 255, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_volumes", ["node_type_id", "t_path"], name: "index_nt_volumes_on_node_type_id_and_t_path", unique: true, using: :btree

  create_table "projects", force: :cascade do |t|
    t.string   "name",       limit: 255,                  null: false
    t.text     "details",    limit: 65535
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "active",     limit: 1,     default: true
  end

  add_index "projects", ["name"], name: "index_projects_on_name", using: :btree

  create_table "repositories", force: :cascade do |t|
    t.string   "name",       limit: 16,                 null: false
    t.string   "title",      limit: 255,                null: false
    t.boolean  "enable",     limit: 1,   default: true, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "repositories", ["name"], name: "index_repositories_on_name", unique: true, using: :btree

  create_table "roles", force: :cascade do |t|
    t.string   "name",          limit: 255
    t.integer  "resource_id",   limit: 4
    t.string   "resource_type", limit: 255
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "roles", ["name", "resource_type", "resource_id"], name: "index_roles_on_name_and_resource_type_and_resource_id", using: :btree
  add_index "roles", ["name"], name: "index_roles_on_name", using: :btree

  create_table "rss_sites", force: :cascade do |t|
    t.string   "url",        limit: 255, null: false
    t.string   "title",      limit: 255
    t.string   "logo",       limit: 255
    t.datetime "last_sync"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "rss_sites", ["url"], name: "index_rss_sites_on_url", unique: true, using: :btree

  create_table "settings", force: :cascade do |t|
    t.string   "var",        limit: 255,   null: false
    t.text     "value",      limit: 65535
    t.integer  "thing_id",   limit: 4
    t.string   "thing_type", limit: 30
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "settings", ["thing_type", "thing_id", "var"], name: "index_settings_on_thing_type_and_thing_id_and_var", unique: true, using: :btree

  create_table "ssh_keys", force: :cascade do |t|
    t.integer  "user_id",                    limit: 4,     null: false
    t.text     "public_key",                 limit: 65535, null: false
    t.text     "encrypted_private_key",      limit: 65535, null: false
    t.string   "encrypted_private_key_salt", limit: 255,   null: false
    t.string   "encrypted_private_key_iv",   limit: 255,   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "ssh_keys", ["user_id"], name: "index_ssh_keys_on_user_id", unique: true, using: :btree

  create_table "stories", force: :cascade do |t|
    t.string   "title",            limit: 255,                  null: false
    t.integer  "project_id",       limit: 4,                    null: false
    t.integer  "point",            limit: 4,     default: 0,    null: false
    t.integer  "status",           limit: 1,     default: 0,    null: false
    t.text     "description",      limit: 65535
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "active",           limit: 1,     default: true
    t.datetime "plan_start_time"
    t.datetime "real_start_time"
    t.datetime "plan_finish_time"
    t.datetime "real_finish_time"
  end

  create_table "stories_story_tags", id: false, force: :cascade do |t|
    t.integer  "story_id",     limit: 4
    t.integer  "story_tag_id", limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "stories_story_types", id: false, force: :cascade do |t|
    t.integer  "story_id",      limit: 4
    t.integer  "story_type_id", limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_comments", force: :cascade do |t|
    t.text     "content",    limit: 65535
    t.integer  "user_id",    limit: 4
    t.integer  "story_id",   limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "active",     limit: 1,     default: true
  end

  add_index "story_comments", ["story_id"], name: "index_story_comments_on_story_id", using: :btree
  add_index "story_comments", ["user_id"], name: "index_story_comments_on_user_id", using: :btree

  create_table "story_followers", force: :cascade do |t|
    t.integer  "story_id",   limit: 4
    t.integer  "user_id",    limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_owners", force: :cascade do |t|
    t.integer  "story_id",   limit: 4
    t.integer  "user_id",    limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_tags", force: :cascade do |t|
    t.string   "name",       limit: 255
    t.string   "icon",       limit: 255
    t.integer  "project_id", limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_types", force: :cascade do |t|
    t.string   "name",       limit: 255
    t.string   "icon",       limit: 255
    t.integer  "project_id", limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "task_comments", force: :cascade do |t|
    t.text     "content",    limit: 65535
    t.integer  "user_id",    limit: 4
    t.integer  "task_id",    limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "active",     limit: 1,     default: true
  end

  add_index "task_comments", ["task_id"], name: "index_task_comments_on_task_id", using: :btree
  add_index "task_comments", ["user_id"], name: "index_task_comments_on_user_id", using: :btree

  create_table "tasks", force: :cascade do |t|
    t.integer  "story_id",         limit: 4,                    null: false
    t.text     "details",          limit: 65535,                null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "level",            limit: 2,     default: 0,    null: false
    t.boolean  "active",           limit: 1,     default: true
    t.datetime "plan_start_time"
    t.datetime "real_start_time"
    t.datetime "plan_finish_time"
    t.datetime "real_finish_time"
    t.integer  "point",            limit: 4
    t.integer  "status",           limit: 1
    t.integer  "priority",         limit: 4
  end

  create_table "translations", force: :cascade do |t|
    t.integer  "zh-CN",      limit: 4
    t.integer  "en",         limit: 4
    t.string   "flag",       limit: 8, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "translations", ["flag"], name: "index_translations_on_flag", using: :btree

  create_table "users", force: :cascade do |t|
    t.string   "email",                  limit: 255, default: "", null: false
    t.string   "encrypted_password",     limit: 255, default: "", null: false
    t.string   "reset_password_token",   limit: 255
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",          limit: 4,   default: 0,  null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip",     limit: 255
    t.string   "last_sign_in_ip",        limit: 255
    t.string   "confirmation_token",     limit: 255
    t.datetime "confirmed_at"
    t.datetime "confirmation_sent_at"
    t.string   "unconfirmed_email",      limit: 255
    t.integer  "failed_attempts",        limit: 4,   default: 0,  null: false
    t.string   "unlock_token",           limit: 255
    t.datetime "locked_at"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "label",                  limit: 255,              null: false
    t.string   "first_name",             limit: 255
    t.string   "last_name",              limit: 255
  end

  add_index "users", ["confirmation_token"], name: "index_users_on_confirmation_token", unique: true, using: :btree
  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["label"], name: "index_users_on_label", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree
  add_index "users", ["unlock_token"], name: "index_users_on_unlock_token", unique: true, using: :btree

  create_table "users_roles", id: false, force: :cascade do |t|
    t.integer "user_id", limit: 4
    t.integer "role_id", limit: 4
  end

  add_index "users_roles", ["user_id", "role_id"], name: "index_users_roles_on_user_id_and_role_id", using: :btree

  create_table "vpn_logs", force: :cascade do |t|
    t.string   "user",         limit: 255,               null: false
    t.string   "trusted_ip",   limit: 32
    t.string   "trusted_port", limit: 16
    t.string   "remote_ip",    limit: 32
    t.string   "remote_port",  limit: 16
    t.string   "message",      limit: 255
    t.datetime "start_time",                             null: false
    t.datetime "end_time",                               null: false
    t.float    "received",     limit: 24,  default: 0.0, null: false
    t.float    "sent",         limit: 24,  default: 0.0, null: false
  end

  add_index "vpn_logs", ["user"], name: "index_vpn_logs_on_user", using: :btree

  create_table "vpn_users", force: :cascade do |t|
    t.string   "name",       limit: 255,                 null: false
    t.string   "email",      limit: 255
    t.string   "phone",      limit: 255
    t.string   "password",   limit: 255,                 null: false
    t.boolean  "online",     limit: 1,   default: false
    t.boolean  "enable",     limit: 1,   default: false
    t.date     "start_date",                             null: false
    t.date     "end_date",                               null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "vpn_users", ["name"], name: "index_vpn_users_on_name", unique: true, using: :btree

  create_table "wikis", force: :cascade do |t|
    t.string   "title",      limit: 255,               null: false
    t.text     "body",       limit: 65535,             null: false
    t.integer  "status",     limit: 2,     default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "wikis", ["title"], name: "index_wikis_on_title", using: :btree

  create_table "wikis_users", id: false, force: :cascade do |t|
    t.integer  "wikis_id",   limit: 4
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
