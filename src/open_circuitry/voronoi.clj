(ns open-circuitry.voronoi
  (:import
   (org.kynosarges.tektosyne.geometry LineD PointD RectD Voronoi)))

(def ^:private epsilon 0.0001)

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

(defn- zero-length? [{[x1 y1] :start, [x2 y2] :end}]
  (let [x-delta (- x1 x2)
        y-delta (- y1 y2)
        length  (Math/sqrt (+ (* x-delta x-delta) (* y-delta y-delta)))]
    (< length epsilon)))

(defn- edges [tektosyne-voronoi tektosyne-bounds] 
  (let [generator-points   (vec (map ->point (.generatorSites tektosyne-voronoi)))
        tektosyne-vertices (.voronoiVertices tektosyne-voronoi)
        edges              (for [tektosyne-edge (.voronoiEdges tektosyne-voronoi)]
                             (let [index-of-vertex1   (.vertex1 tektosyne-edge)
                                   index-of-vertex2   (.vertex2 tektosyne-edge)
                                   [start end]        (bounded-edge 
                                                        tektosyne-bounds
                                                        (get tektosyne-vertices index-of-vertex1)
                                                        (get tektosyne-vertices index-of-vertex2))
                                   index-of-site1     (.site1 tektosyne-edge)
                                   index-of-site2     (.site2 tektosyne-edge)]
                               {:start            start
                                :end              end
                                :generator-points [(get generator-points index-of-site1)
                                                   (get generator-points index-of-site2)]}))] 
    (->> edges
         (remove zero-length?)
         (vec))))

(defn voronoi [points [[left top] [width height]]]
  (let [pointDs             (into-array PointD (map ->PointD points))
        tektosyne-bounds    (RectD. (PointD. left top) (PointD. (+ left width) (+ top height)))
        tektosyne-voronoi   (Voronoi/findAll pointDs tektosyne-bounds)]
    {:edges (edges tektosyne-voronoi tektosyne-bounds)}))
