;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.update
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as string])
  (:import (java.text SimpleDateFormat)
           (java.util Date)))

(defn fjoin [& args]
  (string/join \/ args))

(def clojure-section
  #"(?ms)^CLOJURE.*?(?=^[\p{Lu} ]+\t*\*)")

(defn update-doc! [first-line-pattern src-file dst-file]
  (let [sbuf (->> src-file
                  io/reader
                  line-seq
                  (drop-while #(not (re-find first-line-pattern %)))
                  (string/join \newline))
        dbuf (slurp dst-file)
        dmatch (re-find clojure-section dbuf)
        hunk (re-find clojure-section sbuf)]
    (spit dst-file (string/replace-first dbuf dmatch hunk))))

(defn copy-runtime-files! [src dst & opts]
  (let [{:keys [tag date paths]} (apply hash-map opts)]
    (doseq [path paths
            :let [buf (-> (fjoin src path)
                          slurp
                          (string/replace "%%RELEASE_TAG%%" tag)
                          (string/replace "%%RELEASE_DATE%%" date))]]
      (spit (fjoin dst "runtime" path) buf))))

(defn update-vim!
  "Update Vim repository runtime files in dst/runtime"
  [src dst]
  (let [current-tag (string/trim-newline (:out (sh "git" "tag" "--points-at" "HEAD")))
        current-date (.format (SimpleDateFormat. "dd MMMM YYYY") (Date.))]
    (assert (seq current-tag) "Git HEAD is not tagged!")
    (update-doc! #"CLOJURE\t*\*ft-clojure-indent\*"
                 (fjoin src "doc/clojure.txt")
                 (fjoin dst "runtime/doc/indent.txt"))
    (update-doc! #"CLOJURE\t*\*ft-clojure-syntax\*"
                 (fjoin src "doc/clojure.txt")
                 (fjoin dst "runtime/doc/syntax.txt"))
    (copy-runtime-files! src dst
                         :tag current-tag
                         :date current-date
                         :paths ["autoload/clojurecomplete.vim"
                                 "ftplugin/clojure.vim"
                                 "indent/clojure.vim"
                                 "syntax/clojure.vim"])))

(comment
  (update-vim! "/home/guns/src/vim-clojure-static" "/home/guns/src/vim"))
