(ns syntax-test
  (:require [vim-clojure-static.test :as test :refer [defsyntaxtest]]))

(def number (partial every? (partial = :clojureNumber)))
(def !number (complement number))

(defsyntaxtest number-literals-test
  (with-format "%s"
    "1234567890" number "+1"    number "-1"    number ; Integer
    "0"          number "+0"    number "-0"    number ; Integer zero
    "0.12"       number "+0.12" number "-0.12" number ; Float
    "1."         number "+1."   number "-1."   number ; Float
    "0.0"        number "+0.0"  number "-0.0"  number ; Float zero
    "01234567"   number "+07"   number "-07"   number ; Octal
    "00"         number "+00"   number "-00"   number ; Octal zero
    "0x09abcdef" number "+0xf"  number "-0xf"  number ; Hexadecimal
    "0x0"        number "+0x0"  number "-0x0"  number ; Hexadecimal zero
    "3/2"        number "+3/2"  number "-3/2"  number ; Rational
    "0/0"        number "+0/0"  number "-0/0"  number ; Rational zero (not a syntax error)
    "2r1"        number "+2r1"  number "-2r1"  number ; Radix
    "36R1"       number "+36R1" number "-36R1" number ; Radix

    ;; Illegal literals (some are accepted by the reader, but are bad style)

    ".1" !number
    "01.2" !number
    "089" !number
    "0xfg" !number
    "1.0/1" !number
    "01/2" !number
    "1/02" !number
    "2r2" !number
    "1r0" !number
    "37r36" !number

    ;; BigInt

    "0N" number
    "+0.1N" !number
    "-07N" number
    "08N" !number
    "+0x0fN" number
    "1/2N" !number

    ;; BigDecimal

    "0M" number
    "+0.1M" number
    "08M" !number
    "08.9M" !number
    "0x1fM" !number
    "3/4M" !number
    "2r1M" !number

    ;; Exponential notation

    "0e0" number
    "+0.1e-1" number
    "-1e-1" number
    "08e1" !number
    "07e1" !number
    "0xfe-1" !number
    "2r1e-1" !number))

;; (test #'number-literals-test)
