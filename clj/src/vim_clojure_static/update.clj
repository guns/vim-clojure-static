;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.update
  (:require [clojure.string :as string]
            [clojure.java.shell :refer [sh]])
  (:import (java.text SimpleDateFormat)
           (java.util Date)))

(defn update-vim!
  "Update Vim repository runtime files in dst/runtime"
  [src dst]
  (let [join (fn [& args] (string/join \/ args))
        indent-file (join dst "runtime/doc/indent.txt")
        indent-buf (slurp indent-file)
        indent-match (re-find #"(?ms)^CLOJURE.*?(?=^[ \p{Lu}]+\t*\*)" indent-buf)
        indent-doc (re-find #"(?ms)^CLOJURE.*(?=^ABOUT)" (slurp (join src "doc/clojure.txt")))
        current-date (.format (SimpleDateFormat. "dd MMMM YYYY") (Date.))
        current-tag (string/trim-newline (:out (sh "git" "tag" "--points-at" "HEAD")))]
    (assert (seq current-tag) "Git HEAD is not tagged!")
    ;; Insert indentation documentation
    (spit indent-file (string/replace-first indent-buf
                                            indent-match
                                            (str indent-doc \newline)))
    ;; Copy runtime files
    (doseq [file ["autoload/clojurecomplete.vim"
                  "ftplugin/clojure.vim"
                  "indent/clojure.vim"
                  "syntax/clojure.vim"]
            :let [buf (-> (join src file)
                          slurp
                          (string/replace "%%RELEASE_DATE%%" current-date)
                          (string/replace "%%RELEASE_TAG%%" current-tag))]]
      (spit (join dst "runtime" file) buf))))

(comment
  (update-vim! "/home/guns/src/vim-clojure-static" "/home/guns/src/vim"))
