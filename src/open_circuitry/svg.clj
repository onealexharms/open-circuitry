(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(def drill-hole
  [:circle {:cx 0 :cy 0 :r 0.02}])

(def toolpath :g)

(defn needs-attribute [board attribute]
  (when-not (attribute (data/attributes board))
    (throw (Exception. (str "A board needs a " attribute " attribute")))))

(defn cutout-toolpath [width height]
   [toolpath {:id "cutout-toolpath"}
    [:rect {:fill :white
            :stroke :cornflowerblue
            :dali/z-index -99}
     [0 0] [width height]]])

(defn drill-toolpaths
  [board]
  (let [juncture       (first (data/children board))
        drill-diameter (:drill (data/attributes juncture))
        id             (str "drill-" drill-diameter "mm")]
    [[toolpath {:id id} drill-hole]]))
 
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
           (drill-toolpaths board)))))
