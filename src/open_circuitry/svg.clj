(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(defn needs-attribute [board attribute]
  (when-not (attribute (data/attributes board))
    (throw (Exception. (str "A board needs a " attribute " attribute")))))

(defn rendered-board
  [board]
  (needs-attribute board :width)
  (needs-attribute board :height)
  [:dali/page
   {:width (str (:width (data/attributes board)) "mm")
    :height (:height (data/attributes board))}])
