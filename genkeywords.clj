#!/usr/bin/env lein-exec
;;
;; Vim keyword syntax generator for Clojure.
;; Functionality kept in a monolithic function for easy copy and paste.
;;
;; Copyright (c) 2012 Sung Pae <self@sungpae.com>
;; Distributed under the MIT license.
;; http://www.opensource.org/licenses/mit-license.php

(defn vim-syntax-keywords
  "Return Vimscript literal syntax keyword definitions. Special forms,
   constants, and every public var in clojure.core will be included."
  []
  (let [builtins [["Constant" '[nil]]
                  ["Boolean" '[true false]]
                  ;; http://clojure.org/special_forms
                  ["Special" '[def if do let quote var fn loop recur throw try
                               catch finally monitor-enter monitor-exit . new set!]]
                  ;; The duplicates from Special are intentional here
                  ["Exception" '[try catch finally throw]]
                  ["Cond" '[if-not if-let when when-> when->> when-not
                            when-let when-first cond condp case]]
                  ["Repeat" '[map mapv mapcat reduce reduce-kv filter filterv
                              for doall dorun doseq dotimes map-indexed keep
                              keep-indexed while]]]
        declared (atom (set (filter symbol? (mapcat peek builtins))))
        coresyms (keys (ns-publics 'clojure.core))
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

(println (vim-syntax-keywords))
