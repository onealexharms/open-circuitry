(ns opencircuit.core
  (:require
    [dali.io]
    [dali.layout.distribute]))

(defn position [thing x y]
  [:g {:transform [:translate [x y]]}
   thing])

(defmacro define [name value]
  `(def ^:private ~name ~value))

(defn export [thing filename]
  (dali.io/render-svg thing filename))
