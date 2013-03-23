;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.update
  (:require [clojure.string :as string]
            [clojure.java.shell :as shell]))

(defn update-vim!
  "Update Vim repository runtime files in dst/runtime"
  [src dst]
  (let [join (fn [& args] (string/join \/ args))
        indent-file (join dst "runtime/doc/indent.txt")
        indent-buf (slurp indent-file)
        indent-match (re-find #"(?ms)^CLOJURE.*?(?=^[ \p{Lu}]+\t*\*)" indent-buf)
        indent-doc (re-find #"(?ms)^CLOJURE.*(?=^ABOUT)" (slurp (join src "doc/clojure.txt")))]
    ;; Insert indentation documentation
    (spit indent-file (string/replace-first indent-buf
                                            indent-match
                                            (str indent-doc \newline)))
    ;; Copy runtime files
    (doseq [file ["autoload/clojurecomplete.vim"
                  "ftplugin/clojure.vim"
                  "indent/clojure.vim"
                  "syntax/clojure.vim"]]
      (shell/sh "cp" (join src file) (join dst "runtime" file)))))

(comment
  (update-vim! "/home/guns/src/vim-clojure-static" "/home/guns/src/vim"))
