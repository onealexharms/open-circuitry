(ns open-circuitry.svg
  (:require
   [open-circuitry.data-tree :as data])
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn node [& collections]
  (vec (apply concat collections)))

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

(defn juncture->PointD [juncture]
   (let [[x y] (:at (data/attributes juncture))]
     (PointD. x y)))

(defn junctures-as-PointDs [board]
  (->> (junctures board)
    (map juncture->PointD)
    (into-array PointD)))

(defn voronoi [points [[x y] [width height]]]
  (let [PointDs (into-array PointD (map (fn [[x y]]
                                          (PointD. x y))
                                        points))
        bounds  (RectD. (PointD. x y) (PointD. (+ x width) (+ y height)))]
    (Voronoi/findAll PointDs bounds)))

(defn- juncture-point [juncture]
  (:at (data/attributes juncture)))

(defn- juncture-points [board]
  (map juncture-point (junctures board)))

(defn- isolation-cuts [board]
  (let [points (juncture-points board)]
    (if (> (count points) 1)
      (let [{:keys [width height]} (data/attributes board)
            voronoi  (voronoi points [[0 0] [width height]])
            vertices (.voronoiVertices voronoi)]
        (for [edge (.voronoiEdges voronoi)
              :let [vertex1 (get vertices (.vertex1 edge))
                    vertex2 (get vertices (.vertex2 edge))]]
          [:line {:x1 (.x vertex1)
                  :y1 (.y vertex1)
                  :x2 (.x vertex2)
                  :y2 (.y vertex2)}])))))

(defn isolation-toolpath [board]
  (let [cuts (isolation-cuts board)]
    (node [:g#isolation-toolpath]
          (if (empty? cuts)
            ["dummy text so dali doesn't delete"]
            cuts))))

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
