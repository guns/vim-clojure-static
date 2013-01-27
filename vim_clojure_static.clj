;; Copyright (c) 2012 Sung Pae <self@sungpae.com>
;; Distributed under the MIT license.
;; http://www.opensource.org/licenses/mit-license.php
;;
;; Copy this file to a project running the latest Clojure release and generate
;; updated Vimscript definitions.

(ns vim-clojure-static)

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
                  (let [xs (clojure.set/difference (set (filter pred coresyms)) @declared)]
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
                                (clojure.string/join \space (sort (names keywords)))))
                         builtins)]
    (str "\" Clojure " (clojure-version) \newline
         (clojure.string/join \newline definitions))))

(def core-dictionary
  "Newline delimited string of public vars in clojure.core. Intended for use
   as a dictionary file for Vim insert mode completion."
  (->> `clojure.core
       ns-publics
       keys
       (map str)
       sort
       (clojure.string/join \newline)))

(def special-forms-dictionary
  (->> special-forms
       (map str)
       sort
       (clojure.string/join \newline)))

(comment
  (do (spit "/tmp/clojure-keywords.vim" syntax-keywords)
      (spit "/home/guns/src/vim-clojure-static/ftplugin/clojure/clojure.core.txt" core-dictionary)
      (spit "/home/guns/src/vim-clojure-static/ftplugin/clojure/special-forms.txt" special-forms-dictionary)))
