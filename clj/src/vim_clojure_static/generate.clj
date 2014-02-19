;; Authors: Sung Pae <self@sungpae.com>
;;          Joel Holdbrooks <cjholdbrooks@gmail.com>

(ns vim-clojure-static.generate
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [frak :as f])
  (:import (clojure.lang MultiFn)
           (java.lang Character$UnicodeBlock Character$UnicodeScript)
           (java.lang.reflect Field)
           (java.util.regex Pattern$CharPropertyNames UnicodeProp)))

;;
;; Helpers
;;

(defn vim-frak-pattern
  "Create a non-capturing regular expression pattern compatible with Vim."
  [strs]
  (-> (f/string-pattern strs {:escape-chars :vim})
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
  [^Class cls fieldname]
  (let [^Field field (first (filter #(= fieldname (.getName ^Field %))
                                    (.getDeclaredFields cls)))]
    (.setAccessible field true)
    (.get field field)))

(defn fn-var? [v]
  (let [f @v]
    (or (contains? (meta v) :arglists)
        (fn? f)
        (instance? MultiFn f))))

(defn inner-class-name [^Class cls]
  (string/replace (.getName cls) #".*\$(.+)" "$1"))

(defn map-keyword-names [coll]
  (reduce
    (fn [v x]
      ;; Include fully qualified versions of core vars for matching vars in
      ;; macroexpanded forms
      (cond (symbol? x) (if-let [m (meta (resolve x))]
                          (conj v
                                (str (:name m))
                                (str (:ns m) \/ (:name m)))
                          (conj v (str x)))
            (nil? x) (conj v "nil")
            :else (conj v (str x))))
    [] coll))

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
  '#{def if do let quote var fn loop recur throw try catch finally
     monitor-enter monitor-exit . new set!})

(def keyword-groups
  "Special forms, constants, and every public var in clojure.core listed by
   syntax group suffix."
  (let [builtins [["Constant" '#{nil}]
                  ["Boolean" '#{true false}]
                  ["Special" special-forms]
                  ;; These are duplicates from special-forms
                  ["Exception" '#{throw try catch finally}]
                  ["Cond" '#{case cond cond-> cond->> condp if-let if-not when
                             when-first when-let when-not}]
                  ;; Imperative looping constructs (not sequence functions)
                  ["Repeat" '#{doall dorun doseq dotimes while}]]
        coresyms (set/difference (set (keys (ns-publics 'clojure.core)))
                                 (set (mapcat peek builtins)))
        group-preds [["Define" #(re-seq #"\Adef(?!ault)" (str %))]
                     ["Macro" #(:macro (meta (ns-resolve 'clojure.core %)))]
                     ["Func" #(fn-var? (ns-resolve 'clojure.core %))]
                     ["Variable" identity]]]
    (first
      (reduce
        (fn [[v syms] [group pred]]
          (let [group-syms (set (filterv pred syms))]
            [(conj v [group group-syms])
             (set/difference syms group-syms)]))
        [builtins coresyms] group-preds))))

(def character-properties
  "Character property names derived via reflection."
  (let [props (->> (get-private-field Pattern$CharPropertyNames "map")
                   (mapv (fn [[prop field]] [(inner-class-name (class field)) prop]))
                   (group-by first)
                   (reduce (fn [m [k v]] (assoc m k (mapv peek v))) {}))
        binary (concat (map #(.name ^UnicodeProp %) (get-private-field UnicodeProp "$VALUES"))
                       (keys (get-private-field UnicodeProp "aliases")))
        script (concat (map #(.name ^Character$UnicodeScript %) (Character$UnicodeScript/values))
                       (keys (get-private-field Character$UnicodeScript "aliases")))
        block (keys (get-private-field Character$UnicodeBlock "map"))]
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

(def lispwords
  "Specially indented symbols in clojure.core and clojure.test. Please read
   the commit message tagged `lispwords-guidelines` when adding new words to
   this list."
  (set/union
    ;; Definitions
    '#{bound-fn def definline definterface defmacro defmethod defmulti defn
       defn- defonce defprotocol defrecord defstruct deftest deftest- deftype
       extend extend-protocol extend-type fn ns proxy reify set-test}
    ;; Binding forms
    '#{as-> binding doall dorun doseq dotimes doto for if-let let letfn
       locking loop testing when-first when-let with-bindings with-in-str
       with-local-vars with-open with-precision with-redefs with-redefs-fn
       with-test}
    ;; Conditional branching
    '#{case cond-> cond->> condp if if-not when when-not while}
    ;; Exception handling
    '#{catch}))

;;
;; Vimscript literals
;;

(def vim-keywords
  "Vimscript literal `syntax keyword` for important identifiers."
  (->> keyword-groups
       (map (fn [[group keywords]]
              (format "syntax match clojure%s \"\\v<%s>\"\n"
                      group
                      (vim-frak-pattern (map-keyword-names keywords)))))
       string/join))

(def vim-completion-words
  "Vimscript literal list of words for omnifunc completion."
  (->> 'clojure.core
       ns-publics
       keys
       (concat special-forms)
       (map #(str \" % \"))
       sort
       (string/join \,)
       (format "let s:words = [%s]\n")))

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

(def vim-lispwords
  "Vimscript literal `setlocal lispwords=` statement."
  (str "setlocal lispwords=" (string/join \, (sort lispwords)) "\n"))

(def vim-top-cluster
  "Vimscript literal `syntax cluster` for all top-level syntax groups."
  (->> "../syntax/clojure.vim"
       slurp
       (re-seq #"syntax\s+(?:keyword|match|region)\s+(\S+)(?!.*\bcontained\b)")
       (map peek)
       sort
       distinct
       (string/join \,)
       (format "syntax cluster clojureTop contains=@Spell,%s\n")))

(comment
  ;; Generate the vim literal definitions for pasting into the runtime files.
  (spit "tmp/clojure-defs.vim"
        (str generation-comment
             clojure-version-comment
             vim-keywords
             \newline
             generation-comment
             java-version-comment
             vim-posix-char-classes
             vim-java-char-classes
             vim-unicode-binary-char-classes
             vim-unicode-category-char-classes
             vim-unicode-script-char-classes
             vim-unicode-block-char-classes
             \newline
             generation-comment
             vim-top-cluster
             \newline
             generation-comment
             vim-lispwords
             \newline
             generation-comment
             clojure-version-comment
             vim-completion-words))

  ;; Generate an example file with all possible character property literals.
  (spit "tmp/all-char-props.clj"
        comprehensive-clojure-character-property-regexps)

  ;; Performance test: `syntax keyword` vs `syntax match`
  (vim-clojure-static.test/benchmark
    1000 "tmp/bench.clj" (str keyword-groups)
    ;; `syntax keyword`
    (->> keyword-groups
         (map (fn [[group keywords]]
                (format "syntax keyword clojure%s %s\n"
                        group
                        (string/join \space (sort (map-keyword-names keywords))))))
         (map string/trim-newline)
         (string/join " | "))
    ;; Naive `syntax match`
    (->> keyword-groups
         (map (fn [[group keywords]]
                (format "syntax match clojure%s \"\\V\\<%s\\>\"\n"
                        group
                        (string/join "\\|" (map-keyword-names keywords)))))
         (map string/trim-newline)
         (string/join " | "))
    ;; Frak-optimized `syntax match`
    (->> keyword-groups
         (map (fn [[group keywords]]
                (format "syntax match clojure%s \"\\v<%s>\"\n"
                        group
                        (vim-frak-pattern (map-keyword-names keywords)))))
         (map string/trim-newline)
         (string/join " | ")))
  )
