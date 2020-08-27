(ns opencircuit.core
  (:require
    [dali.io]
    [dali.layout.distribute]))

(defmacro define [name value]
  `(def ^:private ~name ~value))

(defn export [thing filename]
  (dali.io/render-svg thing filename))
