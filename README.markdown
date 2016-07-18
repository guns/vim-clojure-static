

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



Meikel Brandmeyer's excellent Clojure runtime files, extracted from the
[VimClojure](http://www.vim.org/scripts/script.php?script_id=2501) project for
use with alternate Clojure REPL plugins.

These files ship with Vim versions 7.3.803 and later, and are periodically
merged into the official Vim repository.

Installation
============

If you are running an old version of Vim or if you would like to keep up with
development, you can install this repository like a standard Vim plugin.

If you are unfamiliar with this process, refer to
the [Pathogen](https://github.com/tpope/vim-pathogen) project.

Please make sure that the following options are set in your vimrc to enable
all features:

```vim
syntax on
filetype plugin indent on
```

Features
========

* [Augmentable](#syntax-options) syntax highlighting for Clojure and
  ClojureScript buffers.

* [Configurable](#indent-options) Clojure-specific indentation.

* Basic insert mode completion for special forms and public vars in
  `clojure.core` and `clojure.test`.

  This is bound to both the `'omnifunc'` and `'completefunc'` options, which
  can be invoked with the insert mode mappings `<C-X><C-O>` and `<C-X><C-U>`
  respectively.

If you install this project as a plugin, `*.edn` files are recognized as a
Clojure filetype, overriding the built-in declaration as `edif`.

Third Party Extensions
======================

* Rainbow Parentheses

  kien's [`rainbow_parentheses.vim`](https://github.com/kien/rainbow_parentheses.vim)
  is highly recommended.

* Extended Syntax Highlighting

  [`vim-clojure-highlight`](https://github.com/guns/vim-clojure-highlight) is
  a fireplace.vim plugin that extends syntax highlighting to local, referred,
  and aliased vars via [`b:clojure_syntax_keywords`](#syntax-options).

  This is a reimplementation of the DynamicHighlighting feature from
  VimClojure.

Clojure REPL Plugins
====================

If you would like to get more serious about programming in Clojure, consider
using an interactive
[Clojure REPL plugin](https://github.com/guns/vim-clojure-static/wiki/Clojure-REPL-Plugins).

Syntax Options
==============

Syntax highlighting for public vars from `clojure.core` and `clojure.test` is
provided by default, but any symbol can be matched and highlighted by adding
it to the `g:clojure_syntax_keywords` or `b:clojure_syntax_keywords`
variables:

```vim
let g:clojure_syntax_keywords = {
    \ 'clojureMacro': ["defproject", "defcustom"],
    \ 'clojureFunc': ["string/join", "string/replace"]
    \ }
```

See `s:clojure_syntax_keywords` in the [syntax script](syntax/clojure.vim) for
a complete example.

The global version of this variable is intended for users that always wish
to have a certain set of symbols highlighted in a certain way, while the
buffer-local version is intended for plugin authors who wish to highlight
symbols dynamically.

If the buffer flag `b:clojure_syntax_without_core_keywords` is set, vars from
`clojure.core` are not highlighted by default. This is useful for highlighting
namespaces that have set `(:refer-clojure :only [])`.

[`vim-clojure-highlight`](https://github.com/guns/vim-clojure-highlight) uses
these variables to highlight extra vars when connected to a REPL.

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

### `g:clojure_fuzzy_indent`, `g:clojure_fuzzy_indent_patterns`, `g:clojure_fuzzy_indent_blacklist`

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

### `g:clojure_align_subforms`

By default, parenthesized compound forms that look like function calls and
whose head subform is on its own line have subsequent subforms indented by
two spaces relative to the opening paren:

```clojure
(foo
  bar
  baz)
```

Setting this option changes this behavior so that all subforms are aligned to
the same column, emulating the default behavior of clojure-mode.el:

```clojure
(foo
 bar
 baz)
```

This option is off by default.

```vim
" Default
let g:clojure_align_subforms = 0
```

Development
===========

Pull requests and patches are strongly encouraged!

A large portion of the syntax file is generated from Clojure code in
`clj/src/`. Generation of vim code in this fashion is preferred over hand
crafting of the same.

There is an incomplete syntax test suite in `clj/test/`. Any additions and
improvements to these tests are highly appreciated.

License and Acknowledgements
============================

Many thanks to [Meikel Brandmeyer](http://kotka.de/) for his excellent work on
making Vim a first class Clojure editor.

Thanks to [Tim Pope](https://github.com/tpope/) for advice in #vim.

`syntax/clojure.vim`

* Copyright 2007-2008 (c) Toralf Wittner <toralf.wittner@gmail.com>
* Copyright 2008-2012 (c) Meikel Brandmeyer <mb@kotka.de>

`ftdetect/clojure.vim`,<br>
`ftplugin/clojure.vim`,<br>
`indent/clojure.vim`

* Copyright 2008-2012 (c) Meikel Brandmeyer <mb@kotka.de>

Modified and relicensed under the Vim License for distribution with Vim:

* Copyright 2013-2014 (c) Sung Pae <self@sungpae.com>

See LICENSE.txt for more information.

<!--
 vim:ft=markdown:et:tw=78:
-->
