(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(defn rendered-board
  [board]
  (when-not (:width (data/attributes board))
    (throw (Exception. "A board needs a :width attribute")))
  (when-not (:height (data/attributes board))
    (throw (Exception. "A board needs a :height attribute")))
  [nil {:width (:width (data/attributes board))}])
