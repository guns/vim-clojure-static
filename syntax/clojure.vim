" Vim syntax file
" Language:     Clojure
" Authors:      Toralf Wittner <toralf.wittner@gmail.com>
"               modified by Meikel Brandmeyer <mb@kotka.de>
" URL:          http://kotka.de/projects/clojure/vimclojure.html
"
" Contributors: Joel Holdbrooks <cjholdbrooks@gmail.com> (Regexp support, bug fixes)
"
" Maintainer:   Sung Pae <self@sungpae.com>
" URL:          https://github.com/guns/vim-clojure-static
" License:      Same as Vim
" Last Change:  09 March 2013

if exists("b:current_syntax")
    finish
endif

" Generated from https://github.com/guns/vim-clojure-static/blob/vim-release-004/clj/src/vim_clojure_static/generate.clj
" Clojure version 1.5.1
syntax keyword clojureConstant nil
syntax keyword clojureBoolean false true
syntax keyword clojureSpecial . catch clojure.core/fn clojure.core/let clojure.core/loop def do finally fn if let loop monitor-enter monitor-exit new quote recur set! throw try var
syntax keyword clojureException catch finally throw try
syntax keyword clojureCond case clojure.core/case clojure.core/cond clojure.core/cond-> clojure.core/cond->> clojure.core/condp clojure.core/if-let clojure.core/if-not clojure.core/when clojure.core/when-first clojure.core/when-let clojure.core/when-not cond cond-> cond->> condp if-let if-not when when-first when-let when-not
syntax keyword clojureRepeat clojure.core/doall clojure.core/dorun clojure.core/doseq clojure.core/dotimes clojure.core/while doall dorun doseq dotimes while
syntax keyword clojureDefine clojure.core/definline clojure.core/definterface clojure.core/defmacro clojure.core/defmethod clojure.core/defmulti clojure.core/defn clojure.core/defn- clojure.core/defonce clojure.core/defprotocol clojure.core/defrecord clojure.core/defstruct clojure.core/deftype definline definterface defmacro defmethod defmulti defn defn- defonce defprotocol defrecord defstruct deftype
syntax keyword clojureMacro -> ->> .. amap and areduce as-> assert binding bound-fn clojure.core/-> clojure.core/->> clojure.core/.. clojure.core/amap clojure.core/and clojure.core/areduce clojure.core/as-> clojure.core/assert clojure.core/binding clojure.core/bound-fn clojure.core/comment clojure.core/declare clojure.core/delay clojure.core/dosync clojure.core/doto clojure.core/extend-protocol clojure.core/extend-type clojure.core/for clojure.core/future clojure.core/gen-class clojure.core/gen-interface clojure.core/import clojure.core/io! clojure.core/lazy-cat clojure.core/lazy-seq clojure.core/letfn clojure.core/locking clojure.core/memfn clojure.core/ns clojure.core/or clojure.core/proxy clojure.core/proxy-super clojure.core/pvalues clojure.core/refer-clojure clojure.core/reify clojure.core/some-> clojure.core/some->> clojure.core/sync clojure.core/time clojure.core/with-bindings clojure.core/with-in-str clojure.core/with-loading-context clojure.core/with-local-vars clojure.core/with-open clojure.core/with-out-str clojure.core/with-precision clojure.core/with-redefs comment declare delay dosync doto extend-protocol extend-type for future gen-class gen-interface import io! lazy-cat lazy-seq letfn locking memfn ns or proxy proxy-super pvalues refer-clojure reify some-> some->> sync time with-bindings with-in-str with-loading-context with-local-vars with-open with-out-str with-precision with-redefs
syntax keyword clojureFunc * *' + +' - -' ->ArrayChunk ->Vec ->VecNode ->VecSeq -cache-protocol-fn -reset-methods / < <= = == > >= accessor aclone add-classpath add-watch agent agent-error agent-errors aget alength alias all-ns alter alter-meta! alter-var-root ancestors apply array-map aset aset-boolean aset-byte aset-char aset-double aset-float aset-int aset-long aset-short assoc assoc! assoc-in associative? atom await await-for await1 bases bean bigdec bigint biginteger bit-and bit-and-not bit-clear bit-flip bit-not bit-or bit-set bit-shift-left bit-shift-right bit-test bit-xor boolean boolean-array booleans bound-fn* bound? butlast byte byte-array bytes cast char char-array char? chars chunk chunk-append chunk-buffer chunk-cons chunk-first chunk-next chunk-rest chunked-seq? class class? clear-agent-errors clojure-version clojure.core/* clojure.core/*' clojure.core/+ clojure.core/+' clojure.core/- clojure.core/-' clojure.core/->ArrayChunk clojure.core/->Vec clojure.core/->VecNode clojure.core/->VecSeq clojure.core/-cache-protocol-fn clojure.core/-reset-methods clojure.core// clojure.core/< clojure.core/<= clojure.core/= clojure.core/== clojure.core/> clojure.core/>= clojure.core/accessor clojure.core/aclone clojure.core/add-classpath clojure.core/add-watch clojure.core/agent clojure.core/agent-error clojure.core/agent-errors clojure.core/aget clojure.core/alength clojure.core/alias clojure.core/all-ns clojure.core/alter clojure.core/alter-meta! clojure.core/alter-var-root clojure.core/ancestors clojure.core/apply clojure.core/array-map clojure.core/aset clojure.core/aset-boolean clojure.core/aset-byte clojure.core/aset-char clojure.core/aset-double clojure.core/aset-float clojure.core/aset-int clojure.core/aset-long clojure.core/aset-short clojure.core/assoc clojure.core/assoc! clojure.core/assoc-in clojure.core/associative? clojure.core/atom clojure.core/await clojure.core/await-for clojure.core/await1 clojure.core/bases clojure.core/bean clojure.core/bigdec clojure.core/bigint clojure.core/biginteger clojure.core/bit-and clojure.core/bit-and-not clojure.core/bit-clear clojure.core/bit-flip clojure.core/bit-not clojure.core/bit-or clojure.core/bit-set clojure.core/bit-shift-left clojure.core/bit-shift-right clojure.core/bit-test clojure.core/bit-xor clojure.core/boolean clojure.core/boolean-array clojure.core/booleans clojure.core/bound-fn* clojure.core/bound? clojure.core/butlast clojure.core/byte clojure.core/byte-array clojure.core/bytes clojure.core/cast clojure.core/char clojure.core/char-array clojure.core/char? clojure.core/chars clojure.core/chunk clojure.core/chunk-append clojure.core/chunk-buffer clojure.core/chunk-cons clojure.core/chunk-first clojure.core/chunk-next clojure.core/chunk-rest clojure.core/chunked-seq? clojure.core/class clojure.core/class? clojure.core/clear-agent-errors clojure.core/clojure-version clojure.core/coll? clojure.core/commute clojure.core/comp clojure.core/comparator clojure.core/compare clojure.core/compare-and-set! clojure.core/compile clojure.core/complement clojure.core/concat clojure.core/conj clojure.core/conj! clojure.core/cons clojure.core/constantly clojure.core/construct-proxy clojure.core/contains? clojure.core/count clojure.core/counted? clojure.core/create-ns clojure.core/create-struct clojure.core/cycle clojure.core/dec clojure.core/dec' clojure.core/decimal? clojure.core/delay? clojure.core/deliver clojure.core/denominator clojure.core/deref clojure.core/derive clojure.core/descendants clojure.core/destructure clojure.core/disj clojure.core/disj! clojure.core/dissoc clojure.core/dissoc! clojure.core/distinct clojure.core/distinct? clojure.core/double clojure.core/double-array clojure.core/doubles clojure.core/drop clojure.core/drop-last clojure.core/drop-while clojure.core/empty clojure.core/empty? clojure.core/ensure clojure.core/enumeration-seq clojure.core/error-handler clojure.core/error-mode clojure.core/eval clojure.core/even? clojure.core/every-pred clojure.core/every? clojure.core/ex-data clojure.core/ex-info clojure.core/extend clojure.core/extenders clojure.core/extends? clojure.core/false? clojure.core/ffirst clojure.core/file-seq clojure.core/filter clojure.core/filterv clojure.core/find clojure.core/find-keyword clojure.core/find-ns clojure.core/find-protocol-impl clojure.core/find-protocol-method clojure.core/find-var clojure.core/first clojure.core/flatten clojure.core/float clojure.core/float-array clojure.core/float? clojure.core/floats clojure.core/flush clojure.core/fn? clojure.core/fnext clojure.core/fnil clojure.core/force clojure.core/format clojure.core/frequencies clojure.core/future-call clojure.core/future-cancel clojure.core/future-cancelled? clojure.core/future-done? clojure.core/future? clojure.core/gensym clojure.core/get clojure.core/get-in clojure.core/get-method clojure.core/get-proxy-class clojure.core/get-thread-bindings clojure.core/get-validator clojure.core/group-by clojure.core/hash clojure.core/hash-combine clojure.core/hash-map clojure.core/hash-set clojure.core/identical? clojure.core/identity clojure.core/ifn? clojure.core/in-ns clojure.core/inc clojure.core/inc' clojure.core/init-proxy clojure.core/instance? clojure.core/int clojure.core/int-array clojure.core/integer? clojure.core/interleave clojure.core/intern clojure.core/interpose clojure.core/into clojure.core/into-array clojure.core/ints clojure.core/isa? clojure.core/iterate clojure.core/iterator-seq clojure.core/juxt clojure.core/keep clojure.core/keep-indexed clojure.core/key clojure.core/keys clojure.core/keyword clojure.core/keyword? clojure.core/last clojure.core/line-seq clojure.core/list clojure.core/list* clojure.core/list? clojure.core/load clojure.core/load-file clojure.core/load-reader clojure.core/load-string clojure.core/loaded-libs clojure.core/long clojure.core/long-array clojure.core/longs clojure.core/macroexpand clojure.core/macroexpand-1 clojure.core/make-array clojure.core/make-hierarchy clojure.core/map clojure.core/map-indexed clojure.core/map? clojure.core/mapcat clojure.core/mapv clojure.core/max clojure.core/max-key clojure.core/memoize clojure.core/merge clojure.core/merge-with clojure.core/meta clojure.core/method-sig clojure.core/methods clojure.core/min clojure.core/min-key clojure.core/mod clojure.core/munge clojure.core/name clojure.core/namespace clojure.core/namespace-munge clojure.core/neg? clojure.core/newline clojure.core/next clojure.core/nfirst clojure.core/nil? clojure.core/nnext clojure.core/not clojure.core/not-any? clojure.core/not-empty clojure.core/not-every? clojure.core/not= clojure.core/ns-aliases clojure.core/ns-imports clojure.core/ns-interns clojure.core/ns-map clojure.core/ns-name clojure.core/ns-publics clojure.core/ns-refers clojure.core/ns-resolve clojure.core/ns-unalias clojure.core/ns-unmap clojure.core/nth clojure.core/nthnext clojure.core/nthrest clojure.core/num clojure.core/number? clojure.core/numerator clojure.core/object-array clojure.core/odd? clojure.core/parents clojure.core/partial clojure.core/partition clojure.core/partition-all clojure.core/partition-by clojure.core/pcalls clojure.core/peek clojure.core/persistent! clojure.core/pmap clojure.core/pop clojure.core/pop! clojure.core/pop-thread-bindings clojure.core/pos? clojure.core/pr clojure.core/pr-str clojure.core/prefer-method clojure.core/prefers clojure.core/print clojure.core/print-ctor clojure.core/print-simple clojure.core/print-str clojure.core/printf clojure.core/println clojure.core/println-str clojure.core/prn clojure.core/prn-str clojure.core/promise clojure.core/proxy-call-with-super clojure.core/proxy-mappings clojure.core/proxy-name clojure.core/push-thread-bindings clojure.core/quot clojure.core/rand clojure.core/rand-int clojure.core/rand-nth clojure.core/range clojure.core/ratio? clojure.core/rational? clojure.core/rationalize clojure.core/re-find clojure.core/re-groups clojure.core/re-matcher clojure.core/re-matches clojure.core/re-pattern clojure.core/re-seq clojure.core/read clojure.core/read-line clojure.core/read-string clojure.core/realized? clojure.core/reduce clojure.core/reduce-kv clojure.core/reduced clojure.core/reduced? clojure.core/reductions clojure.core/ref clojure.core/ref-history-count clojure.core/ref-max-history clojure.core/ref-min-history clojure.core/ref-set clojure.core/refer clojure.core/release-pending-sends clojure.core/rem clojure.core/remove clojure.core/remove-all-methods clojure.core/remove-method clojure.core/remove-ns clojure.core/remove-watch clojure.core/repeat clojure.core/repeatedly clojure.core/replace clojure.core/replicate clojure.core/require clojure.core/reset! clojure.core/reset-meta! clojure.core/resolve clojure.core/rest clojure.core/restart-agent clojure.core/resultset-seq clojure.core/reverse clojure.core/reversible? clojure.core/rseq clojure.core/rsubseq clojure.core/satisfies? clojure.core/second clojure.core/select-keys clojure.core/send clojure.core/send-off clojure.core/send-via clojure.core/seq clojure.core/seq? clojure.core/seque clojure.core/sequence clojure.core/sequential? clojure.core/set clojure.core/set-agent-send-executor! clojure.core/set-agent-send-off-executor! clojure.core/set-error-handler! clojure.core/set-error-mode! clojure.core/set-validator! clojure.core/set? clojure.core/short clojure.core/short-array clojure.core/shorts clojure.core/shuffle clojure.core/shutdown-agents clojure.core/slurp clojure.core/some clojure.core/some-fn clojure.core/sort clojure.core/sort-by clojure.core/sorted-map clojure.core/sorted-map-by clojure.core/sorted-set clojure.core/sorted-set-by clojure.core/sorted? clojure.core/special-symbol? clojure.core/spit clojure.core/split-at clojure.core/split-with clojure.core/str clojure.core/string? clojure.core/struct clojure.core/struct-map clojure.core/subs clojure.core/subseq clojure.core/subvec clojure.core/supers clojure.core/swap! clojure.core/symbol clojure.core/symbol? clojure.core/take clojure.core/take-last clojure.core/take-nth clojure.core/take-while clojure.core/test clojure.core/the-ns clojure.core/thread-bound? clojure.core/to-array clojure.core/to-array-2d clojure.core/trampoline clojure.core/transient clojure.core/tree-seq clojure.core/true? clojure.core/type clojure.core/unchecked-add clojure.core/unchecked-add-int clojure.core/unchecked-byte clojure.core/unchecked-char clojure.core/unchecked-dec clojure.core/unchecked-dec-int clojure.core/unchecked-divide-int clojure.core/unchecked-double clojure.core/unchecked-float clojure.core/unchecked-inc clojure.core/unchecked-inc-int clojure.core/unchecked-int clojure.core/unchecked-long clojure.core/unchecked-multiply clojure.core/unchecked-multiply-int clojure.core/unchecked-negate clojure.core/unchecked-negate-int clojure.core/unchecked-remainder-int clojure.core/unchecked-short clojure.core/unchecked-subtract clojure.core/unchecked-subtract-int clojure.core/underive clojure.core/update-in clojure.core/update-proxy clojure.core/use clojure.core/val clojure.core/vals clojure.core/var-get clojure.core/var-set clojure.core/var? clojure.core/vary-meta clojure.core/vec clojure.core/vector clojure.core/vector-of clojure.core/vector? clojure.core/with-bindings* clojure.core/with-meta clojure.core/with-redefs-fn clojure.core/xml-seq clojure.core/zero? clojure.core/zipmap coll? commute comp comparator compare compare-and-set! compile complement concat conj conj! cons constantly construct-proxy contains? count counted? create-ns create-struct cycle dec dec' decimal? delay? deliver denominator deref derive descendants destructure disj disj! dissoc dissoc! distinct distinct? double double-array doubles drop drop-last drop-while empty empty? ensure enumeration-seq error-handler error-mode eval even? every-pred every? ex-data ex-info extend extenders extends? false? ffirst file-seq filter filterv find find-keyword find-ns find-protocol-impl find-protocol-method find-var first flatten float float-array float? floats flush fn? fnext fnil force format frequencies future-call future-cancel future-cancelled? future-done? future? gensym get get-in get-method get-proxy-class get-thread-bindings get-validator group-by hash hash-combine hash-map hash-set identical? identity ifn? in-ns inc inc' init-proxy instance? int int-array integer? interleave intern interpose into into-array ints isa? iterate iterator-seq juxt keep keep-indexed key keys keyword keyword? last line-seq list list* list? load load-file load-reader load-string loaded-libs long long-array longs macroexpand macroexpand-1 make-array make-hierarchy map map-indexed map? mapcat mapv max max-key memoize merge merge-with meta method-sig methods min min-key mod munge name namespace namespace-munge neg? newline next nfirst nil? nnext not not-any? not-empty not-every? not= ns-aliases ns-imports ns-interns ns-map ns-name ns-publics ns-refers ns-resolve ns-unalias ns-unmap nth nthnext nthrest num number? numerator object-array odd? parents partial partition partition-all partition-by pcalls peek persistent! pmap pop pop! pop-thread-bindings pos? pr pr-str prefer-method prefers print print-ctor print-simple print-str printf println println-str prn prn-str promise proxy-call-with-super proxy-mappings proxy-name push-thread-bindings quot rand rand-int rand-nth range ratio? rational? rationalize re-find re-groups re-matcher re-matches re-pattern re-seq read read-line read-string realized? reduce reduce-kv reduced reduced? reductions ref ref-history-count ref-max-history ref-min-history ref-set refer release-pending-sends rem remove remove-all-methods remove-method remove-ns remove-watch repeat repeatedly replace replicate require reset! reset-meta! resolve rest restart-agent resultset-seq reverse reversible? rseq rsubseq satisfies? second select-keys send send-off send-via seq seq? seque sequence sequential? set set-agent-send-executor! set-agent-send-off-executor! set-error-handler! set-error-mode! set-validator! set? short short-array shorts shuffle shutdown-agents slurp some some-fn sort sort-by sorted-map sorted-map-by sorted-set sorted-set-by sorted? special-symbol? spit split-at split-with str string? struct struct-map subs subseq subvec supers swap! symbol symbol? take take-last take-nth take-while test the-ns thread-bound? to-array to-array-2d trampoline transient tree-seq true? type unchecked-add unchecked-add-int unchecked-byte unchecked-char unchecked-dec unchecked-dec-int unchecked-divide-int unchecked-double unchecked-float unchecked-inc unchecked-inc-int unchecked-int unchecked-long unchecked-multiply unchecked-multiply-int unchecked-negate unchecked-negate-int unchecked-remainder-int unchecked-short unchecked-subtract unchecked-subtract-int underive update-in update-proxy use val vals var-get var-set var? vary-meta vec vector vector-of vector? with-bindings* with-meta with-redefs-fn xml-seq zero? zipmap
syntax keyword clojureVariable *1 *2 *3 *agent* *allow-unresolved-vars* *assert* *clojure-version* *command-line-args* *compile-files* *compile-path* *compiler-options* *data-readers* *default-data-reader-fn* *e *err* *file* *flush-on-newline* *fn-loader* *in* *math-context* *ns* *out* *print-dup* *print-length* *print-level* *print-meta* *print-readably* *read-eval* *source-path* *unchecked-math* *use-context-classloader* *verbose-defrecords* *warn-on-reflection* EMPTY-NODE char-escape-string char-name-string clojure.core/*1 clojure.core/*2 clojure.core/*3 clojure.core/*agent* clojure.core/*allow-unresolved-vars* clojure.core/*assert* clojure.core/*clojure-version* clojure.core/*command-line-args* clojure.core/*compile-files* clojure.core/*compile-path* clojure.core/*compiler-options* clojure.core/*data-readers* clojure.core/*default-data-reader-fn* clojure.core/*e clojure.core/*err* clojure.core/*file* clojure.core/*flush-on-newline* clojure.core/*fn-loader* clojure.core/*in* clojure.core/*math-context* clojure.core/*ns* clojure.core/*out* clojure.core/*print-dup* clojure.core/*print-length* clojure.core/*print-level* clojure.core/*print-meta* clojure.core/*print-readably* clojure.core/*read-eval* clojure.core/*source-path* clojure.core/*unchecked-math* clojure.core/*use-context-classloader* clojure.core/*verbose-defrecords* clojure.core/*warn-on-reflection* clojure.core/EMPTY-NODE clojure.core/char-escape-string clojure.core/char-name-string clojure.core/default-data-readers clojure.core/primitives-classnames clojure.core/print-dup clojure.core/print-method clojure.core/unquote clojure.core/unquote-splicing default-data-readers primitives-classnames print-dup print-method unquote unquote-splicing

" Keywords are symbols:
"   static Pattern symbolPat = Pattern.compile("[:]?([\\D&&[^/]].*/)?([\\D&&[^/]][^/]*)");
" But they:
"   * Must not end in a : or /
"   * Must not have two adjacent colons except at the beginning
"   * Must not contain any reader metacharacters except for ' and #
syntax match clojureKeyword "\v<:{1,2}%([^ \n\r\t()\[\]{}";@^`~\\%/]+/)*[^ \n\r\t()\[\]{}";@^`~\\%/]+:@<!>"

