(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data]
   [open-circuitry.voronoi :refer [voronoi]]))

(defn node [& collections]
  (vec (apply concat collections)))

(defn drill-hole [x y]
  [:circle {:cx x :cy y :r 0.002}])

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

(defn- juncture-point [juncture]
  (let [[x y] (:at (data/attributes juncture))]
    [(double x) (double y)]))

(defn- juncture-trace [juncture]
  (:trace (data/attributes juncture)))

(defn- juncture-points [board]
  (map juncture-point (junctures board)))

(defn- bounds [board]
  (let [{:keys [width height]} (data/attributes board)]
    [[0 0] [width height]]))

(defn- traces [junctures]
  (zipmap
    (map juncture-point junctures)
    (map juncture-trace junctures)))  

(defn- separates-isolated-traces? [edge traces]
  (let [[point1 point2]   (:generator-points edge)
        no-traces?        (and (nil? (traces point1)) (nil? (traces point2)))
        different-traces? (not= (traces point1) (traces point2))]
    (or different-traces? no-traces?)))

(defn- edge->line [edge]
  (let [[start-x start-y] (:start edge)
        [end-x end-y]     (:end edge)]
    [:line {:x1 start-x
            :y1 start-y
            :x2 end-x
            :y2 end-y}]))

(defn- merge-colinear [edges]
  edges)

(defn- isolation-paths [board]
  (if (> (count (juncture-points board)) 1)
    (let [traces           (traces (junctures board))
          possible-edges   (:edges (voronoi (juncture-points board) (bounds board)))
          isolating-edges  (filter #(separates-isolated-traces? % traces) possible-edges)
          lines            (map edge->line isolating-edges)]
      lines)))

(defn isolation-toolpath [board]
  (let [paths (isolation-paths board)]
    (node [:g#isolation-toolpath]
          (if (empty? paths)
            ["dummy text so dali doesn't delete"]
            paths))))

(defn drill-holes [board diameter]
  (for [juncture (junctures board)
        :when    (= diameter (:drill (data/attributes juncture)))]
    (let [[x y] (:at (data/attributes juncture))]
      (drill-hole x y))))

(defn drill-toolpath [drill-diameter board]
  (let [id (str "drill-" drill-diameter "mm")]
    (node [toolpath {:id id}] (drill-holes board drill-diameter))))

(defn drill-diameters [board]
  (->> (junctures board)
    (map (fn [juncture]
           (:drill (data/attributes juncture))))
    (remove nil?)
    distinct))

(defn drill-toolpaths
  [board]
  (for [drill-diameter (drill-diameters board)]
    (drill-toolpath drill-diameter board)))
 
(defn dali-rendering
  [board]
  (needs-attribute board :width)
  (needs-attribute board :height)
  (doseq [juncture (junctures board)]
    (needs-attribute juncture :at))
  (let [{:keys [width height]} (data/attributes board)]
    (node
      [:dali/page {:width (str width "mm")
                   :height (str height "mm")
                   :view-box (str "0 0 " width " " height)}]
      [(cutout-toolpath width height)
       (isolation-toolpath board)]
      (drill-toolpaths board))))
