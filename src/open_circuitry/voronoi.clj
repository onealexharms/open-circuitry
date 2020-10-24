(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn- ->point [^PointD p]
  [(.x p) (.y p)])

(defn- ->PointD [[x y]]
  (PointD. x y))

(defn voronoi
  [points [[left top] [width height]]]
  (let [PointDs          (into-array PointD (map ->PointD points))
        bounds           (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        voronoi-object   (Voronoi/findAll PointDs bounds)
        voronoi-vertices (.voronoiVertices voronoi-object)
        voronoi-edges    (.voronoiEdges voronoi-object)
        generator-sites  (.generatorSites voronoi-object)
        vertices         (vec (map ->point voronoi-vertices))
        edges            (vec (map (fn [edge]
                                     {:start            (get vertices (.vertex1 edge))
                                      :end              (get vertices (.vertex2 edge))
                                      :generator-points [(->point (get generator-sites (.site1 edge)))
                                                         (->point (get generator-sites (.site2 edge)))]})
                                   voronoi-edges))]
    {:vertices vertices
     :edges    edges}))