syntax match clojureStringEscape "\v\\%([\\btnfr"]|u\x{4}|[0-3]\o{2}|\o{1,2})" contained

syntax region clojureString start=/"/ skip=/\\\\\|\\"/ end=/"/ contains=clojureStringEscape

syntax match clojureCharacter "\\."
syntax match clojureCharacter "\\o\%([0-3]\o\{2\}\|\o\{1,2\}\)"
syntax match clojureCharacter "\\u\x\{4\}"
syntax match clojureCharacter "\\space"
syntax match clojureCharacter "\\tab"
syntax match clojureCharacter "\\newline"
syntax match clojureCharacter "\\return"
syntax match clojureCharacter "\\backspace"
syntax match clojureCharacter "\\formfeed"

syntax match clojureSymbol "\v%([a-zA-Z!$&*_+=|<.>?-]|[^\x00-\x7F])+%(:?%([a-zA-Z0-9!#$%&*_+=|'<.>/?-]|[^\x00-\x7F]))*[#:]@<!"

let s:radix_chars = "0123456789abcdefghijklmnopqrstuvwxyz"
for s:radix in range(2, 36)
    execute 'syntax match clojureNumber "\v\c<[-+]?' . s:radix . 'r[' . strpart(s:radix_chars, 0, s:radix) . ']+>"'
