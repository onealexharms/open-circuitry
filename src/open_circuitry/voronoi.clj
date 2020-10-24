(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn- ->point [^PointD p]
  [(.x p) (.y p)])

(defn- ->PointD [[x y]]
  (PointD. x y))

(defn voronoi
  [points [[left top] [width height]]]
  (let [pointDs             (into-array PointD (map ->PointD points))
        tektosyne-bounds    (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        tektosyne-voronoi   (Voronoi/findAll pointDs tektosyne-bounds)
        tektosyne-vertices  (.voronoiVertices tektosyne-voronoi)
        tektosyne-edges     (.voronoiEdges tektosyne-voronoi)
        generator-points    (vec (map ->point (.generatorSites tektosyne-voronoi)))
        vertices            (vec (map ->point tektosyne-vertices))
        ->edge              (fn [edge]
                              {:start            (get vertices (.vertex1 edge))
                               :end              (get vertices (.vertex2 edge))
                               :generator-points [(get generator-points (.site1 edge))
                                                  (get generator-points (.site2 edge))]})
        edges               (vec (map ->edge tektosyne-edges))]
    {:vertices vertices
     :edges    edges}))
