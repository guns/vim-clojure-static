" Authors: Sung Pae <self@sungpae.com>

execute 'set rtp=' . expand('%:p:h:h:h') . ',$VIMRUNTIME'
filetype plugin on
syntax on
setfiletype clojure

function! s:append_syn_id_names()
    let names = []
    for lnum in range(1, line('$'))
        let f = 'synIDattr(synID(' . lnum . ', v:val, 0), "name")'
        call add(names, map(range(1, virtcol([lnum, '$']) - 1), f))
    endfor
    " Changing the quotes will make this valid EDN
    call append(line('$'), tr(string(names), "'", '"'))
endfunction

call s:append_syn_id_names() | write | quitall!
