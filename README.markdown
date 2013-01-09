

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



Meikel Brandmeyer's excellent Clojure runtime files, extracted for
static editing and use with alternate Clojure development plugins.

Rationale
=========

[VimClojure](http://www.vim.org/scripts/script.php?script_id=2501)
consists of a syntax script, indent script, filetype settings, limited
static completions, and a sophisticated synchronous REPL environment for
interacting with JVM Clojure processes.

While it is not necessary to use any of the interactive features of
VimClojure, the static runtime files are not standalone scripts and
cannot easily be extracted from the VimClojure support libraries. The
side effects of this coupling are:

* Hacking on the runtime files is more difficult.

* Installing the latest revisions from source is more complicated than
  tracking a single repository.

* Installing the whole VimClojure distribution for the runtime files is
  overkill. A smaller, self-contained set of files would be eligible for
  inclusion in Vim itself.

This is a shame since VimClojure's syntax and indent scripts are of very
high quality. This fork aims to address these problems.

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

* Static completion is not provided. If you are looking for
  comprehensive completions, please consider using an interactive
  development plugin listed below.

Interactive Clojure Development Plugins
=======================================

### [vim-foreplay](https://github.com/tpope/vim-foreplay)

New nREPL client by Tim Pope.

### [VimClojure](http://www.vim.org/scripts/script.php?script_id=2501)

The original interactive Clojure editing environment by Meikel
Brandmeyer. These runtime files are **incompatible** with the original
VimClojure project in several small ways, so be sure to uninstall
vim-clojure-static when using VimClojure.

Meikel has [announced](https://groups.google.com/forum/?fromgroups=#!topic/vimclojure/B-UU8qctd5A)
that the upcoming version of VimClojure will feature only the dynamic
portion of the project, and will be compatible with these static files.

### [slimv.vim](http://www.vim.org/scripts/script.php?script_id=2531)

SWANK client for Vim by Tamas Kovacs.

### [screen](http://www.vim.org/scripts/script.php?script_id=2711)

Send text to REPLs running in GNU Screen or tmux. Not Clojure specific.

By Eric Van Dewoestine.

Try <https://github.com/guns/screen> for better window handling if you
are running tmux 1.5 or higher.

Options
=======

The indent script has a few configurable options. Documentation with
default values below:

### `g:clojure_maxlines`

Maximum scan distance of searchpairpos(). Larger values trade performance
for correctness when dealing with very long forms. A value of 0 means search
without limits.

```vim
" Default
let g:clojure_maxlines = 100
```

### `g:clojure_fuzzy_indent`, `g:clojure_fuzzy_indent_patterns`, and `g:clojure_fuzzy_indent_blacklist`

The 'lispwords' option is a list of comma-separated words that mark special
forms whose following lines must be indented as if the word is on the
first line alone.

For example:

```clojure
(defn good []
  "Correct indentation")

(defn bad []
      "Incorrect indentation")
```

If you would like to match words that match a pattern, you can use the
fuzzy indent feature. The defaults are:

```vim
" Default
let g:clojure_fuzzy_indent = 1
let g:clojure_fuzzy_indent_patterns = ['^with', '^def', '^let']
let g:clojure_fuzzy_indent_blacklist = ['^with-meta$', '-fn$']

" Legacy comma-delimited string version; the list format above is
" recommended. Note that patterns are implicitly anchored with ^ and $.
let g:clojure_fuzzy_indent_patterns = 'with.*,def.*,let.*'
```

`g:clojure_fuzzy_indent_patterns` and `g:clojure_fuzzy_indent_blacklist` are
*Lists* of patterns that will be matched against the unqualified symbol at the
head of a list. This means that a pattern like `"^foo"` will match all these
candidates: `"foobar"`, `"my.ns/foobar"`, and `"#'foobar"`.

Each candidate word is tested for special treatment in this order:

1. Return true if word is literally in 'lispwords'
2. Return false if word matches a pattern in `g:clojure_fuzzy_indent_blacklist`
3. Return true if word matches a pattern in `g:clojure_fuzzy_indent_patterns`
4. Return false and indent normally otherwise

### `g:clojure_special_indent_words`

Some forms in Clojure are indented so that every subform is indented only two
spaces, regardless of 'lispwords'. If you have a custom construct that should
be indented in this idiosyncratic fashion, you can add your symbols to the
default list below.

```vim
" Default
let g:clojure_special_indent_words = 'deftype,defrecord,reify,proxy,extend-type,extend-protocol,letfn'
```

### `g:clojure_align_multiline_strings`

When indenting multiline strings, align subsequent lines to the column
after the opening quote, instead of the same column.

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

```vim
" Default
let g:clojure_align_multiline_strings = 0
```

License and Acknowledgements
============================

Thanks to Meikel Brandmeyer for his excellent work on making Vim a first
class Clojure editor.

Thanks to [Tim Pope](https://github.com/tpope/) for advice in #vim.

Copyright 2008-2012 (c) Meikel Brandmeyer.

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
