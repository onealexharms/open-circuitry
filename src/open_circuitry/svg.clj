(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(def arbitrary-element
  "required by dali so it doesn't delete stuff"
  [:circle {:cx -1 :cy -1 :r 0.001}])

(defn needs-attribute [board attribute]
  (when-not (attribute (data/attributes board))
    (throw (Exception. (str "A board needs a " attribute " attribute")))))

(defn cutout-toolpath [width height]
   [:g#cutout-toolpath
    [:rect {:fill :white
            :stroke :red
            :dali/z-index -99}
     [0 0] [width height]]])

(defn child-toolpaths
  [board]
  (let [[child] (data/children board)
        {:keys [drill]} (data/attributes child)
        id (str "drill-" drill "mm")]
    [[:g {:id id} arbitrary-element]]))
 
(defn dali-rendering
  [board]
  (needs-attribute board :width)
  (needs-attribute board :height)
  (let [{:keys [width height]} (data/attributes board)]
    (vec (concat 
           [:dali/page
            {:width (str width "mm")
             :height (str height "mm")
             :view-box (str "0 0 " width " " height)}]
           [(cutout-toolpath width height)]
           (child-toolpaths board)))))
