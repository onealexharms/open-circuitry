(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]))

(defn drill-hole [x y]
  [:circle {:cx x :cy y :r 0.02}])

(def toolpath :g)

(defn needs-attribute [thing attribute]
  (when-not (attribute (data/attributes thing))
    (throw (Exception. (str "A " (name (first thing)) " needs attribute: " attribute)))))

(defn cutout-toolpath [width height]
   [toolpath {:id "cutout-toolpath"}
    [:rect {:fill :white
            :stroke :cornflowerblue
            :dali/z-index -99}
     [0 0] [width height]]])

(defn junctures [board]
  (data/children board))

(defn node [& collections]
  (vec (apply concat collections)))

(defn drill-holes [board]
  (for [juncture (junctures board)]
    (let [{:keys [x, y]} (data/attributes juncture)]
      (drill-hole x y))))

(defn drill-toolpaths
  [board]
  (when-let [drill-juncture (first (junctures board))]
    (needs-attribute drill-juncture :x)
    (needs-attribute drill-juncture :y)
    (let [drill-diameter (:drill (data/attributes drill-juncture))
          id             (str "drill-" drill-diameter "mm")
          drill-toolpath (node [toolpath {:id id}] (drill-holes board))]
      [drill-toolpath])))
 
(defn dali-rendering
  [board]
  (needs-attribute board :width)
  (needs-attribute board :height)
  (let [{:keys [width height]} (data/attributes board)]
    (node
      [:dali/page {:width (str width "mm")
                   :height (str height "mm")
                   :view-box (str "0 0 " width " " height)}]
      [(cutout-toolpath width height)]
      (drill-toolpaths board))))
