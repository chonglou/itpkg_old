FactoryGirl.define do
  factory :admin, class: User do
    id 1
    email 'admin@aaa.com'
    label 'u1'
  end
  factory :manager1, class: User do
    id 11
    email 'manager1@aaa.com'
    label 'm1'
  end
  factory :manager2, class: User do
    id 12
    email 'manager2@aaa.com'
    label 'm2'
  end
  factory :employee1, class: User do
    id 21
    email 'employee1@aaa.com'
    label 'e1'
  end
  factory :employee2, class: User do
    id 22
    email 'employee2@aaa.com'
    label 'e2'
  end
end