endfor
unlet! s:radix_chars s:radix

syntax match clojureNumber "\v<[-+]?%(0\o*|0x\x+|[1-9]\d*)N?>"
syntax match clojureNumber "\v<[-+]?%(0|[1-9]\d*|%(0|[1-9]\d*)\.\d*)%(M|[eE][-+]?\d+)?>"
syntax match clojureNumber "\v<[-+]?%(0|[1-9]\d*)/%(0|[1-9]\d*)>"

syntax match clojureVarArg "&"

syntax match clojureQuote "'"
syntax match clojureQuote "`"
syntax match clojureUnquote "\~"
syntax match clojureUnquote "\~@"
syntax match clojureMeta "\^"
syntax match clojureDeref "@"
syntax match clojureDispatch "\v#[\^'=<_]?"

" Clojure permits no more than 20 params.
syntax match clojureAnonArg "%\(20\|1\d\|[1-9]\|&\)\?"

syntax match   clojureRegexpEscape  "\v\\%(\\|[tnrfae]|c\u|0[0-3]?\o{1,2}|x%(\x{2}|\{\x{1,9}\})|u\x{4}|.)" contained display
syntax region  clojureRegexpQuoted  start=/\v\<@!\\Q/ms=e+1 skip=/\v\\\\|\\"/ end=/\\E/me=s-1 end=/"/me=s-1 contained
syntax region  clojureRegexpQuote   start=/\v\<@!\\Q/       skip=/\v\\\\|\\"/ end=/\\E/       end=/"/me=s-1 contains=clojureRegexpQuoted keepend contained
syntax cluster clojureRegexpEscapes contains=clojureRegexpEscape,clojureRegexpQuote

