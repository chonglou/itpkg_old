require 'erb'

class Template < ActiveRecord::Base

  validates :flag, :name, :body, :owner, presence: true
  validates :name, uniqueness: {scope: :flag}

  def to_sh
    <<-SHELL
#!/bin/sh
[ -d #{File.dirname self.name} ] || mkdir #{File.dirname self.name}
cat > #{self.name} <<'EOF'
#{self.body.gsub(/\r\n/, "\n")}
EOF
chmod #{self.mode} #{self.name}
chown #{self.owner} #{self.name}
    SHELL
  end
end
