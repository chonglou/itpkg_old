class NtTemplate < ActiveRecord::Base

  validates :node_type_id, :name, :body, :owner, presence: true
  validates :name, uniqueness: {scope: :node_type_id}

  belongs_to :node_type

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