" Character classes
syntax match   clojureRegexpPredefinedCharClass "\v%(\\[dDsSwW]|\.)" contained display
syntax match   clojureRegexpPosixCharClass      "\v\\[pP]\{%(Alnum|Alpha|ASCII|Blank|Cntrl|Digit|Graph|Lower|Print|Punct|Space|Upper|XDigit)\}" contained display
" Generated from https://github.com/guns/vim-clojure-static/blob/vim-release-004/clj/src/vim_clojure_static/generate.clj
" Java version 1.7.0_17
syntax match   clojureRegexpJavaCharClass       "\v\\[pP]\{java%(Alphabetic|Defined|Digit|ISOControl|IdentifierIgnorable|Ideographic|JavaIdentifierPart|JavaIdentifierStart|Letter|LetterOrDigit|LowerCase|Mirrored|SpaceChar|TitleCase|UnicodeIdentifierPart|UnicodeIdentifierStart|UpperCase|Whitespace)\}" contained display
syntax match   clojureRegexpUnicodeCharClass    "\v\\[pP]\{Is%(Arabic|Armenian|Avestan|Balinese|Bamum|Batak|Bengali|Bopomofo|Brahmi|Buginese|Buhid|Carian|Cham|Cherokee|Coptic|Cuneiform|Cyrillic|Deseret|Devanagari|Egyptian_Hieroglyphs|Ethiopic|Georgian|Glagolitic|Gothic|Gujarati|Gurmukhi|Hanunoo|Hebrew|Hiragana|Imperial_Aramaic|Inscriptional_Pahlavi|Inscriptional_Parthian|Javanese|Kaithi|Kannada|Katakana|Kayah_Li|Kharoshthi|Khmer|Lao|Lepcha|Limbu|Lisu|Lycian|Lydian|Malayalam|Mandaic|Meetei_Mayek|Mongolian|Myanmar|NKo|New_Tai_Lue|Ogham|Ol_Chiki|Old_Italic|Old_Persian|Old_South_Arabian|Old_Turkic|Oriya|Osmanya|Phags_pa|Phoenician|Rejang|Runic|Samaritan|Saurashtra|Shavian|Sinhala|Sundanese|Syloti_Nagri|Syriac|Tagalog|Tagbanwa|Tai_Le|Tai_Tham|Tai_Viet|Tamil|Telugu|Thaana|Thai|Tibetan|Tifinagh|Ugaritic|Vai)\}" contained display
syntax match   clojureRegexpUnicodeCharClass    "\v\\[pP]\{In%(AegeanNumbers|Aegean_Numbers|AlchemicalSymbols|Alchemical_Symbols|AlphabeticPresentationForms|Alphabetic_Presentation_Forms|AncientGreekMusicalNotation|AncientGreekNumbers|AncientSymbols|Ancient_Greek_Musical_Notation|Ancient_Greek_Numbers|Ancient_Symbols|Arabic|ArabicPresentationForms-A|ArabicPresentationForms-B|ArabicSupplement|Arabic_Presentation_Forms_A|Arabic_Presentation_Forms_B|Arabic_Supplement|Armenian|Arrows|Avestan|Balinese|Bamum|BamumSupplement|Bamum_Supplement|BasicLatin|Basic_Latin|Batak|Bengali|BlockElements|Block_Elements|Bopomofo|BopomofoExtended|Bopomofo_Extended|BoxDrawing|Box_Drawing|Brahmi|BraillePatterns|Braille_Patterns|Buginese|Buhid|ByzantineMusicalSymbols|Byzantine_Musical_Symbols|CJKCompatibility|CJKCompatibilityForms|CJKCompatibilityIdeographs|CJKCompatibilityIdeographsSupplement|CJKRadicalsSupplement|CJKStrokes|CJKSymbolsandPunctuation|CJKUnifiedIdeographs|CJKUnifiedIdeographsExtensionA|CJKUnifiedIdeographsExtensionB|CJKUnifiedIdeographsExtensionC|CJKUnifiedIdeographsExtensionD|CJK_Compatibility|CJK_Compatibility_Forms|CJK_Compatibility_Ideographs|CJK_Compatibility_Ideographs_Supplement|CJK_Radicals_Supplement|CJK_Strokes|CJK_Symbols_and_Punctuation|CJK_Unified_Ideographs|CJK_Unified_Ideographs_Extension_A|CJK_Unified_Ideographs_Extension_B|CJK_Unified_Ideographs_Extension_C|CJK_Unified_Ideographs_Extension_D|Carian|Cham|Cherokee|CombiningDiacriticalMarks|CombiningDiacriticalMarksSupplement|CombiningDiacriticalMarksforSymbols|CombiningHalfMarks|Combining_Diacritical_Marks|Combining_Diacritical_Marks_Supplement|Combining_Half_Marks|CommonIndicNumberForms|Common_Indic_Number_Forms|ControlPictures|Control_Pictures|Coptic|CountingRodNumerals|Counting_Rod_Numerals|Cuneiform|CuneiformNumbersandPunctuation|Cuneiform_Numbers_and_Punctuation|CurrencySymbols|Currency_Symbols|CypriotSyllabary|Cypriot_Syllabary|Cyrillic|CyrillicExtended-A|CyrillicExtended-B|CyrillicSupplement|Cyrillic_Extended_A|Cyrillic_Extended_B|Deseret|Devanagari|DevanagariExtended|Devanagari_Extended|Dingbats|DominoTiles|Domino_Tiles|EgyptianHieroglyphs|Egyptian_Hieroglyphs|Emoticons|EnclosedAlphanumericSupplement|EnclosedAlphanumerics|EnclosedCJKLettersandMonths|EnclosedIdeographicSupplement|Enclosed_Alphanumeric_Supplement|Enclosed_Alphanumerics|Enclosed_CJK_Letters_and_Months|Enclosed_Ideographic_Supplement|Ethiopic|EthiopicExtended|EthiopicExtended-A|EthiopicSupplement|Ethiopic_Extended|Ethiopic_Extended_A|Ethiopic_Supplement|GeneralPunctuation|General_Punctuation|GeometricShapes|Geometric_Shapes|Georgian|GeorgianSupplement|Georgian_Supplement|Glagolitic|Gothic|GreekExtended|Greek_Extended|GreekandCoptic|Gujarati|Gurmukhi|Halfwidth_and_Fullwidth_Forms|HalfwidthandFullwidthForms|HangulCompatibilityJamo|HangulJamo|HangulJamoExtended-A|HangulJamoExtended-B|HangulSyllables|Hangul_Compatibility_Jamo|Hangul_Jamo|Hangul_Jamo_Extended_A|Hangul_Jamo_Extended_B|Hangul_Syllables|Hanunoo|Hebrew|HighPrivateUseSurrogates|HighSurrogates|High_Private_Use_Surrogates|High_Surrogates|Hiragana|IPAExtensions|IPA_Extensions|IdeographicDescriptionCharacters|Ideographic_Description_Characters|ImperialAramaic|Imperial_Aramaic|InscriptionalPahlavi|InscriptionalParthian|Inscriptional_Pahlavi|Inscriptional_Parthian|Javanese|Kaithi|KanaSupplement|Kana_Supplement|Kanbun|KangxiRadicals|Kangxi_Radicals|Kannada|Katakana|KatakanaPhoneticExtensions|Katakana_Phonetic_Extensions|KayahLi|Kayah_Li|Kharoshthi|Khmer|KhmerSymbols|Khmer_Symbols|Lao|Latin-1Supplement|LatinExtended-A|LatinExtended-B|LatinExtended-C|LatinExtended-D|LatinExtendedAdditional|Latin_1_Supplement|Latin_Extended_A|Latin_Extended_Additional|Latin_Extended_B|Latin_Extended_C|Latin_Extended_D|Lepcha|LetterlikeSymbols|Letterlike_Symbols|Limbu|LinearBIdeograms|LinearBSyllabary|Linear_B_Ideograms|Linear_B_Syllabary|Lisu|LowSurrogates|Low_Surrogates|Lycian|Lydian|MahjongTiles|Mahjong_Tiles|Malayalam|Mandaic|MathematicalAlphanumericSymbols|MathematicalOperators|Mathematical_Alphanumeric_Symbols|Mathematical_Operators|MeeteiMayek|Meetei_Mayek|MiscellaneousMathematicalSymbols-A|MiscellaneousMathematicalSymbols-B|MiscellaneousSymbols|MiscellaneousSymbolsAndPictographs|MiscellaneousSymbolsandArrows|MiscellaneousTechnical|Miscellaneous_Mathematical_Symbols_A|Miscellaneous_Mathematical_Symbols_B|Miscellaneous_Symbols|Miscellaneous_Symbols_And_Pictographs|Miscellaneous_Symbols_and_Arrows|Miscellaneous_Technical|ModifierToneLetters|Modifier_Tone_Letters|Mongolian|MusicalSymbols|Musical_Symbols|Myanmar|MyanmarExtended-A|Myanmar_Extended_A|NKo|NewTaiLue|New_Tai_Lue|NumberForms|Number_Forms|Ogham|OlChiki|Ol_Chiki|OldItalic|OldPersian|OldSouthArabian|OldTurkic|Old_Italic|Old_Persian|Old_South_Arabian|Old_Turkic|OpticalCharacterRecognition|Optical_Character_Recognition|Oriya|Osmanya|Phags-pa|Phags_pa|PhaistosDisc|Phaistos_Disc|Phoenician|PhoneticExtensions|PhoneticExtensionsSupplement|Phonetic_Extensions|Phonetic_Extensions_Supplement|PlayingCards|Playing_Cards|PrivateUseArea|Private_Use_Area|Rejang|RumiNumeralSymbols|Rumi_Numeral_Symbols|Runic|Samaritan|Saurashtra|Shavian|Sinhala|SmallFormVariants|Small_Form_Variants|SpacingModifierLetters|Spacing_Modifier_Letters|Specials|Sundanese|Superscripts_and_Subscripts|SuperscriptsandSubscripts|SupplementalArrows-A|SupplementalArrows-B|SupplementalMathematicalOperators|SupplementalPunctuation|Supplemental_Arrows_A|Supplemental_Arrows_B|Supplemental_Mathematical_Operators|Supplemental_Punctuation|SupplementaryPrivateUseArea-A|SupplementaryPrivateUseArea-B|Supplementary_Private_Use_Area_A|Supplementary_Private_Use_Area_B|SylotiNagri|Syloti_Nagri|Syriac|Tagalog|Tagbanwa|Tags|TaiLe|TaiTham|TaiViet|TaiXuanJingSymbols|Tai_Le|Tai_Tham|Tai_Viet|Tai_Xuan_Jing_Symbols|Tamil|Telugu|Thaana|Thai|Tibetan|Tifinagh|TransportAndMapSymbols|Transport_And_Map_Symbols|Ugaritic|UnifiedCanadianAboriginalSyllabics|UnifiedCanadianAboriginalSyllabicsExtended|Unified_Canadian_Aboriginal_Syllabics|Unified_Canadian_Aboriginal_Syllabics_Extended|Vai|VariationSelectors|VariationSelectorsSupplement|Variation_Selectors|Variation_Selectors_Supplement|VedicExtensions|Vedic_Extensions|VerticalForms|Vertical_Forms|YiRadicals|YiSyllables|Yi_Radicals|Yi_Syllables|YijingHexagramSymbols|Yijing_Hexagram_Symbols)\}" contained display
syntax match   clojureRegexpUnicodeCharClass    "\v\\[pP]\{%(Is)?%(C[cfnos]|L[lmotu]|M[cen]|N[dlo]|P[cdefios]|S[ckmo]|Z[lps])\}" contained display
syntax match   clojureRegexpUnicodeCharClass    "\v\\[pP]%(C|L|M|N|P|S|Z)" contained display
syntax cluster clojureRegexpCharPropertyClasses contains=clojureRegexpPosixCharClass,clojureRegexpUnicodeCharClass,clojureRegexpJavaCharClass
syntax cluster clojureRegexpCharClasses         contains=clojureRegexpPredefinedCharClass,clojureRegexpCharClass,@clojureRegexpCharPropertyClasses
syntax region  clojureRegexpCharClass           start="\\\@<!\[" end="\\\@<!\]" contained contains=clojureRegexpSpecialChar,clojureRegexpPredefinedCharClass,@clojureRegexpCharPropertyClasses
syntax match   clojureRegexpBoundary            "\\[bBAGZz]"   contained display
syntax match   clojureRegexpBoundary            "[$^]"         contained display
syntax match   clojureRegexpQuantifier          "[?*+][?+]\="  contained display
syntax match   clojureRegexpQuantifier          "\v\{\d+%(,|,\d+)?}\??" contained display
syntax match   clojureRegexpOr                  "|" contained display
syntax match   clojureRegexpBackRef             "\v\\%([1-9]\d*|k\<[a-zA-z]+\>)" contained display

