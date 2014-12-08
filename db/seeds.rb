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

root = User.new label:'root', email:"root@#{ENV['ITPKG_DOMAIN']}", password:'changeme', confirmed_at:DateTime.now
root.skip_confirmation!
root.save!
Permission.create role: "user://#{root.id}", resource: 'SYSTEM', operation: 'root.id', start_date: Date.today.strftime, end_date: '9999-12-31'
Permission.create role: "user://#{root.id}", resource: 'SYSTEM', operation: 'ADMIN', start_date: Date.today.strftime, end_date: '9999-12-31'
n1 = Notice.create user_id: root.id, body: 'IT-PACKAGE System is online now!'
n2 = Notice.create user_id: root.id, body: 'IT-PACKAGE 系统正式上线!'
Translation.create flag: 'notice', en: n1.id, 'zh-CN' => n2.id

require 'itpkg/linux/certificate'
Certificate.create Linux::Certificate.root(10)

Dir.glob("#{Rails.root}/tools/seeds/*") do |t|
  nt = NodeType.new creator_id: root.id, name: File.basename(t)
  File.open("#{t}/Dockerfile", 'r') { |f| nt.dockerfile = f.read }
  nt.save

  File.open("#{t}/vars", 'r').each_line do |l|
    name, flag = l.chomp.split(':')
    NtVar.create node_type_id: nt.id, name:name, flag:flag.to_sym
  end
  File.open("#{t}/volumes", 'r').each_line do |l|
    p = l.chomp
    NtVolume.create node_type_id: nt.id, s_path: p, t_path: p
  end
  File.open("#{t}/ports", 'r').each_line do |l|
    port, protocol=l.chomp.split(':')
    NtPort.create node_type_id: nt.id, s_port: port, t_port: port, tcp: protocol=='tcp'
  end

  Dir.glob("#{t}/**/*") do |tt|
    next if ["#{t}/vars", "#{t}/Dockerfile", "#{t}/ports", "#{t}/volumes"].include?(tt) || File.directory?(tt)

    nt_t = NtTemplate.new name: tt[t.size..-1], mode: sprintf('%o', File.stat(tt).mode)[-3..-1], node_type_id: nt.id
    File.open(tt, 'r') { |f| nt_t.body = f.read }
    nt_t.save
  end

end


