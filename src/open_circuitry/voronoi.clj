(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry LineD PointD RectD Voronoi)))

(defn- ->point [^PointD p]
  [(.x p) (.y p)])

(defn- ->PointD [[x y]]
  (PointD. x y))

(defn- bounded-edge [tektosyne-bounds start end]
  (let [line         (LineD. (->PointD start) (->PointD end))
        clipped-line (.intersect tektosyne-bounds line)
        start        (->point (.start clipped-line))
        end          (->point (.end clipped-line))]
    [start end]))

(defn- edges [tektosyne-bounds tektosyne-edges vertices generator-points]
  (vec (for [tektosyne-edge tektosyne-edges]
         (let [[start end] (bounded-edge tektosyne-bounds
                                         (get vertices (.vertex1 tektosyne-edge))
                                         (get vertices (.vertex2 tektosyne-edge)))]
           {:start            start
            :end              end
            :generator-points [(get generator-points (.site1 tektosyne-edge))
                               (get generator-points (.site2 tektosyne-edge))]}))))

(defn voronoi
  [points [[left top] [width height]]]
  (let [pointDs             (into-array PointD (map ->PointD points))
        tektosyne-bounds    (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        tektosyne-voronoi   (Voronoi/findAll pointDs tektosyne-bounds)
        tektosyne-vertices  (.voronoiVertices tektosyne-voronoi)
        tektosyne-edges     (.voronoiEdges tektosyne-voronoi)
        generator-points    (vec (map ->point (.generatorSites tektosyne-voronoi)))
        vertices            (vec (map ->point tektosyne-vertices))
        edges               (edges tektosyne-bounds tektosyne-edges vertices generator-points)]
    {:vertices vertices
     :edges    edges}))
