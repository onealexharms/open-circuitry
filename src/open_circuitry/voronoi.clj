(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn- ->point [^PointD p]
  [(.x p) (.y p)])

(defn- ->PointD [[x y]]
  (PointD. x y))

(defn voronoi
  [points [[left top] [width height]]]
  (let [pointDs          (into-array PointD (map ->PointD points))
        bounds           (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        voronoi-object   (Voronoi/findAll pointDs bounds)
        voronoi-vertices (.voronoiVertices voronoi-object)
        voronoi-edges    (.voronoiEdges voronoi-object)
        generator-points (vec (map ->point (.generatorSites voronoi-object)))
        vertices         (vec (map ->point voronoi-vertices))
        ->edge           (fn [edge]
                           {:start            (get vertices (.vertex1 edge))
                            :end              (get vertices (.vertex2 edge))
                            :generator-points [(get generator-points (.site1 edge))
                                               (get generator-points (.site2 edge))]})
        edges            (vec (map ->edge voronoi-edges))]
    {:vertices vertices
     :edges    edges}))
