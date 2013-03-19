;; Authors: Sung Pae <self@sungpae.com>
;;          Joel Holdbrooks <cjholdbrooks@gmail.com>

(ns vim-clojure-static.generate
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [clojure.reflect :as reflect]))

;;
;; Helpers
;;

(defn re-pattern?
  "Returns true if s is a valid regular expression pattern, false otherwise."
  [s]
  (try
    (re-pattern s)
    true
    (catch java.util.regex.PatternSyntaxException _ false)))

(defn valid-property-class?
  "Returns true if s is a valid regular expression character property class."
  [s]
  (re-pattern? (format "\\p{%s}" s)))

(defn property-pattern
  "Vimscript very magic pattern for a character property class."
  [s]
  (format "\\v\\\\[pP]\\{%s\\}" s))

;;
;; Definitions
;;

(def generation-comment
  "\" Generated from https://github.com/guns/vim-clojure-static/blob/vim-release-004/clj/src/vim_clojure_static/generate.clj\n")

(def clojure-version-comment
  (format "\" Clojure version %s\n" (clojure-version)))

(def java-version-comment
  (format "\" Java version %s\n" (System/getProperty "java.version")))

(def special-forms
  "http://clojure.org/special_forms"
  '[def if do let quote var fn loop recur throw try catch finally
    monitor-enter monitor-exit . new set!])

(def keyword-groups
  "Special forms, constants, and every public var in clojure.core listed by
   syntax group suffix."
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
                    xs))]
    (conj builtins
          ;; Clojure devs are fastidious about accurate metadata
          ["Define" (select! #(re-seq #"\Adef(?!ault)" (str %)))]
          ["Macro" (select! #(:macro (meta (resolve %))))]
          ["Func" (select! #(:arglists (meta (resolve %))))]
          ["Variable" (select! identity)])))

(def java-property-classes
  "java.lang.Character boolean isMethods as valid regular expression property
   class names."
  (->> java.lang.Character
       reflect/reflect
       :members
       (map (comp name :name))
       (filter #(.startsWith % "is"))
       distinct
       (map #(string/replace % #"\Ais" "java"))
       (filter valid-property-class?)))

;;
;; Vimscript literals
;;

(def vim-syntax-keywords
  "Vimscript literal `syntax keyword` definitions."
  (let [names (fn [coll]
                (reduce (fn [v x]
                          ;; Include fully qualified versions of core vars
                          (cond (symbol? x) (if-let [m (meta (resolve x))]
                                              (conj v (str (:name m)) (str (:ns m) \/ (:name m)))
                                              (conj v (str x)))
                                (nil? x) (conj v "nil")
                                :else (conj v (str x))))
                        [] coll))
        definitions (map (fn [[group keywords]]
                           (format "syntax keyword clojure%s %s"
                                   group
                                   (string/join \space (sort (names keywords)))))
                         keyword-groups)]
    (str clojure-version-comment
         (string/join \newline definitions))))

(def vim-completion-words
  "Vimscript literal list of words for omnifunc completion."
  (str clojure-version-comment
       (format "let s:words = [%s]"
               (->> `clojure.core
                    ns-publics
                    keys
                    (concat special-forms)
                    (map #(str \" % \"))
                    sort
                    (string/join \,)))))

(def vim-java-char-classes
  "Vimscript literal `syntax match` for \\p{javaMethod} property classes."
  (let [ps (sort (map #(string/replace % #"\Ajava" "") java-property-classes))
        vimpat (property-pattern (format "java%%(%s)" (string/join \| ps)))]
    (str java-version-comment
         (format "syntax match clojureRegexpJavaCharClass \"%s\" contained display" vimpat))))

(comment
  (spit "tmp/clojure-defs.vim"
        (->> [vim-syntax-keywords vim-completion-words vim-java-char-classes]
             (map (partial str generation-comment))
             (string/join "\n\n"))))
