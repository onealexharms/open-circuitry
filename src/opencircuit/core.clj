(ns opencircuit.core)

(defmacro define [name value]
  `(def ^:private ~name ~value))
