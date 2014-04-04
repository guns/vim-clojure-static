;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as string]
            [clojure.test :as test])
  (:import (java.io File)
           (java.util List)))

(defmacro with-tempfile
  {:requires [File]}
  [tmp-sym & body]
  `(let [~tmp-sym (File/createTempFile "vim-clojure-static" ".tmp")]
     (try
       ~@body
       (finally
         (.delete ~tmp-sym)))))

(defn vim-exec
  "Spit buf into file, then execute vim-expr after Vim loads the file. The
   value of vim-expr is evaluated as EDN and returned."
  [file buf vim-expr & opts]
  (let [{:keys [pre]} (apply hash-map opts)]
    (with-tempfile tmp
      (io/make-parents file)
      (spit file buf)
      (spit tmp (str "let @x = " vim-expr))
      (let [{:keys [exit err]}
            (shell/sh "vim" "-N" "-u" "vim/test-runtime.vim"
                      "-c" (or pre "execute")
                      "-c" (str "source " tmp)
                      "-c" (str "call writefile([@x], " (pr-str (str tmp)) ")")
                      "-c" "quitall!"
                      file)]
        (when-not (zero? exit)
          (throw (RuntimeException. ^String err))))
      (edn/read-string (slurp tmp)))))

(defn syn-id-names
  "Map lines of clojure text to vim synID names at each column as keywords:

   (syn-id-names \"foo\" …) -> {\"foo\" [:clojureString :clojureString :clojureString] …}

   First parameter is the file that is used to communicate with Vim. The file
   is not deleted to allow manual inspection."
  [file & lines]
  (into {} (map (fn [l ids] [l (mapv keyword ids)])
                lines
                (vim-exec file (string/join \newline lines) "ClojureSynIDNames()"))))

(defn subfmt
  "Extract a subsequence of seq s corresponding to the character positions of
   %s in format spec fmt"
  [fmt s]
  (let [f (seq (format fmt \o001))
        i (.indexOf ^List f \o001)]
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
  {:requires [#'test/deftest syn-id-names subfmt]}
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
        test-file (str "tmp/" name ".clj")
        syntable (gensym "syntable")]
    `(test/deftest ~name
       (spit ~test-file "")
       (let [~syntable (syn-id-names ~test-file ~@strings)]
         ~@(map (fn [{:keys [fmt ss λs]}]
                  `(test/testing ~fmt
                     ~@(map (fn [s λ] `(test/is (~λ (subfmt ~fmt (get ~syntable ~s)))))
                            ss λs)))
                contexts)))))

(defmacro defpredicates
  "Create two complementary predicate vars, `sym` and `!sym`, which test if
   all members of a passed collection are equal to `kw`"
  [sym kw]
  `(do
     (defn ~sym
       ~(str "Returns true if all elements of coll equal " kw)
       {:arglists '~'[coll]}
       [coll#]
       (every? (partial = ~kw) coll#))
     (defn ~(symbol (str \! sym))
       ~(str "Returns true if any elements of coll do not equal " kw)
       {:arglists '~'[coll]}
       [coll#]
       (boolean (some (partial not= ~kw) coll#)))))

(defmacro with-transform-test
  "Copy contents of `in` to a tempfile, execute body with tempfile bound to
   tmp-sym, then finally compare the transformed contents of the tempfile with
   the contents of `out`.

   `in` and `out` are urls that will be passed to clojure.java.io/resource."
  {:requires [#'test/testing #'with-tempfile]}
  [string {:keys [in out]} [tmp-sym] & body]
  `(test/testing ~string
     (with-tempfile ~tmp-sym
       (try
         (spit ~tmp-sym (slurp (~io/resource ~in)))
         ~@body
         (catch Throwable e#
           (spit ~tmp-sym e#))
         (finally
           (test/is (= (slurp ~tmp-sym)
                       (slurp (~io/resource ~out)))))))))

(defmacro test-indent
  {:requires [#'with-transform-test]}
  [string & opts]
  (let [{:keys [in out pre keys]} (apply hash-map opts)
        test-file (str "tmp/test-indent-" (string/replace string #"[^\w-]" "-") ".clj")
        vim-expr (if keys
                   (format "TypeKeys(%s)" (string/replace (pr-str keys) "\\\\" "\\"))
                   "IndentFile()")]
    `(with-transform-test ~string
       {:in ~in :out ~out}
       [tmp#]
       ;; FIXME: Too much file IO
       (~io/make-parents ~test-file)
       (spit ~test-file "")
       (~vim-exec ~test-file (slurp tmp#) ~vim-expr ~@(when pre [:pre pre]))
       (spit tmp# (slurp ~test-file)))))

(defn benchmark [n file buf & exprs]
  (vim-exec file buf (format "Benchmark(%d, %s)"
                             n
                             (string/join \, (map pr-str exprs)))))
