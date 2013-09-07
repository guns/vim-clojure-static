" Authors: Sung Pae <self@sungpae.com>

execute 'set rtp=' . expand('%:p:h:h:h') . ',$VIMRUNTIME'
filetype plugin on
syntax on
set synmaxcol=0
setfiletype clojure

if !exists('g:testing')
    let g:testing = 1
endif

function! s:syn_id_names()
    let names = []
    for lnum in range(1, line('$'))
        let f = 'synIDattr(synID(' . lnum . ', v:val, 0), "name")'
        call add(names, map(range(1, virtcol([lnum, '$']) - 1), f))
    endfor
    return names
endfunction

if g:testing
    " Changing the quotes will make this valid EDN
    call append(line('$'), tr(string(s:syn_id_names()), "'", '"')) | write | quitall!
endif
