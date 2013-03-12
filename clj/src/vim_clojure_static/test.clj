;; Copyright (c) 2013 Sung Pae <self@sungpae.com>
;; Distributed under the MIT license.
;; http://www.opensource.org/licenses/mit-license.php

(ns vim-clojure-static.test
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.edn :as edn]
            [clojure.string :as string]))

(defn syn-id-names
  "Map lines of clojure text to vim synID names at each column as keywords:

   (syn-id-names \"foo\" …) -> {\"foo\" [:clojureString :clojureString :clojureString] …}

   First parameter is the file that is used to communicate with Vim. The file
   is not deleted to allow manual inspection."
  [file & lines]
  (io/make-parents file)
  (spit file (string/join \newline lines))
  (shell/sh "vim" "-u" "NONE" "-N" "-S" "vim/syn-id-names.vim" file)
  ;; The last line of the file will contain valid EDN
  (into {} (map (fn [l ids] [l (mapv keyword ids)])
                lines
                (edn/read-string (peek (string/split-lines (slurp file)))))))

(comment
  (syn-id-names "tmp/syntax.clj" "#\"^[a-z]$\""))
