(ns open-circuitry.core
  (:require
    [dali.io]
    [dali.layout.distribute]))

(defn position [thing x y]
  [:g {:transform [:translate [x y]]}
   thing])

(defmacro define [name value]
  `(def ^:private ~name ~value))

(defn board [name board-definition]
  (dali.io/render-svg board-definition (str name ".svg")))
