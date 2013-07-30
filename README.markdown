

                o8o
                '"'
    oooo    ooooooo ooo. .oo.  .oo.
     `88.  .8' `888 `888P"Y88bP"Y88b
      `88..8'   888  888   888   888
       `888'    888  888   888   888
        `8'    o888oo888o o888o o888o


             oooo           o8o
             `888           '"'
     .ooooo.  888  .ooooo. oooooooo  oooo oooo d8b .ooooo.
    d88' `"Y8 888 d88' `88b`888`888  `888 `888""8Pd88' `88b
    888       888 888   888 888 888   888  888    888ooo888
    888   .o8 888 888   888 888 888   888  888    888    .o
    `Y8bod8P'o888o`Y8bod8P' 888 `V88V"V8P'd888b   `Y8bod8P'
                            888
                        .o. 88P
                 .      `Y888P  .   o8o
               .o8            .o8   '"'
      .oooo.o.o888oo .oooo. .o888oooooo  .ooooo.
     d88(  "8  888  `P  )88b  888  `888 d88' `"Y8
     `"Y88b.   888   .oP"888  888   888 888
     o.  )88b  888 .d8(  888  888 . 888 888   .o8
     8""888P'  "888"`Y888""8o "888"o888o`Y8bod8P'



Meikel Brandmeyer's excellent Clojure runtime files, extracted for static
editing and use with alternate Clojure development plugins.

Rationale
=========

[VimClojure](http://www.vim.org/scripts/script.php?script_id=2501) consists of
a syntax script, indent script, filetype settings, limited static completions,
and a sophisticated synchronous REPL environment for interacting with JVM
Clojure processes.

While it is not necessary to use any of the interactive features of
VimClojure, the static runtime files are not standalone scripts and cannot
easily be extracted from the VimClojure support libraries. The side effects of
this coupling are:

* Hacking on the runtime files is more difficult.

* Installing the latest revisions from source is more complicated than
  tracking a single repository.

* Installing the whole VimClojure distribution for the runtime files is
  overkill. A smaller, self-contained set of files would be eligible for
  inclusion in Vim itself.

This is a shame since VimClojure's syntax and indent scripts are of very high
quality. This fork aims to address these problems.

Differences from VimClojure
===========================

* Only provides syntax, indent, and filetype settings for Clojure and
  ClojureScript files.

* All scripts are independent and do not depend on VimClojure library
  functions.

* Rainbow parentheses support is omitted in favor of more general and
  extensible third-party scripts. kien's
  [`rainbow_parentheses.vim`](https://github.com/kien/rainbow_parentheses.vim)
  is an excellent replacement.

* Basic insert mode completion is provided for special forms and public vars
  in `clojure.core`. It is bound to both the `'omnifunc'` and `'completefunc'`
  options, which can be invoked with the insert mode mappings `<C-X><C-O>` and
  `<C-X><C-U>` respectively.

  For more comprehensive completions, consider using an interactive
  development plugin listed below.

Interactive Clojure Development Plugins
=======================================

### [vim-fireplace](https://github.com/tpope/vim-fireplace)

New nREPL client by Tim Pope.

### [VimClojure](http://www.vim.org/scripts/script.php?script_id=2501)

The original interactive Clojure editing environment by Meikel Brandmeyer.
These runtime files are **incompatible** with the original VimClojure project
in several small ways, so be sure to uninstall vim-clojure-static when using
VimClojure.

Meikel has [announced](https://groups.google.com/forum/?fromgroups=#!topic/vimclojure/B-UU8qctd5A)
that the upcoming version of VimClojure will feature only the dynamic portion
of the project, and will be compatible with these static files.

### [slimv.vim](http://www.vim.org/scripts/script.php?script_id=2531)

SWANK client for Vim by Tamas Kovacs.

### [screen](http://www.vim.org/scripts/script.php?script_id=2711)

Send text to REPLs running in GNU Screen or tmux. Not Clojure specific.

By Eric Van Dewoestine.

### [vim-slime](https://github.com/jpalardy/vim-slime)

Like `screen` above, an asynchronous REPL plugin that uses GNU screen and
tmux. Not Clojure specific.

By Jonathan Palardy.

Installation
============

Vim version 7.3.803 and later ships with these runtime files, so you may
already have them installed!

If you are running an earlier version or you would like to keep up with
development, you can install this repository like a standard Vim plugin. If
you don't have a favorite method for installing plugins,
[Pathogen](https://github.com/tpope/vim-pathogen) or
[Vundle](https://github.com/gmarik/vundle) are recommended.

Please make sure that the following options are set in your .vimrc:

```vim
syntax on
filetype plugin indent on
```

Otherwise the filetype is not activated.

Indent Options
==============

Clojure indentation differs somewhat from traditional Lisps, due in part to
the use of square and curly brackets, and otherwise by community convention.
These conventions are not universally followed, so the Clojure indent script
offers a few configurable options, listed below.

If the current vim does not include searchpairpos(), the indent script falls
back to normal `'lisp'` indenting, and the following options are ignored.

### `g:clojure_maxlines`

Set maximum scan distance of searchpairpos(). Larger values trade performance
for correctness when dealing with very long forms. A value of 0 will scan
without limits.

```vim
" Default
let g:clojure_maxlines = 100
```

### `g:clojure_fuzzy_indent`, `g:clojure_fuzzy_indent_patterns`, and `g:clojure_fuzzy_indent_blacklist`

The `'lispwords'` option is a list of comma-separated words that mark special
forms whose subforms must be indented with two spaces.

For example:

```clojure
(defn bad []
      "Incorrect indentation")

(defn good []
  "Correct indentation")
```

If you would like to specify `'lispwords'` with a pattern instead, you can use
the fuzzy indent feature:

```vim
" Default
let g:clojure_fuzzy_indent = 1
let g:clojure_fuzzy_indent_patterns = ['^with', '^def', '^let']
let g:clojure_fuzzy_indent_blacklist = ['-fn$', '\v^with-%(meta|out-str|loading-context)$']

" Legacy comma-delimited string version; the list format above is
" recommended. Note that patterns are implicitly anchored with ^ and $.
let g:clojure_fuzzy_indent_patterns = 'with.*,def.*,let.*'
```

`g:clojure_fuzzy_indent_patterns` and `g:clojure_fuzzy_indent_blacklist` are
lists of patterns that will be matched against the unqualified symbol at the
head of a list. This means that a pattern like `"^foo"` will match all these
candidates: `foobar`, `my.ns/foobar`, and `#'foobar`.

Each candidate word is tested for special treatment in this order:

1. Return true if word is literally in `'lispwords'`
2. Return false if word matches a pattern in `g:clojure_fuzzy_indent_blacklist`
3. Return true if word matches a pattern in `g:clojure_fuzzy_indent_patterns`
4. Return false and indent normally otherwise

### `g:clojure_special_indent_words`

Some forms in Clojure are indented so that every subform is indented only
two spaces, regardless of `'lispwords'`. If you have a custom construct that
should be indented in this idiosyncratic fashion, you can add your symbols to
the default list below.

```vim
" Default
let g:clojure_special_indent_words = 'deftype,defrecord,reify,proxy,extend-type,extend-protocol,letfn'
```

### `g:clojure_align_multiline_strings`

Align subsequent lines in multiline strings to the column after the opening
quote, instead of the same column.

For example:

```clojure
(def default
  "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do
  eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
  enim ad minim veniam, quis nostrud exercitation ullamco laboris
  nisi ut aliquip ex ea commodo consequat.")

(def aligned
  "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do
   eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
   enim ad minim veniam, quis nostrud exercitation ullamco laboris
   nisi ut aliquip ex ea commodo consequat.")
```

This option is off by default.

```vim
" Default
let g:clojure_align_multiline_strings = 0
```

License and Acknowledgements
============================

Many thanks to [Meikel Brandmeyer](http://kotka.de/) for his excellent work on
making Vim a first class Clojure editor.

Thanks to [Tim Pope](https://github.com/tpope/) for advice in #vim.

`syntax/clojure.vim`

* Copyright 2007-2008 (c) Toralf Wittner <toralf.wittner@gmail.com>
* Copyright 2008-2012 (c) Meikel Brandmeyer <mb@kotka.de>

`ftdetect/clojure.vim`<br>
`ftplugin/clojure.vim`<br>
`indent/clojure.vim`

* Copyright 2008-2012 (c) Meikel Brandmeyer <mb@kotka.de>

Modified and relicensed under the Vim License for distribution with Vim:

* Copyright 2013 (c) Sung Pae <self@sungpae.com>

See LICENSE.txt for more information.

<!--
 vim:ft=markdown:et:tw=78:
-->
