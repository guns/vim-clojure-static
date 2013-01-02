" Vim filetype plugin file
" Language:     Clojure
" Author:       Meikel Brandmeyer <mb@kotka.de>

" This file forked from the VimClojure project:
" Maintainer:   Sung Pae <self@sungpae.com>
" URL:          https://github.com/guns/vim-clojure-static

" Only do this when not done yet for this buffer
if exists("b:did_ftplugin")
    finish
endif

let b:did_ftplugin = 1

let s:cpo_save = &cpo
set cpo&vim

let b:undo_ftplugin = 'setlocal define< formatoptions< comments< commentstring<'

" There will be false positives, but this is arguably better than missing the
" whole set of user-defined def* definitions.
setlocal define=\\v[(/]def(ault)@!\\S*

" Set 'formatoptions' to break comment lines but not other lines,
" and insert the comment leader when hitting <CR> or using "o".
setlocal formatoptions-=t formatoptions+=croql

" Set 'comments' to format dashed lists in comments.
setlocal comments=sO:;\ -,mO:;\ \ ,n:;
setlocal commentstring=;%s

" Take all directories of the CLOJURE_SOURCE_DIRS environment variable
" and add them to the path option.
if has("win32") || has("win64")
    let s:delim = ";"
else
    let s:delim = ":"
endif
for s:dir in split($CLOJURE_SOURCE_DIRS, s:delim)
    let s:path = fnameescape(s:dir . "/**")
    " Whitespace escaping for Windows
    let s:path = substitute(s:path, '\', '\\\\', 'g')
    let s:path = substitute(s:path, '\ ', '\\ ', 'g')
    execute "setlocal path+=" . s:path
endfor
if exists('$CLOJURE_SOURCE_DIRS')
    let b:undo_ftplugin .= '| setlocal path<'
endif

" When the matchit plugin is loaded, this makes the % command skip parens and
" braces in comments.
let b:match_words = &matchpairs
let b:match_skip = 's:comment\|string\|character'

" Win32 can filter files in the browse dialog
if has("gui_win32") && !exists("b:browsefilter")
    let b:browsefilter = "Clojure Source Files (*.clj)\t*.clj\n" .
                       \ "ClojureScript Source Files (*.cljs)\t*.cljs\n" .
                       \ "Java Source Files (*.java)\t*.java\n" .
                       \ "All Files (*.*)\t*.*\n"
endif

let &cpo = s:cpo_save

" vim:sts=4 sw=4 et:
