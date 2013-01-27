" Vim filetype plugin file
" Language:     Clojure
" Author:       Meikel Brandmeyer <mb@kotka.de>
"
" Maintainer:   Sung Pae <self@sungpae.com>
" URL:          https://github.com/guns/vim-clojure-static

" Only do this when not done yet for this buffer
if exists("b:did_ftplugin")
    finish
endif
let b:did_ftplugin = 1

let s:cpo_save = &cpo
set cpo&vim

let b:undo_ftplugin = 'setlocal define< formatoptions< comments< commentstring< complete<'

" There will be false positives, but this is better than missing the whole set
" of user-defined def* definitions.
setlocal define=\\v[(/]def(ault)@!\\S*

" Remove 't' from 'formatoptions' to avoid auto-wrapping code. The '+=croql'
" is standard ftplugin boilerplate, although it is arguably intrusive.
setlocal formatoptions-=t formatoptions+=croql

" Lisp comments are routinely nested (e.g. ;;; SECTION HEADING)
setlocal comments=n:;
setlocal commentstring=;\ %s

function! s:escape_path_for_option(path)
    let path = fnameescape(a:path)
    " Whitespace escaping for Windows
    let path = substitute(path, '\', '\\\\', 'g')
    let path = substitute(path, '\ ', '\\ ', 'g')
    return path
endfunction

" Provide insert mode completions for special forms and clojure.core
for s:compfile in split(globpath(&rtp, "ftplugin/clojure/*.txt"), '\n')
    execute 'setlocal complete+=k' . s:escape_path_for_option(s:compfile)
endfor

" Take all directories of the CLOJURE_SOURCE_DIRS environment variable
" and add them to the path option.
"
" This is a legacy option for VimClojure users.
if exists('$CLOJURE_SOURCE_DIRS')
    for s:dir in split($CLOJURE_SOURCE_DIRS, (has("win32") || has("win64")) ? ';' : ':')
        execute "setlocal path+=" . s:escape_path_for_option(s:dir . "/**")
    endfor
    let b:undo_ftplugin .= ' | setlocal path<'
endif

" Skip brackets in ignored syntax regions when using the % command
if exists('loaded_matchit')
    let b:match_words = &matchpairs
    let b:match_skip = 's:comment\|string\|regex\|character'
    let b:undo_ftplugin .= ' | unlet! b:match_words b:match_skip'
endif

" Win32 can filter files in the browse dialog
if has("gui_win32") && !exists("b:browsefilter")
    let b:browsefilter = "Clojure Source Files (*.clj)\t*.clj\n" .
                       \ "ClojureScript Source Files (*.cljs)\t*.cljs\n" .
                       \ "Java Source Files (*.java)\t*.java\n" .
                       \ "All Files (*.*)\t*.*\n"
    let b:undo_ftplugin .= ' | unlet! b:browsefilter'
endif

delfunction s:escape_path_for_option

let &cpo = s:cpo_save

" vim:sts=4 sw=4 et:
