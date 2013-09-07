;; Authors: Sung Pae <self@sungpae.com>
;;          Joel Holdbrooks <cjholdbrooks@gmail.com>

(ns vim-clojure-static.generate
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [frak]))

;;
;; Helpers
;;

(defn vim-frak-pattern
  "Create a non-capturing regular expression pattern compatible with Vim."
  [strs]
  (-> (str (frak/pattern strs))
      (string/replace #"\(\?:" "\\%\\(")))

(defn property-pattern
  "Vimscript very magic pattern for a character property class."
  ([s] (property-pattern s true))
  ([s braces?]
   (if braces?
     (format "\\v\\\\[pP]\\{%s\\}" s)
     (format "\\v\\\\[pP]%s" s))))

(defn syntax-match-properties
  "Vimscript literal `syntax match` for a character property class."
  ([group fmt props] (syntax-match-properties group fmt props true))
  ([group fmt props braces?]
   (format "syntax match %s \"%s\" contained display\n"
           (name group)
           (property-pattern (format fmt (vim-frak-pattern props)) braces?))))

(defn get-private-field
  "Violate encapsulation and get the value of a private field."
  [cls fieldname]
  (let [field (first (filter #(= fieldname (.getName %)) (.getDeclaredFields cls)))]
    (.setAccessible field true)
    (.get field field)))

;;
;; Definitions
;;

(def generation-comment
  "\" Generated from https://github.com/guns/vim-clojure-static/blob/%%RELEASE_TAG%%/clj/src/vim_clojure_static/generate.clj\n")

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
                    (vec xs)))]
    (conj builtins
          ;; Clojure devs are fastidious about accurate metadata
          ["Define" (select! #(re-seq #"\Adef(?!ault)" (str %)))]
          ["Macro" (select! #(:macro (meta (resolve %))))]
          ["Func" (select! #(:arglists (meta (resolve %))))]
          ["Variable" (select! identity)])))

(def character-properties
  "Character property names derived via reflection."
  (let [props (map (fn [[p typ]] [p (string/replace (.getName (type typ)) #".*\$(.+)" "$1")])
                   (get-private-field java.util.regex.Pattern$CharPropertyNames "map"))
        props (map (fn [[typ ps]] [typ (map first ps)])
                   (group-by peek props))
        props (into {} props)
        binary (concat (map #(. % name) (get-private-field java.util.regex.UnicodeProp "$VALUES"))
                       (keys (get-private-field java.util.regex.UnicodeProp "aliases")))
        script (concat (map #(. % name) (java.lang.Character$UnicodeScript/values))
                       (keys (get-private-field java.lang.Character$UnicodeScript "aliases")))
        block (keys (get-private-field java.lang.Character$UnicodeBlock "map"))]
    ;;
    ;; * The keys "1"…"5" reflect the order of CharPropertyFactory
    ;;   declarations in Pattern.java!
    ;;
    ;; * The "L1" (Latin-1) category is not defined by Unicode and exists
    ;;   merely as an alias for the first 8 bits of code points.
    ;;
    ;; * The "all" category is the Unicode "Any" category by a different name,
    ;;   and thus excluded.
    ;;
    {:posix    (disj (set (mapcat (partial get props) ["2" "3"])) "L1")
     :java     (set (get props "4"))
     :binary   (set binary)
     :category (set (get props "1"))
     :script   (set script)
     :block    (set block)}))

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
                           (format "syntax keyword clojure%s %s\n"
                                   group
                                   (string/join \space (sort (names keywords)))))
                         keyword-groups)]
    (string/join definitions)))

(def vim-completion-words
  "Vimscript literal list of words for omnifunc completion."
  (format "let s:words = [%s]\n"
          (->> `clojure.core
               ns-publics
               keys
               (concat special-forms)
               (map #(str \" % \"))
               sort
               (string/join \,))))

(def vim-posix-char-classes
  "Vimscript literal `syntax match` for POSIX character classes."
  ;; `IsPosix` works, but is undefined.
  (syntax-match-properties
    :clojureRegexpPosixCharClass
    "%s"
    (:posix character-properties)))

(def vim-java-char-classes
  "Vimscript literal `syntax match` for \\p{javaMethod} property classes."
  ;; `IsjavaMethod` works, but is undefined.
  (syntax-match-properties
    :clojureRegexpJavaCharClass
    "java%s"
    (map #(string/replace % #"\Ajava" "") (:java character-properties))))

(def vim-unicode-binary-char-classes
  "Vimscript literal `syntax match` for Unicode Binary properties."
  ;; Though the docs do not mention it, the property name is matched case
  ;; insensitively like the other Unicode properties.
  (syntax-match-properties
    :clojureRegexpUnicodeCharClass
    "\\cIs%s"
    (map string/lower-case (:binary character-properties))))

(def vim-unicode-category-char-classes
  "Vimscript literal `syntax match` for Unicode General Category classes."
  (let [cats (sort (:category character-properties))
        chrs (->> (map seq cats)
                  (group-by first)
                  (keys)
                  (map str)
                  (sort))]
    ;; gc= and general_category= can be case insensitive, but this is behavior
    ;; is undefined.
    (str
      (syntax-match-properties
        :clojureRegexpUnicodeCharClass
        "%s"
        chrs
        false)
      (syntax-match-properties
        :clojureRegexpUnicodeCharClass
        "%s"
        cats)
      (syntax-match-properties
        :clojureRegexpUnicodeCharClass
        "%%(Is|gc\\=|general_category\\=)?%s"
        cats))))

(def vim-unicode-script-char-classes
  "Vimscript literal `syntax match` for Unicode Script properties."
  ;; Script names are matched case insensitively, but Is, sc=, and script=
  ;; should be matched exactly. In this case, only Is is matched exactly, but
  ;; this is an acceptable trade-off.
  ;;
  ;; InScriptName works, but is undefined.
  (syntax-match-properties
    :clojureRegexpUnicodeCharClass
    "\\c%%(Is|sc\\=|script\\=)%s"
    (map string/lower-case (:script character-properties))))

(def vim-unicode-block-char-classes
  "Vimscript literal `syntax match` for Unicode Block properties."
  ;; Block names work like Script names, except the In prefix is used in place
  ;; of Is.
  (syntax-match-properties
    :clojureRegexpUnicodeCharClass
    "\\c%%(In|blk\\=|block\\=)%s"
    (map string/lower-case (:block character-properties))))

(def comprehensive-clojure-character-property-regexps
  "A string representing a Clojure literal vector of regular expressions
   containing all possible property character classes. For testing Vimscript
   syntax matching optimizations."
  (let [fmt (fn [prefix prop-key]
              (let [props (map (partial format "\\p{%s%s}" prefix)
                               (sort (get character-properties prop-key)))]
                (format "#\"%s\"" (string/join props))))]
    (string/join \newline [(fmt "" :posix)
                           (fmt "" :java)
                           (fmt "Is" :binary)
                           (fmt "general_category=" :category)
                           (fmt "script=" :script)
                           (fmt "block=" :block)])))

(comment
  ;; Generate the vim literal definitions for pasting into the runtime files.
  (spit "tmp/clojure-defs.vim"
        (str generation-comment
             clojure-version-comment
             vim-syntax-keywords
             \newline
             generation-comment
             clojure-version-comment
             vim-completion-words
             \newline
             generation-comment
             java-version-comment
             vim-posix-char-classes
             vim-java-char-classes
             vim-unicode-binary-char-classes
             vim-unicode-category-char-classes
             vim-unicode-script-char-classes
             vim-unicode-block-char-classes))
  ;; Generate an example file with all possible character property literals.
  (spit "tmp/all-char-props.clj"
        comprehensive-clojure-character-property-regexps)
  )
