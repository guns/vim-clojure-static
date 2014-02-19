" Authors: Sung Pae <self@sungpae.com>

execute 'set rtp=' . expand('%:p:h:h:h') . ',$VIMRUNTIME'
filetype plugin on
syntax on
set synmaxcol=0
setfiletype clojure

function! ClojureSynIDNames()
    let names = []
    for lnum in range(1, line('$'))
        let f = 'synIDattr(synID(' . lnum . ', v:val, 0), "name")'
        call add(names, map(range(1, virtcol([lnum, '$']) - 1), f))
    endfor
    " Changing the quotes makes this valid EDN
    return tr(string(names), "'", '"')
endfunction
