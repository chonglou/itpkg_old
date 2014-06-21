itpkg 代理端
=========

#### 编译
	go get github.com/gorilla/websocket
	make build

#### 运行
	./itpkg-agent


#### vim设置
	set nobackup
		if exists("g:did_load_filetypes")
		filetype off
		filetype plugin indent off
	endif
	set runtimepath+=$GOROOT/misc/vim 
	filetype plugin indent on
	syntax on

