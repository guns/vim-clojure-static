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

;; Helper functions (should probably be moved to a util ns).

(defn syntax-match [group pattern contained?]
  "Returns a Vimscript literal `syntax match` statement. The content of pattern
   is automatically wrapped in quotes."
  (let [parts ["syntax match" (name group) (format "\"%s\"" pattern)]
        parts (if contained?
                (conj parts "contained")
                parts)]
    (string/join \space parts)))

(defn re-pattern? [s]
  "Returns true if s is a valid regular expression pattern, false otherwiese."
  (try
    (re-pattern s)
    true
    (catch java.util.regex.PatternSyntaxException _ false)))

(defn pipe-join [ss]
  (string/join \| ss))

;;;; clojureRegex*CharClass generation ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bracket-char-class? [s]
  "Returns true if s is a valid posix, java, or unicode character class."
  (re-pattern? (format "\\p{%s}" s)))

;; This helps cut down on line noise.
(defn bracket-char-class-pattern [s]
  (format "\\v\\\\[pP]\\{%s\\}" s))

(def unicode-char-classes
  "Vimscript literal syntax match for unicode regex character classes."
  (delay ;; Since we need to hit the network.
    (let [page (slurp "http://www.regular-expressions.info/unicode.html")
          cs (loop [m (re-matcher #"\\p\{([a-zA-Z_]+)\}" page)
                    v (transient [])]
               (if-let [[_ t] (re-find m)]
                 (do
                   (conj! v t)
                   (recur m v))
                 (sort (distinct (persistent! v)))))
          cs (filter bracket-char-class? cs)
          ;; This complicates things mildly but apparently not every unicode
          ;; class can be prefixed with "Is".
          {cs1 true cs2 false} (group-by #(bracket-char-class? (str "Is" %)) cs)]
      (syntax-match
        :clojureRegexpUnicodeCharClass
        (bracket-char-class-pattern (format "%%(%%(Is)?%%(%s)|%%(%s))" (pipe-join cs1) (pipe-join cs2)))
        true))))

(def java-char-classes
  "Vimscript literal syntax match for (Is)java* regex character classes."
  (let [is-methods (->> java.lang.Character
                        r/reflect
                        :members
                        (map (comp name :name))
                        (filter #(.startsWith % "is"))
                        distinct
                        sort)
        cs (filter #(bracket-char-class? (str "java" %))
                   (map #(second (string/split % #"is" 2)) is-methods))
        {cs1 true cs2 false} (group-by #(bracket-char-class? (str "Is" %)) cs)]
    (syntax-match
      :clojureRegexpJavaCharClass
      (bracket-char-class-pattern (format "%%(%%(Is)?java%%(%s)|java%%(%s))" (pipe-join cs1) (pipe-join cs2)))
      true)))

(comment
  (spit "/tmp/clojure-defs.vim" (str syntax-keywords "\n\n" completion-words))
  (spit "/tmp/clojure-char-classes.vim" (str java-char-classes "\n" @unicode-char-classes)))
