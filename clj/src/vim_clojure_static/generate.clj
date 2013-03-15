;; Copyright (c) 2013 Sung Pae <self@sungpae.com>
;; Distributed under the MIT license.
;; http://www.opensource.org/licenses/mit-license.php

(ns vim-clojure-static.generate
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [clojure.reflect :as r]))

(def generation-message
  (str "\" Generated from https://github.com/guns/vim-clojure-static/blob/vim-release-004/clj/src/vim_clojure_static/generate.clj"
       \newline
       "\" Clojure " (clojure-version)
       \newline))

(def special-forms
  "http://clojure.org/special_forms"
  '[def if do let quote var fn loop recur throw try catch finally
    monitor-enter monitor-exit . new set!])

(def syntax-keywords
  "Vimscript literal syntax keyword definitions. Special forms, constants, and
   every public var in clojure.core is included."
  (let [builtins [["Constant" '[nil]]
                  ["Boolean" '[true false]]
                  ["Special" special-forms]
                  ;; The duplicates from Special are intentional here
                  ["Exception" '[throw try catch finally]]
                  ["Cond" '[case cond cond-> cond->> condp if-let if-not when
                            when-first when-let when-not]]
                  ;; Imperative looping constructs (not sequence functions)
                  ["Repeat" '[doall dorun doseq dotimes while]]]
        declared (atom (set (filter symbol? (mapcat peek builtins))))
        coresyms (keys (ns-publics `clojure.core))
        select! (fn [pred]
                  (let [xs (set/difference (set (filter pred coresyms)) @declared)]
                    (swap! declared into xs)
                    xs))
        builtins (conj builtins
                       ;; Clojure devs are fastidious about accurate metadata
                       ["Define" (select! #(re-seq #"\Adef(?!ault)" (str %)))]
                       ["Macro" (select! #(:macro (meta (resolve %))))]
                       ["Func" (select! #(:arglists (meta (resolve %))))]
                       ["Variable" (select! identity)])
        names (fn [coll]
                (reduce (fn [v x]
                          ;; Include fully qualified versions of core vars
                          (cond (symbol? x) (if-let [m (meta (resolve x))]
                                              (conj v (str (:name m)) (str (:ns m) \/ (:name m)))
                                              (conj v (str x)))
                                (nil? x) (conj v "nil")
                                :else (conj v (str x))))
                        [] coll))
        definitions (map (fn [[group keywords]]
                           (str "syntax keyword clojure" group \space
                                (string/join \space (sort (names keywords)))))
                         builtins)]
    (str generation-message (string/join \newline definitions))))

(def completion-words
  "Vimscript literal list of special forms and public vars in clojure.core."
  (str generation-message
       (format "let s:words = [%s]"
               (->> `clojure.core
                    ns-publics
                    keys
                    (concat special-forms)
                    (map #(str \" % \"))
                    sort
                    (string/join \,)))))

(def java-char-class-names
  "Returns a list of valid java character class names (excluding the \"java\"
   prefix) for use in a regular expression literal."
  ;; java.lang.Character/is* methods.
  (let [is-ms (->> java.lang.Character
                   r/reflect
                   :members
                   (map (comp name :name))
                   (filter #(.startsWith % "is"))
                   set
                   sort)]
    (reduce
      (fn [pats is-m]
        (let [c-name (second (s/split is-m #"is" 2))]
          (try
            (re-pattern (format "\\p{java%s}" c-name))
            (conj pats c-name)
            (catch java.util.regex.PatternSyntaxException e pats))))
      []
      is-ms)))

(comment
  (spit "/tmp/clojure-defs.vim" (str syntax-keywords "\n\n" completion-words)))
