(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn voronoi
  [points [[left top] [width height]]]
  (let [PointDs      (into-array PointD (map (fn [[x y]]
                                               (PointD. x y))
                                             points))
        bounds           (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        voronoi-object   (Voronoi/findAll PointDs bounds)
        voronoi-vertices (.voronoiVertices voronoi-object)
        voronoi-edges    (.voronoiEdges voronoi-object)
        vertices         (vec (map (fn [^PointD vertex]
                                     [(.x vertex) (.y vertex)])
                                   voronoi-vertices))
        edges            (vec (map (fn [edge]
                                     {:start            (get vertices (.vertex1 edge))
                                      :end              (get vertices (.vertex2 edge))
                                      :generator-points "string"})
                                   voronoi-edges))]
    {:vertices vertices
     :edges    edges}))
