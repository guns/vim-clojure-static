;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.update
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as string]
            [vim-clojure-static.generate :as g])
  (:import (java.text SimpleDateFormat)
           (java.util Date)
           (java.util.regex Pattern)))

(def CLOJURE-SECTION
  #"(?ms)^CLOJURE.*?(?=^[\p{Lu} ]+\t*\*)")

(defn fjoin [& args]
  (string/join \/ args))

(defn qstr [& xs]
  (string/replace (apply str xs) "\\" "\\\\"))

(defn update-doc! [first-line-pattern src-file dst-file]
  (let [sbuf (->> src-file
                  io/reader
                  line-seq
                  (drop-while #(not (re-find first-line-pattern %)))
                  (string/join \newline))
        dbuf (slurp dst-file)
        dmatch (re-find CLOJURE-SECTION dbuf)
        hunk (re-find CLOJURE-SECTION sbuf)]
    (spit dst-file (string/replace-first dbuf dmatch hunk))))

(defn copy-runtime-files! [src dst & opts]
  (let [{:keys [tag date paths]} (apply hash-map opts)]
    (doseq [path paths
            :let [buf (-> (fjoin src path)
                          slurp
                          (string/replace "%%RELEASE_TAG%%" tag)
                          (string/replace "%%RELEASE_DATE%%" date))]]
      (spit (fjoin dst "runtime" path) buf))))

(defn project-replacements [dir]
  {(fjoin dir "syntax/clojure.vim")
   {"-*- KEYWORDS -*-"
    (qstr g/generation-comment
          g/clojure-version-comment
          g/vim-keywords)
    "-*- CHARACTER PROPERTY CLASSES -*-"
    (qstr g/generation-comment
          g/java-version-comment
          g/vim-posix-char-classes
          g/vim-java-char-classes
          g/vim-unicode-binary-char-classes
          g/vim-unicode-category-char-classes
          g/vim-unicode-script-char-classes
          g/vim-unicode-block-char-classes)
    "-*- TOP CLUSTER -*-"
    (qstr g/generation-comment
          (g/vim-top-cluster (mapv first g/keyword-groups)
                             (slurp (fjoin dir "syntax/clojure.vim"))))}

   (fjoin dir "ftplugin/clojure.vim")
   {"-*- LISPWORDS -*-"
    (qstr g/generation-comment
          g/vim-lispwords)}

   (fjoin dir "autoload/clojurecomplete.vim")
   {"-*- COMPLETION WORDS -*-"
    (qstr g/generation-comment
          g/clojure-version-comment
          g/vim-completion-words)}})

(defn update-project!
  "Update project runtime files in the given directory."
  [dir]
  (doseq [[file replacements] (project-replacements dir)]
    (doseq [[magic-comment replacement] replacements]
      (let [buf (slurp file)
            pat (Pattern/compile (str "(?s)\\Q" magic-comment "\\E\\n.*?\\n\\n"))
            rep (str magic-comment "\n" replacement "\n")
            buf' (string/replace buf pat rep)]
        (if (= buf buf')
          (printf "No changes: %s\n" magic-comment)
          (do (printf "Updating %s\n" magic-comment)
              (spit file buf')))))))

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
  (update-project! "..")
  (update-vim! "/home/guns/src/vim-clojure-static" "/home/guns/src/vim"))
