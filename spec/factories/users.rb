FactoryGirl.define do
  factory :admin, class: User do
    id 1
    email 'admin1@aaa.com'
    label 'admin1'
    password '111111111111'
  end
  factory :manager1, class: User do
    id 11
    email 'manager1@aaa.com'
    label 'manager1'
    password '111111111111'
  end
  factory :manager2, class: User do
    id 12
    email 'manager2@aaa.com'
    label 'manager2'
    password '111111111111'
  end
  factory :employee1, class: User do
    id 21
    email 'employee1@aaa.com'
    label 'employee1'
    password '111111111111'
  end
  factory :employee2, class: User do
    id 22
    email 'employee2@aaa.com'
    label 'employee2'
    password '111111111111'
  end
end