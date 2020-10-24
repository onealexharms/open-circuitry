(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry PointD RectD Voronoi)))

(defn- ->point [^PointD p]
  [(.x p) (.y p)])

(defn- ->PointD [[x y]]
  (PointD. x y))

(defn- edges [tektosyne-edges vertices generator-points]
  (let [edge (fn [tektosyne-edge]
               {:start            (get vertices (.vertex1 tektosyne-edge))
                :end              (get vertices (.vertex2 tektosyne-edge))
                :generator-points [(get generator-points (.site1 tektosyne-edge))
                                   (get generator-points (.site2 tektosyne-edge))]})]
    (vec (map edge tektosyne-edges))))

(defn voronoi
  [points [[left top] [width height]]]
  (let [pointDs             (into-array PointD (map ->PointD points))
        tektosyne-bounds    (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        tektosyne-voronoi   (Voronoi/findAll pointDs tektosyne-bounds)
        tektosyne-vertices  (.voronoiVertices tektosyne-voronoi)
        tektosyne-edges     (.voronoiEdges tektosyne-voronoi)
        generator-points    (vec (map ->point (.generatorSites tektosyne-voronoi)))
        vertices            (vec (map ->point tektosyne-vertices))
        edges               (edges tektosyne-edges vertices generator-points)]
    {:vertices vertices
     :edges    edges}))