" Mode modifiers, mode-modified spans, lookaround, regular and atomic
" grouping, and named-capturing.
syntax match clojureRegexpMod "\v\(@<=\?:"                        contained display
syntax match clojureRegexpMod "\v\(@<=\?[xdsmiuU]*-?[xdsmiuU]+:?" contained display
syntax match clojureRegexpMod "\v\(@<=\?%(\<?[=!]|\>)"            contained display
syntax match clojureRegexpMod "\v\(@<=\?\<[a-zA-Z]+\>"            contained display

syntax region clojureRegexpGroup start="\\\@<!(" matchgroup=clojureRegexpGroup end="\\\@<!)" contained contains=clojureRegexpMod,clojureRegexpQuantifier,clojureRegexpBoundary,clojureRegexpEscape,@clojureRegexpCharClasses
syntax region clojureRegexp start=/\#"/ skip=/\\\\\|\\"/ end=/"/ contains=@clojureRegexpCharClasses,clojureRegexpEscape,clojureRegexpQuote,clojureRegexpBoundary,clojureRegexpQuantifier,clojureRegexpOr,clojureRegexpBackRef,clojureRegexpGroup keepend

syntax match clojureComment ";.*$" contains=clojureTodo,@Spell
syntax match clojureComment "#!.*$"

