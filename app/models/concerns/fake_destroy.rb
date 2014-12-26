module FakeDestroy
  extend ActiveSupport::Concern

  def destroy
    self.update(active: false)
  end
end
