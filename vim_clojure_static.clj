;; Copyright (c) 2012 Sung Pae <self@sungpae.com>
;; Distributed under the MIT license.
;; http://www.opensource.org/licenses/mit-license.php
;;
;; Copy this file to a project running the latest Clojure release and generate
;; updated Vimscript definitions.

(ns vim-clojure-static
  (:require clojure.string clojure.java.shell))

(def generation-message
  (str "\" Generated from https://github.com/guns/vim-clojure-static/blob/vim-release-002/vim_clojure_static.clj"
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
    (str generation-message (clojure.string/join \newline definitions))))

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
                    (clojure.string/join \,)))))

(defn update-vim!
  "Update runtime files in dir/runtime"
  [src dst]
  (let [join (fn [& args] (clojure.string/join \/ args))
        indent-file (join dst "runtime/doc/indent.txt")
        indent-buf (slurp indent-file)
        indent-match (re-find #"(?ms)^CLOJURE.*?(?=^[ \p{Lu}]+\t*\*)" indent-buf)
        indent-doc (re-find #"(?ms)^CLOJURE.*(?=^ABOUT)" (slurp (join src "doc/clojure.txt")))]
    ;; Insert indentation documentation
    (spit indent-file (clojure.string/replace-first indent-buf
                                                    indent-match
                                                    (str indent-doc \newline)))
    ;; Copy runtime files
    (doseq [file ["autoload/clojurecomplete.vim" "ftplugin/clojure.vim" "indent/clojure.vim" "syntax/clojure.vim"]]
      (println (clojure.java.shell/sh "cp" (join src file) (join dst "runtime" file))))))

(comment
  (spit "/tmp/clojure-defs.vim" (str syntax-keywords "\n\n" completion-words))
  (update-vim! "/home/guns/src/vim-clojure-static" "/home/guns/src/vim"))