syntax keyword clojureTodo contained FIXME XXX TODO FIXME: XXX: TODO:

syntax region clojureSexp   matchgroup=clojureParen start="("  matchgroup=clojureParen end=")"  contains=TOP,@Spell
syntax region clojureVector matchgroup=clojureParen start="\[" matchgroup=clojureParen end="\]" contains=TOP,@Spell
syntax region clojureMap    matchgroup=clojureParen start="{"  matchgroup=clojureParen end="}"  contains=TOP,@Spell

" Highlight superfluous closing parens, brackets and braces.
syntax match clojureError "]\|}\|)"

syntax sync fromstart

highlight default link clojureConstant     Constant
highlight default link clojureBoolean      Boolean
highlight default link clojureCharacter    Character
highlight default link clojureKeyword      Keyword
highlight default link clojureNumber       Number
highlight default link clojureString       String
highlight default link clojureStringEscape Character

highlight default link clojureRegexp                    Constant
highlight default link clojureRegexpEscape              Character
highlight default link clojureRegexpCharClass           SpecialChar
highlight default link clojureRegexpPosixCharClass      SpecialChar
highlight default link clojureRegexpJavaCharClass       SpecialChar
highlight default link clojureRegexpUnicodeCharClass    SpecialChar
highlight default link clojureRegexpPredefinedCharClass SpecialChar
highlight default link clojureRegexpBoundary            SpecialChar
highlight default link clojureRegexpQuantifier          SpecialChar
highlight default link clojureRegexpMod                 SpecialChar
highlight default link clojureRegexpOr                  SpecialChar
highlight default link clojureRegexpBackRef             SpecialChar
highlight default link clojureRegexpGroup               clojureRegexp
highlight default link clojureRegexpQuoted              clojureRegexp
highlight default link clojureRegexpQuote               clojureRegexpEscape

highlight default link clojureVariable  Identifier
highlight default link clojureCond      Conditional
highlight default link clojureDefine    Define
highlight default link clojureException Exception
highlight default link clojureFunc      Function
highlight default link clojureMacro     Macro
highlight default link clojureRepeat    Repeat

highlight default link clojureSpecial   Special
highlight default link clojureVarArg    Special
highlight default link clojureQuote     SpecialChar
highlight default link clojureUnquote   SpecialChar
highlight default link clojureMeta      SpecialChar
highlight default link clojureDeref     SpecialChar
highlight default link clojureAnonArg   SpecialChar
highlight default link clojureDispatch  SpecialChar

highlight default link clojureComment   Comment
highlight default link clojureTodo      Todo

highlight default link clojureError     Error

highlight default link clojureParen     Delimiter

let b:current_syntax = "clojure"

" vim:sts=4:sw=4:ts=4:et:smc=3000
