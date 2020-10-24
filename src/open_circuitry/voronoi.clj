(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn voronoi
  [points [[x y] [width height]]]
  (let [PointDs      (into-array PointD (map (fn [[x y]]
                                               (PointD. x y))
                                             points))
        bounds           (RectD. (PointD. x y) (PointD. (+ x width) (+ y height)))
        voronoi-object   (Voronoi/findAll PointDs bounds)
        voronoi-vertices (.voronoiVertices voronoi-object)
        voronoi-edges    (.voronoiEdges voronoi-object)
        vertices         (vec (map (fn [^PointD vertex]
                                     [(.x vertex) (.y vertex)])
                                   voronoi-vertices))
        edges            (vec (map (fn [edge]
                                     {:start (get vertices (.vertex1 edge))
                                      :end   (get vertices (.vertex2 edge))})
                                   voronoi-edges))]
    {:vertices vertices
     :edges    edges}))
