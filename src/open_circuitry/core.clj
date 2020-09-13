(ns open-circuitry.core
  (:require
    [dali.io]
    [dali.layout.distribute]
    [open-circuitry.svg :as svg]))

(defn position [thing x y]
  [:g {:transform [:translate [x y]]}
   thing])

(defmacro define [name value]
  `(def ^:private ~name ~value))

(defn board [name board]
  (dali.io/render-svg (svg/dali-rendering board) (str name ".svg")))
