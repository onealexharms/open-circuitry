(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry LineD PointD RectD Voronoi)))

(defn- ->point [^PointD p]
  [(.x p) (.y p)])

(defn- ->PointD [[x y]]
  (PointD. x y))

(defn- bounded-edge [tektosyne-bounds start end]
  (let [line         (LineD. start end)
        clipped-line (.intersect tektosyne-bounds line)
        start        (->point (.start clipped-line))
        end          (->point (.end clipped-line))]
    [start end]))

(defn- edges [tektosyne-voronoi tektosyne-bounds] 
  (let [generator-points   (vec (map ->point (.generatorSites tektosyne-voronoi)))]
    (vec (for [tektosyne-edge (.voronoiEdges tektosyne-voronoi)]
           (let [tektosyne-vertices (.voronoiVertices tektosyne-voronoi)
                 [start end] (bounded-edge tektosyne-bounds
                                           (get tektosyne-vertices (.vertex1 tektosyne-edge))
                                           (get tektosyne-vertices (.vertex2 tektosyne-edge)))]
             {:start            start
              :end              end
              :generator-points [(get generator-points (.site1 tektosyne-edge))
                                 (get generator-points (.site2 tektosyne-edge))]})))))

(defn voronoi
  [points [[left top] [width height]]]
  (let [pointDs             (into-array PointD (map ->PointD points))
        tektosyne-bounds    (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        tektosyne-voronoi   (Voronoi/findAll pointDs tektosyne-bounds)
        edges               (edges tektosyne-voronoi tektosyne-bounds)]
    {:edges edges}))
