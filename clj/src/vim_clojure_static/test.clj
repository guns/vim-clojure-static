;; Authors: Sung Pae <self@sungpae.com>

(ns vim-clojure-static.test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as string]
            [clojure.test :as test])
  (:import (java.io File)
           (java.util List)))

(defn vim-exec
  "Spit buf into file, then execute vim-expr after Vim loads the file. The
   value of vim-expr is evaluated as EDN and returned."
  [file buf vim-expr]
  (let [tmp (File/createTempFile "vim-clojure-static.test." ".out")]
    (try
      (io/make-parents file)
      (spit file buf)
      (spit tmp (str "let @x = " vim-expr))
      (shell/sh "vim" "-N" "-u" "vim/test-runtime.vim"
                "-c" (str "source " tmp " | call writefile([@x], " (pr-str (str tmp)) ") | quitall!")
                file)
      (edn/read-string (slurp tmp))
      (finally
        (.delete tmp)))))

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
  {:require [#'test/deftest]}
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
       ~(str "Returns true if any alements of coll do not equal " kw)
       {:arglists '~'[coll]}
       [coll#]
       (boolean (some (partial not= ~kw) coll#)))))

(defn benchmark [n file buf & exprs]
  (vim-exec file buf (format "Benchmark(%d, %s)"
                             n
                             (string/join \, (map pr-str exprs)))))
