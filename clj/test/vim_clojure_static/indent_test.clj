(ns vim-clojure-static.indent-test
  (:require [clojure.test :refer [deftest]]
            [vim-clojure-static.test :refer [test-indent]]))

(deftest test-basic-sexp-indent
  (test-indent "works as expected with basic S-expressions"
               :in "test-basic-sexp-indent.txt"
               :out "test-basic-sexp-indent.txt"))

(deftest test-multibyte-indent
  (test-indent "with multibyte characters"
               :in "test-multibyte-indent.txt"
               :out "test-multibyte-indent.txt"))

(deftest test-inherit-indent
  (test-indent "is inherited from previous element"
               :in "test-inherit-indent.in"
               :out "test-inherit-indent.out"
               :keys "/α\\<CR>s\\<C-O>Oa\\<Esc>/β\\<CR>s\\<CR>\\<CR>\\<C-H>\\<C-H>\\<C-H>\\<C-H>\\<C-H>\\<C-H>\\<C-H>b\\<CR>c\\<CR>\\<CR>d\\<Esc>"))

(deftest test-side-effects-in-indentexpr
  (test-indent "GetClojureIndent does not move cursor"
               :in "test-side-effects-in-indentexpr.in"
               :out "test-side-effects-in-indentexpr.out"
               :keys "/α\\<CR>:call GetClojureIndent()\\<CR>rxj:call GetClojureIndent()\\<CR>ry"))

(deftest test-reader-conditional-indent
  (test-indent "reader conditionals are indented like maps"
               :in "test-reader-conditional-indent.in"
               :out "test-reader-conditional-indent.out"))
