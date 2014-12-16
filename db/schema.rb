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

ActiveRecord::Schema.define(version: 20141216002924) do

  create_table "certificates", force: true do |t|
    t.text     "cert",               null: false
    t.text     "csr"
    t.text     "encrypted_key",      null: false
    t.string   "encrypted_key_salt", null: false
    t.string   "encrypted_key_iv",   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "comments", force: true do |t|
    t.integer  "user_id",    null: false
    t.integer  "project_id"
    t.integer  "task_id"
    t.integer  "story_id"
    t.text     "content",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "confirmations", force: true do |t|
    t.string   "subject",                                     null: false
    t.integer  "status",                          default: 0, null: false
    t.string   "token",                limit: 36,             null: false
    t.text     "encrypted_extra",                             null: false
    t.string   "encrypted_extra_salt",                        null: false
    t.string   "encrypted_extra_iv",                          null: false
    t.integer  "user_id",                         default: 0, null: false
    t.datetime "deadline",                                    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "confirmations", ["token"], name: "index_confirmations_on_token", unique: true, using: :btree

  create_table "contacts", force: true do |t|
    t.integer  "user_id",    null: false
    t.string   "logo",       null: false
    t.string   "username",   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "qq"
    t.string   "wechat"
    t.string   "phone"
    t.string   "fax"
    t.string   "address"
    t.string   "weibo"
    t.string   "linkedin"
    t.string   "facebook"
    t.string   "skype"
    t.text     "others"
  end

  add_index "contacts", ["username"], name: "index_contacts_on_username", using: :btree

  create_table "dns_acls", force: true do |t|
    t.string   "country"
    t.string   "region",     default: "*", null: false
    t.string   "city",       default: "*", null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_acls", ["city"], name: "index_dns_acls_on_city", using: :btree
  add_index "dns_acls", ["country", "region", "city"], name: "index_dns_acls_on_country_and_region_and_city", unique: true, using: :btree
  add_index "dns_acls", ["country"], name: "index_dns_acls_on_country", using: :btree
  add_index "dns_acls", ["region"], name: "index_dns_acls_on_region", using: :btree

  create_table "dns_counts", force: true do |t|
    t.string   "zone",                   null: false
    t.integer  "count",      default: 0, null: false
    t.integer  "code",       default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_counts", ["zone"], name: "index_dns_counts_on_zone", using: :btree

  create_table "dns_records", force: true do |t|
    t.string   "zone",                                  null: false
    t.string   "host",                  default: "@",   null: false
    t.string   "flag",        limit: 8,                 null: false
    t.text     "data"
    t.integer  "ttl",                   default: 86400, null: false
    t.integer  "mx_priority"
    t.integer  "refresh"
    t.integer  "retry"
    t.integer  "expire"
    t.integer  "minimum"
    t.integer  "serial",      limit: 8
    t.string   "resp_person"
    t.string   "primary_ns"
    t.integer  "code",                  default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_records", ["flag"], name: "index_dns_records_on_flag", using: :btree
  add_index "dns_records", ["host", "zone", "flag", "code"], name: "index_dns_records_on_host_and_zone_and_flag_and_code", unique: true, using: :btree
  add_index "dns_records", ["host", "zone"], name: "index_dns_records_on_host_and_zone", using: :btree
  add_index "dns_records", ["host"], name: "index_dns_records_on_host", using: :btree
  add_index "dns_records", ["zone"], name: "index_dns_records_on_zone", using: :btree

  create_table "dns_xfrs", force: true do |t|
    t.string   "zone",                   null: false
    t.string   "client",                 null: false
    t.integer  "code",       default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "dns_xfrs", ["client"], name: "index_dns_xfrs_on_client", using: :btree
  add_index "dns_xfrs", ["zone", "client"], name: "index_dns_xfrs_on_zone_and_client", unique: true, using: :btree
  add_index "dns_xfrs", ["zone"], name: "index_dns_xfrs_on_zone", using: :btree

  create_table "documents", force: true do |t|
    t.integer  "project_id",                        null: false
    t.string   "name",       limit: 36,             null: false
    t.string   "ext",        limit: 5
    t.integer  "status",     limit: 2,  default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "avatar",                            null: false
    t.integer  "size",                  default: 0, null: false
    t.text     "details"
  end

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
    t.string   "password",   null: false
    t.string   "email",      null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "email_users", ["email"], name: "index_email_users_on_email", unique: true, using: :btree

  create_table "finance_scores", force: true do |t|
    t.integer  "project_id",                                      null: false
    t.integer  "user_id",                                         null: false
    t.decimal  "val",        precision: 10, scale: 0, default: 0, null: false
    t.integer  "tag_id",                                          null: false
    t.string   "title",                                           null: false
    t.decimal  "balance",    precision: 10, scale: 0, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "finance_tags", force: true do |t|
    t.integer  "project_id",            null: false
    t.string   "name",       limit: 32, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "histories", force: true do |t|
    t.string   "url",        null: false
    t.text     "data",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "histories", ["url"], name: "index_histories_on_url", using: :btree

  create_table "logging_nodes", force: true do |t|
    t.integer  "flag",       limit: 2,  default: 0, null: false
    t.string   "name",                              null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "vip",        limit: 15,             null: false
  end

  add_index "logging_nodes", ["name"], name: "index_logging_nodes_on_name", using: :btree
  add_index "logging_nodes", ["vip"], name: "index_logging_nodes_on_vip", unique: true, using: :btree

  create_table "logging_searches", force: true do |t|
    t.string   "name",                      null: false
    t.string   "message",    default: ".*", null: false
    t.string   "vip",        default: ".*", null: false
    t.string   "hostname",   default: ".*", null: false
    t.string   "tag",        default: ".*", null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "logs", force: true do |t|
    t.integer  "user_id",    null: false
    t.string   "message",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "monitor_nodes", force: true do |t|
    t.integer  "flag",               limit: 2,  default: 0, null: false
    t.string   "name",                                      null: false
    t.text     "encrypted_cfg",                             null: false
    t.string   "encrypted_cfg_salt",                        null: false
    t.string   "encrypted_cfg_iv",                          null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "vip",                limit: 15,             null: false
  end

  add_index "monitor_nodes", ["name"], name: "index_monitor_nodes_on_name", using: :btree
  add_index "monitor_nodes", ["vip"], name: "index_monitor_nodes_on_vip", unique: true, using: :btree

  create_table "node_types", force: true do |t|
    t.string   "name",       null: false
    t.text     "dockerfile", null: false
    t.integer  "creator_id", null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "node_types", ["name"], name: "index_node_types_on_name", unique: true, using: :btree

  create_table "node_users", force: true do |t|
    t.integer  "node_id",    null: false
    t.integer  "user_id",    null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "node_users", ["node_id", "user_id"], name: "index_node_users_on_node_id_and_user_id", unique: true, using: :btree

  create_table "nodes", force: true do |t|
    t.integer  "creator_id",                      null: false
    t.integer  "node_type_id",                    null: false
    t.integer  "name",                            null: false
    t.text     "encrypted_keys",                  null: false
    t.string   "encrypted_keys_salt",             null: false
    t.string   "encrypted_keys_iv",               null: false
    t.text     "encrypted_cfg",                   null: false
    t.string   "encrypted_cfg_salt",              null: false
    t.string   "encrypted_cfg_iv",                null: false
    t.integer  "status",              default: 0, null: false
    t.string   "nid",                             null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nodes", ["nid"], name: "index_nodes_on_nid", unique: true, using: :btree

  create_table "notices", force: true do |t|
    t.integer  "user_id",    null: false
    t.text     "body",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "nt_ports", force: true do |t|
    t.integer  "node_type_id",                null: false
    t.integer  "s_port",                      null: false
    t.boolean  "tcp",          default: true, null: false
    t.integer  "t_port",                      null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_ports", ["node_type_id", "tcp", "t_port"], name: "index_nt_ports_on_node_type_id_and_tcp_and_t_port", unique: true, using: :btree

  create_table "nt_templates", force: true do |t|
    t.string   "name",                                          null: false
    t.text     "body",                                          null: false
    t.string   "mode",         limit: 3,  default: "400",       null: false
    t.string   "owner",        limit: 16, default: "root:root", null: false
    t.integer  "node_type_id",                                  null: false
    t.integer  "version",                 default: 0,           null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_templates", ["mode"], name: "index_nt_templates_on_mode", using: :btree
  add_index "nt_templates", ["name"], name: "index_nt_templates_on_name", using: :btree
  add_index "nt_templates", ["node_type_id", "name"], name: "index_nt_templates_on_node_type_id_and_name", unique: true, using: :btree
  add_index "nt_templates", ["owner"], name: "index_nt_templates_on_owner", using: :btree

  create_table "nt_vars", force: true do |t|
    t.integer  "node_type_id",             null: false
    t.string   "name",                     null: false
    t.integer  "flag",         default: 0, null: false
    t.text     "def_v"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_vars", ["node_type_id", "name"], name: "index_nt_vars_on_node_type_id_and_name", unique: true, using: :btree

  create_table "nt_volumes", force: true do |t|
    t.integer  "node_type_id", null: false
    t.string   "s_path",       null: false
    t.string   "t_path",       null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "nt_volumes", ["node_type_id", "t_path"], name: "index_nt_volumes_on_node_type_id_and_t_path", unique: true, using: :btree

  create_table "projects", force: true do |t|
    t.string   "name",       null: false
    t.text     "details"
    t.integer  "creator_id", null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "projects", ["name"], name: "index_projects_on_name", using: :btree

  create_table "projects_users", id: false, force: true do |t|
    t.integer  "project_id"
    t.integer  "user_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "repositories", force: true do |t|
    t.string   "name",       limit: 16,                null: false
    t.string   "title",                                null: false
    t.boolean  "enable",                default: true, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "repositories", ["name"], name: "index_repositories_on_name", unique: true, using: :btree

  create_table "roles", force: true do |t|
    t.string   "name"
    t.integer  "resource_id"
    t.string   "resource_type"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "roles", ["name", "resource_type", "resource_id"], name: "index_roles_on_name_and_resource_type_and_resource_id", using: :btree
  add_index "roles", ["name"], name: "index_roles_on_name", using: :btree

  create_table "rss_sites", force: true do |t|
    t.string   "url",        null: false
    t.string   "title"
    t.string   "logo"
    t.datetime "last_sync"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "rss_sites", ["url"], name: "index_rss_sites_on_url", unique: true, using: :btree

  create_table "settings", force: true do |t|
    t.string   "var",                   null: false
    t.text     "value"
    t.integer  "thing_id"
    t.string   "thing_type", limit: 30
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "settings", ["thing_type", "thing_id", "var"], name: "index_settings_on_thing_type_and_thing_id_and_var", unique: true, using: :btree

  create_table "ssh_keys", force: true do |t|
    t.integer  "user_id",                    null: false
    t.text     "public_key",                 null: false
    t.text     "encrypted_private_key",      null: false
    t.string   "encrypted_private_key_salt", null: false
    t.string   "encrypted_private_key_iv",   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "ssh_keys", ["user_id"], name: "index_ssh_keys_on_user_id", unique: true, using: :btree

  create_table "stories", force: true do |t|
    t.string   "title",                              null: false
    t.integer  "project_id",                         null: false
    t.integer  "point",                  default: 0, null: false
    t.integer  "requester_id",                       null: false
    t.integer  "status",       limit: 1, default: 0, null: false
    t.text     "description"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "stories_story_tags", id: false, force: true do |t|
    t.integer  "story_id"
    t.integer  "story_tag_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "stories_story_types", id: false, force: true do |t|
    t.integer  "story_id"
    t.integer  "story_type_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_followers", force: true do |t|
    t.integer  "story_id"
    t.integer  "user_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_owners", force: true do |t|
    t.integer  "story_id"
    t.integer  "user_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_tags", force: true do |t|
    t.string   "name"
    t.string   "icon"
    t.integer  "project_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "story_types", force: true do |t|
    t.string   "name"
    t.string   "icon"
    t.integer  "project_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "tasks", force: true do |t|
    t.integer  "story_id",                         null: false
    t.string   "details",                          null: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "level",      limit: 2, default: 0, null: false
  end

  create_table "translations", force: true do |t|
    t.integer  "zh-CN"
    t.integer  "en"
    t.string   "flag",       limit: 8, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "translations", ["flag"], name: "index_translations_on_flag", using: :btree

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
    t.string   "label",                               null: false
  end

  add_index "users", ["confirmation_token"], name: "index_users_on_confirmation_token", unique: true, using: :btree
  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["label"], name: "index_users_on_label", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree
  add_index "users", ["unlock_token"], name: "index_users_on_unlock_token", unique: true, using: :btree

  create_table "users_roles", id: false, force: true do |t|
    t.integer "user_id"
    t.integer "role_id"
  end

  add_index "users_roles", ["user_id", "role_id"], name: "index_users_roles_on_user_id_and_role_id", using: :btree

  create_table "vpn_logs", force: true do |t|
    t.string   "user",                                  null: false
    t.string   "trusted_ip",   limit: 32
    t.string   "trusted_port", limit: 16
    t.string   "remote_ip",    limit: 32
    t.string   "remote_port",  limit: 16
    t.string   "message"
    t.datetime "start_time",                            null: false
    t.datetime "end_time",                              null: false
    t.float    "received",     limit: 24, default: 0.0, null: false
    t.float    "sent",         limit: 24, default: 0.0, null: false
  end

  add_index "vpn_logs", ["user"], name: "index_vpn_logs_on_user", using: :btree

  create_table "vpn_users", force: true do |t|
    t.string   "name",                       null: false
    t.string   "email"
    t.string   "phone"
    t.string   "password",                   null: false
    t.boolean  "online",     default: false
    t.boolean  "enable",     default: false
    t.date     "start_date",                 null: false
    t.date     "end_date",                   null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "vpn_users", ["name"], name: "index_vpn_users_on_name", unique: true, using: :btree

  create_table "wikis", force: true do |t|
    t.string   "title",                            null: false
    t.text     "body",                             null: false
    t.integer  "status",     limit: 2, default: 0, null: false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "wikis", ["title"], name: "index_wikis_on_title", using: :btree

  create_table "wikis_users", id: false, force: true do |t|
    t.integer  "wikis_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
