IT-PACKAGE
=====

### Usage

#### Setup
    rake itpkg:env

#### Background Worker
    sidekiq -d # start new worker
    ps -ef | grep sidekiq | grep -v grep | awk '{print $2}' | xargs kill -USR1 # stop all workers
