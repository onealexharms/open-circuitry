(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(defn needs-attribute [board attribute]
  (when-not (attribute (data/attributes board))
    (throw (Exception. (str "A board needs a " attribute " attribute")))))

(defn dali-rendering
  [board]
  (needs-attribute board :width)
  (needs-attribute board :height)
  (let [{:keys [width height]} (data/attributes board)]
    [:dali/page
     {:width (str width "mm")
      :height (str height "mm")
      :view-box (str "0 0 " width " " height)}
     [:g#cutout-toolpath
      [:rect [0 0] [width height]]]]))
