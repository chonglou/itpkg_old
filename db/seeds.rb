require 'itpkg/services/site'

Itpkg::SiteService.version!
Setting.init = Time.now
Setting.git = {
    host: '127.0.0.1',
    username: 'git',
    port: 22,
    public_key: "#{ENV['HOME']}/.ssh/id_rsa.pub",
    private_key: "#{ENV['HOME']}/.ssh/id_rsa",
    email: "git@#{ENV['ITPKG_DOMAIN']}"
}

ROOT=1
Permission.create role: "user://#{ROOT}", resource: 'SYSTEM', operation: 'ROOT', start_date: Date.today.strftime, end_date: '9999-12-31'
Permission.create role: "user://#{ROOT}", resource: 'SYSTEM', operation: 'ADMIN', start_date: Date.today.strftime, end_date: '9999-12-31'
n1 = Notice.create user_id: ROOT, body: 'IT-PACKAGE System is online now!'
n2 = Notice.create user_id: ROOT, body: 'IT-PACKAGE 系统正式上线!'
Translation.create flag: 'notice', en: n1.id, 'zh-CN' => n2.id

require 'itpkg/linux/certificate'
Certificate.create Linux::Certificate.root(10)

Dir.glob("#{Rails.root}/tools/seeds/*") do |t|
  nt = NodeType.new creator_id: ROOT, name: File.basename(t)
  File.open("#{t}/Dockerfile", 'r') { |f| nt.dockerfile = f.read }
  nt.save
  File.open("#{t}/vars", 'r').each_line { |l| NtVar.create node_type_id: nt.id, name: l }

  Dir.glob("#{t}/**/*") do |tt|
    next if ["#{t}/vars", "#{t}/Dockerfile"].include?(tt) || File.directory?(tt)

    nt_t = NtTemplate.new name: tt[t.size..-1], mode: sprintf('%o', File.stat(tt).mode)[-3..-1], node_type_id: nt.id
    File.open(tt, 'r') { |f| nt_t.body = f.read }
    nt_t.save
  end

end


