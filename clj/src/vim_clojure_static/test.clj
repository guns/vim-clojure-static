;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.test
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.test :as test]))

(defn syn-id-names
  "Map lines of clojure text to vim synID names at each column as keywords:

   (syn-id-names \"foo\" …) -> {\"foo\" [:clojureString :clojureString :clojureString] …}

   First parameter is the file that is used to communicate with Vim. The file
   is not deleted to allow manual inspection."
  [file & lines]
  (io/make-parents file)
  (spit file (string/join \newline lines))
  (shell/sh "vim" "-u" "NONE" "-N" "-S" "vim/test-runtime.vim" file)
  ;; The last line of the file will contain valid EDN
  (into {} (map (fn [l ids] [l (mapv keyword ids)])
                lines
                (edn/read-string (peek (string/split-lines (slurp file)))))))

(defn subfmt
  "Extract a subsequence of seq s corresponding to the character positions of
   %s in format spec fmt"
  [fmt s]
  (let [f (seq (format fmt \o001))
        i (.indexOf f \o001)]
    (->> s
         (drop i)
         (drop-last (- (count f) i 1)))))

(defmacro defsyntaxtest
  "Create a new testing var with tests in the format:

   (defsyntaxtest example
     [format
      [test-string test-predicate
       …]]
     [\"#\\\"%s\\\"\"
      [\"123\" #(every? (partial = :clojureRegexp) %)
       …]]
     […])

   At runtime the syn-id-names of the strings (which are placed in the format
   spec) are passed to their associated predicates. The format spec should
   contain a single `%s`."
  [name & body]
  (assert (every? (fn [[fmt tests]] (and (string? fmt)
                                         (coll? tests)
                                         (even? (count tests))))
                  body))
  (let [[strings contexts] (reduce (fn [[strings contexts] [fmt tests]]
                                     (let [[ss λs] (apply map list (partition 2 tests))
                                           ss (map #(format fmt %) ss)]
                                       [(concat strings ss)
                                        (conj contexts {:fmt fmt :ss ss :λs λs})]))
                                   [[] []] body)
        syntable (gensym "syntable")]
    `(test/deftest ~name
       ;; Shellout to vim should happen at runtime
       (let [~syntable (syn-id-names (str "tmp/" ~(str name) ".clj") ~@strings)]
         ~@(map (fn [{:keys [fmt ss λs]}]
                  `(test/testing ~fmt
                     ~@(map (fn [s λ] `(test/is (~λ (subfmt ~fmt (get ~syntable ~s)))))
                            ss λs)))
                contexts)))))

(defn vim-nfa-dump
  "Run a patched version of Vim compiled with -DDEBUG on a new file containing
   buffer, then move the NFA log to log-path. The patch is located at
   vim/custom-nfa-log.patch"
  [vim-path buffer log-path]
  (let [file "tmp/nfa-test-file.clj"]
    (spit file buffer)
    (time (shell/sh vim-path "-u" "NONE" "-N" "-S" "vim/test-runtime.vim" file))
    (shell/sh "mv" "nfa_regexp.log" log-path)))

(defn compare-nfa-dumps
  "Dump NFA logs with given buffer and syntax-files; log-files are written to
   tmp/ and are distinguished by the hash of the buffer and syntax script.

   The vim-path passed to vim-nfa-dump should either be in the VIMDEBUG
   environment variable, or be the top vim in your PATH.

   Returns the line count of each corresponding log file."
  [buf [& syntax-files] & opts]
  (let [{:keys [vim-path]
         :or {vim-path (or (System/getenv "VIMDEBUG") "vim")}} opts
        syn-path "../syntax/clojure.vim"
        orig-syn (slurp syn-path)
        buf-hash (hash buf)]
    (try
      (mapv (fn [path]
              (let [syn-buf (slurp path)
                    syn-hash (hash syn-buf)
                    log-path (format "tmp/debug:%d:%d.log" buf-hash syn-hash)]
                (spit syn-path syn-buf)
                (vim-nfa-dump vim-path buf log-path)
                (count (re-seq #"\n" (slurp log-path)))))
            syntax-files)
      (finally
        (spit syn-path orig-syn)))))

(comment

  (macroexpand-1
    '(defsyntaxtest number-literals-test
       ["%s"
        ["123" #(every? (partial = :clojureNumber) %)
         "456" #(every? (partial = :clojureNumber) %)]]
       ["#\"%s\""
        ["^" #(= % [:clojureRegexpBoundary])]]))
  (test #'number-literals-test)

  (defn dump! [buf]
    (compare-nfa-dumps (format "#\"\\p{%s}\"\n" buf)
                       ["../syntax/clojure.vim" "tmp/altsyntax.vim"]))

  (dump! "Ll")
  (dump! "javaLowercase")
  (dump! "block=UNIFIED CANADIAN ABORIGINAL SYLLABICS")

  )
