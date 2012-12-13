

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
                 .      `Y888P   .   o8o
               .o8             .o8   `"'
      .oooo.o.o888oo .oooo.  .o888oooooo  .ooooo.
     d88(  "8  888  `P  )88b   888  `888 d88' `"Y8
     `"Y88b.   888   .oP"888   888   888 888
     o.  )88b  888 .d8(  888   888 . 888 888   .o8
     8""888P'  "888"`Y888""8o  "888"o888o`Y8bod8P'



Meikel Brandmeyer's excellent Clojure runtime files, extracted and
decomplected for static editing and for use with alternate Clojure
development plugins.

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

* Hacking on the runtime files is more difficult

* Installation from source is more complicated (e.g. installing clojure
  runtime files from the source repository must first build a zip file
  via `gradle`)

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

Please send any bug reports to this project; while the majority of the
code is in sync with VimClojure, there are still enough differences that
bugs may not reside upstream.

Alternate Interactive Development Plugins
=========================================

TODO

Options
=======

The indent script has a few configurable options. Documentation with
default values below:

### `g:clojure_maxlines`

Maximum scan distance of searchpairpos().

```vim
" Default
let g:clojure_maxlines = 100
```

### `g:clojure_fuzzy_indent` and `g:clojure_fuzzy_indent_patterns`

Indent words that match patterns as if they are included in `lispwords`.

```vim
" Default
let g:clojure_fuzzy_indent = 1
let g:clojure_fuzzy_indent_patterns = "with.*,def.*,let.*"
```

### `g:clojure_align_multiline_strings`

When indenting multiline strings, align subsequent lines to the column
after the opening quote, instead of the same column.

Demo:

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
