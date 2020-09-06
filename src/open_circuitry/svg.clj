(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(defn rendering
  [board]
  (when-not (:width (data/attributes board))
    (throw (Exception. "A board needs a :width attribute")))
  [nil {:width (:width (data/attributes board))}])
