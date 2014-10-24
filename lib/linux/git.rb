require 'rugged'

module Linux
  class Git
    attr_reader :root

    def initialize(name)
      @root = "#{Rails.root}/tmp/storage/repos/#{name}.git"
    end

    def exist?
      Dir.exist? @root
    end

    def init
      Rugged::Repository.init_at @root, :bare
    end

    def open
      @repo = Rugged::Repository.new @root
    end

    def branches
      @repo.branches.each_name().sort
    end


    #{name, email, time} message
    def log(branch)
      walker = Rugged::Walker.new(@repo)
      walker.sorting(Rugged::SORT_TOPO | Rugged::SORT_REVERSE)
      walker.push @repo.branches[branch].target_id
      walker.each { |c| yield c.author, c.message }
      walker.reset
    end
  end
end