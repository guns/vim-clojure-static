;; Authors: Sung Pae <self@sungpae.com>
;;          Joel Holdbrooks <cjholdbrooks@gmail.com>

(ns vim-clojure-static.generate
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.set :as set]
            [clojure.string :as string]
            [frak :as f])
  (:import (clojure.lang MultiFn)
           (java.lang Character$UnicodeBlock Character$UnicodeScript)
           (java.lang.reflect Field)
           (java.text SimpleDateFormat)
           (java.util Date)
           (java.util.regex Pattern Pattern$CharPropertyNames UnicodeProp)))

;;
;; Helpers
;;

(defn- vim-frak-pattern
  "Create a non-capturing regular expression pattern compatible with Vim."
  [strs]
  (-> (f/string-pattern strs {:escape-chars :vim})
      (string/replace #"\(\?:" "\\%\\(")))

(defn- property-pattern
  "Vimscript very magic pattern for a character property class."
  ([s] (property-pattern s true))
  ([s braces?]
   (if braces?
     (format "\\v\\\\[pP]\\{%s\\}" s)
     (format "\\v\\\\[pP]%s" s))))

(defn- syntax-match-properties
  "Vimscript literal `syntax match` for a character property class."
  ([group fmt props] (syntax-match-properties group fmt props true))
  ([group fmt props braces?]
   (format "syntax match %s \"%s\" contained display\n"
           (name group)
           (property-pattern (format fmt (vim-frak-pattern props)) braces?))))

(defn- get-private-field
  "Violate encapsulation and get the value of a private field."
  [^Class cls fieldname]
  (let [^Field field (first (filter #(= fieldname (.getName ^Field %))
                                    (.getDeclaredFields cls)))]
    (.setAccessible field true)
    (.get field field)))

(defn- fn-var? [v]
  (let [f @v]
    (or (contains? (meta v) :arglists)
        (fn? f)
        (instance? MultiFn f))))

(defn- inner-class-name [^Class cls]
  (string/replace (.getName cls) #".*\$(.+)" "$1"))

(defn- map-keyword-names [coll]
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

(defn- vim-top-cluster
  "Generate a Vimscript literal `syntax cluster` statement for `groups` and
   all top-level syntax groups in the given syntax buffer."
  [groups syntax-buf]
  (->> syntax-buf
       (re-seq #"syntax\s+(?:keyword|match|region)\s+(\S+)(?!.*\bcontained\b)")
       (map peek)
       (concat groups)
       sort
       distinct
       (string/join \,)
       (format "syntax cluster clojureTop contains=@Spell,%s\n")))

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
  "Special forms, constants, and every public var in clojure.core keyed by
   syntax group name."
  (let [exceptions '#{throw try catch finally}
        builtins {"clojureConstant" '#{nil}
                  "clojureBoolean" '#{true false}
                  "clojureSpecial" (apply disj special-forms exceptions)
                  "clojureException" exceptions
                  "clojureCond" '#{case cond cond-> cond->> condp if-let
                                   if-not if-some when when-first when-let
                                   when-not when-some}
                  ;; Imperative looping constructs (not sequence functions)
                  "clojureRepeat" '#{doseq dotimes while}}
        coresyms (set/difference
                   (set/union
                     (set (keys (ns-publics 'clojure.core)))
                     (set (keys (ns-publics 'clojure.test))))
                   (set (mapcat peek builtins)))
        group-preds [["clojureDefine" #(re-seq #"\Adef(?!ault)" (str %))]
                     ["clojureMacro" #(:macro (meta (ns-resolve 'clojure.core %)))]
                     ["clojureFunc" #(fn-var? (or (ns-resolve 'clojure.core %)
                                                  (ns-resolve 'clojure.test %)))]
                     ["clojureVariable" identity]]]
    (first
      (reduce
        (fn [[m syms] [group pred]]
          (let [group-syms (set (filterv pred syms))]
            [(assoc m group group-syms)
             (set/difference syms group-syms)]))
        [builtins coresyms] group-preds))))

(def character-properties
  "Character property names derived via reflection."
  (let [props (->> (get-private-field Pattern$CharPropertyNames "map")
                   (mapv (fn [[prop field]] [(inner-class-name (class field)) prop]))
                   (group-by first)
                   (reduce-kv (fn [m k v] (assoc m k (mapv peek v))) {}))
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
  "Specially indented symbols in clojure.core and clojure.test. The following
   commit message (tag `lispwords-guidelines`) outlines a convention:

   commit c2920f43191ae48084cea2c641a42ca8d34381f5
   Author: guns <self@sungpae.com>
   Date:   Sat Jan 26 06:53:14 2013 -0600

       Update lispwords

       Besides expanding the definitions into an easily maintainable style, we
       update the set of words for Clojure 1.5 using a simple rule:

           A function should only be indented specially if its first argument
           is special.

       This generally includes:

           * Definitions
           * Binding forms
           * Constructs that branch from a predicate

       What it does not include are functions/macros that accept a flat list of
       arguments (arglist: [& body]). Omissions include:

           clojure.core/dosync                       [& exprs]
           clojure.core/future                       [& body]
           clojure.core/gen-class                    [& options]
           clojure.core/gen-interface                [& options]
           clojure.core/with-out-str                 [& body]

       Also added some symbols from clojure.test, since this namespace is
       present in many projects.

       Interestingly, clojure-mode.el includes \"assoc\" and \"struct-map\" in the
       special indent list, which makes a good deal of sense:

         (assoc my-map
           :foo \"foo\"
           :bar \"bar\")

       If we were to include this in lispwords, the following functions/macros
       should also be considered since they also take optional key value pairs
       at the end of the arglist:

           clojure.core/agent                        [state & options]
           clojure.core/assoc                        … [map key val & kvs]
           clojure.core/assoc!                       … [coll key val & kvs]
           clojure.core/atom                         … [x & options]
           clojure.core/ref                          [x] [x & options]
           clojure.core/refer                        [ns-sym & filters]
           clojure.core/restart-agent                [a new-state & options]
           clojure.core/slurp                        [f & opts]
           clojure.core/sorted-map-by                [comparator & keyvals]
           clojure.core/spit                         [f content & options]
           clojure.core/struct-map                   [s & inits]"
  (set/union
    ;; Definitions
    '#{bound-fn def definline definterface defmacro defmethod defmulti defn
       defn- defonce defprotocol defrecord defstruct deftest deftest- deftype
       extend extend-protocol extend-type fn ns proxy reify set-test}
    ;; Binding forms
    '#{as-> binding doseq dotimes doto for if-let if-some let letfn locking
       loop testing when-first when-let when-some with-bindings with-in-str
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
  "Vimscript literal dictionary of important identifiers."
  (->> keyword-groups
       sort
       (map (fn [[group keywords]]
              (->> keywords
                   map-keyword-names
                   sort
                   (map pr-str)
                   (string/join \,)
                   (format "'%s': [%s]" group))))
       (string/join "\n    \\ , ")
       (format "let s:clojure_syntax_keywords = {\n    \\   %s\n    \\ }\n")))

(def vim-completion-words
  "Vimscript literal list of words for omnifunc completion."
  (->> ['clojure.core 'clojure.test]
       (mapcat ns-publics)
       keys
       (concat special-forms)
       (map (comp pr-str str))
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

(def vim-lispwords
  "Vimscript literal `setlocal lispwords=` statement."
  (str "setlocal lispwords=" (string/join \, (sort lispwords)) "\n"))

(defn- comprehensive-clojure-character-property-regexps []
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

;;
;; Update functions
;;

(def ^:private CLOJURE-SECTION
  #"(?ms)^CLOJURE.*?(?=^[\p{Lu} ]+\t*\*)")

(defn- fjoin [& args]
  (string/join \/ args))

(defn- qstr [& xs]
  (string/replace (string/join xs) "\\" "\\\\"))

(defn- update-doc! [first-line-pattern src-file dst-file]
  (let [sbuf (with-open [rdr (io/reader src-file)]
               (->> rdr
                    line-seq
                    (drop-while #(not (re-find first-line-pattern %)))
                    (string/join \newline)))
        dbuf (slurp dst-file)
        dmatch (re-find CLOJURE-SECTION dbuf)
        hunk (re-find CLOJURE-SECTION sbuf)]
    (spit dst-file (string/replace-first dbuf dmatch hunk))))

(defn- copy-runtime-files! [src dst & opts]
  (let [{:keys [tag date paths]} (apply hash-map opts)]
    (doseq [path paths
            :let [buf (-> (fjoin src path)
                          slurp
                          (string/replace "%%RELEASE_TAG%%" tag)
                          (string/replace "%%RELEASE_DATE%%" date))]]
      (spit (fjoin dst "runtime" path) buf))))

(defn- project-replacements [dir]
  {(fjoin dir "syntax/clojure.vim")
   {"-*- KEYWORDS -*-"
    (qstr generation-comment
          clojure-version-comment
          vim-keywords)
    "-*- CHARACTER PROPERTY CLASSES -*-"
    (qstr generation-comment
          java-version-comment
          vim-posix-char-classes
          vim-java-char-classes
          vim-unicode-binary-char-classes
          vim-unicode-category-char-classes
          vim-unicode-script-char-classes
          vim-unicode-block-char-classes)
    "-*- TOP CLUSTER -*-"
    (qstr generation-comment
          (vim-top-cluster (keys keyword-groups)
                           (slurp (fjoin dir "syntax/clojure.vim"))))}

   (fjoin dir "ftplugin/clojure.vim")
   {"-*- LISPWORDS -*-"
    (qstr generation-comment
          vim-lispwords)}

   (fjoin dir "autoload/clojurecomplete.vim")
   {"-*- COMPLETION WORDS -*-"
    (qstr generation-comment
          clojure-version-comment
          vim-completion-words)}})

(defn- update-project!
  "Update project runtime files in the given directory."
  [dir]
  (doseq [[file replacements] (project-replacements dir)]
    (doseq [[magic-comment replacement] replacements]
      (let [buf (slurp file)
            pat (Pattern/compile (str "(?s)\\Q" magic-comment "\\E\\n.*?\\n\\n"))
            rep (str magic-comment "\n" replacement "\n")
            buf' (string/replace buf pat rep)]
        (if (= buf buf')
          (printf "No changes: %s\n" magic-comment)
          (do (printf "Updating %s\n" magic-comment)
              (spit file buf')))))))

(defn- update-vim!
  "Update Vim repository runtime files in dst/runtime"
  [src dst]
  (let [current-tag (string/trim-newline (:out (sh "git" "tag" "--points-at" "HEAD")))
        current-date (.format (SimpleDateFormat. "dd MMMM YYYY") (Date.))]
    (assert (seq current-tag) "Git HEAD is not tagged!")
    (update-doc! #"CLOJURE\t*\*ft-clojure-indent\*"
                 (fjoin src "doc/clojure.txt")
                 (fjoin dst "runtime/doc/indent.txt"))
    (update-doc! #"CLOJURE\t*\*ft-clojure-syntax\*"
                 (fjoin src "doc/clojure.txt")
                 (fjoin dst "runtime/doc/syntax.txt"))
    (copy-runtime-files! src dst
                         :tag current-tag
                         :date current-date
                         :paths ["autoload/clojurecomplete.vim"
                                 "ftplugin/clojure.vim"
                                 "indent/clojure.vim"
                                 "syntax/clojure.vim"])))

(comment
  ;; Run this to update the project files
  (update-project! "..")

  ;; Run this to update a vim repository
  (update-vim! ".." "../../vim")

  ;; Generate an example file with all possible character property literals.
  (spit "tmp/all-char-props.clj"
        (comprehensive-clojure-character-property-regexps))

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
