(ns vim-clojure-static.indent-test
  (:require [clojure.test :refer [deftest]]
            [vim-clojure-static.test :refer [test-indent]]))

(deftest test-indentation
  (test-indent "is inherited from previous element"
               :in "test-inherit-indent.in"
               :out "test-inherit-indent.out"
               :keys "/α\\<CR>s\\<C-O>Oa\\<Esc>/β\\<CR>s\\<CR>\\<CR>\\<C-H>\\<C-H>\\<C-H>\\<C-H>\\<C-H>\\<C-H>\\<C-H>b\\<CR>c\\<CR>\\<CR>d\\<Esc>"))
