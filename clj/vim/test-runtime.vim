" Authors: Sung Pae <self@sungpae.com>

execute 'set rtp=' . expand('%:p:h:h:h') . ',$VIMRUNTIME'
filetype plugin indent on
syntax on
set synmaxcol=0 backspace=2
setfiletype clojure

function! EDN(value)
	" Changing the quotes may make this valid EDN
	return tr(string(a:value), "'", '"')
endfunction

function! ClojureSynIDNames()
	let names = []
	for lnum in range(1, line('$'))
		let f = 'synIDattr(synID(' . lnum . ', v:val, 0), "name")'
		call add(names, map(range(1, virtcol([lnum, '$']) - 1), f))
	endfor
	return EDN(names)
endfunction

function! TypeKeys(keys)
	execute 'normal! ' . a:keys
	write
endfunction

function! IndentFile()
	normal! gg=G
	write
endfunction

function! Time(n, expr)
	let start = reltime()
	let i = 0
	while i < a:n
		execute a:expr
		let i += 1
	endwhile
	return eval(reltimestr(reltime(start)))
endfunction

function! Benchmark(n, ...)
	let times = []
	for expr in a:000
		call add(times, Time(a:n, expr))
	endfor
	return EDN(times)
endfunction

" vim:sts=8:sw=8:ts=8:noet
