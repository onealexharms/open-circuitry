(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.data-tree :as data]
    [open-circuitry.test-helpers :refer :all]
    [open-circuitry.svg :refer [dali-rendering]])
  (:import
   (org.kynosarges.tektosyne.subdivision Subdivision)
   (org.kynosarges.tektosyne.geometry LineD PointD)))

(deftest a-drill-toolpath
  (testing "containing two 5mm drilled junctures"
    (let [toolpath-id       "drill-5mm"
          toolpath-selector (enlive/attr= :id toolpath-id)
          board             [:open-circuitry/board {:width 10, :height 10}
                             [:juncture {:at [4 3], :drill 2}]
                             [:juncture {:at [1 2], :drill 5}]
                             [:juncture {:at [2 3], :drill 5}]
                             [:juncture {:at [5 6]}]]
          holes             (:content (first (toolpaths-with-id "drill-5mm" board)))
          location1         (select-keys (:attrs (first holes)) [:cx :cy])
          location2         (select-keys (:attrs (second holes)) [:cx :cy])]
      (testing "exists once and only once"
        (is (= 1 (count (toolpaths-with-id toolpath-id board)))))
      (testing "contains only junctures that are drilled"
        (is (empty? (elements-by-selector
                      [toolpath-selector :> (location-selector 5 6)]
                      board))))
      (testing "does not contain junctures of a different diameter"
        (is (empty? (elements-by-selector
                      [toolpath-selector :> (location-selector 4 3)]
                      board))))
      (testing "has two holes"
        (is (= 2 (count holes)))
        (testing "at the junctures' locations"
          (is (= {:cx "1", :cy "2"} location1))
          (is (= {:cx "2", :cy "3"} location2)))
        (testing "that accomodate LaserWeb's hole weirdness"
          (is (= "0.002" (:r (:attrs (first holes)))))
          (is (= "0.002" (:r (:attrs (second holes)))))))))
  (testing "for a board with only non-drilled junctures"
    (let [board [:open-circuitry/board {:width 10, :height 10}
                 [:juncture {:at [4 3]}]
                 [:juncture {:at [3 7]}]]]
      (testing "does not exist"
        (is (empty? (elements-by-selector [(enlive/attr|= :id "drill")] board)))))))

(deftest a-juncture
  (testing "requires"
    (testing "a position"
      (is (thrown-with-msg? Exception
                            #"A juncture needs attribute: :at"
                            (dali-rendering [:open-circuitry/board {:width 10, :height 10}
                                             [:juncture {:drill 22.7}]]))))))

(defn line->LineD [line]
  (LineD. (read-string (:x1 (:attrs line)))
          (read-string (:y1 (:attrs line)))
          (read-string (:x2 (:attrs line)))
          (read-string (:y2 (:attrs line)))))

(defn cutout-LineDs [board]
  (let [rectangle (element-by-selector [:#cutout-toolpath :> :rect] board)
        {:keys [x y width height]} (:attrs rectangle)
        left        (read-string x)
        top         (read-string y)
        right       (+ left (read-string width))
        bottom      (+ top (read-string height))]
    (into-array LineD [(LineD. left top right top)
                       (LineD. right top right bottom)
                       (LineD. right bottom left bottom)
                       (LineD. left bottom left top)])))

(defn isolation-LineDs [board]
  (->> (elements-by-selector [:#isolation-toolpath :> :line] board)
       (map line->LineD)
       (into-array LineD)))

(defn isolated? [location1 location2 board]
  (let [cutout          (Subdivision/fromLines (cutout-LineDs board) 0.001)
        isolation-lines (Subdivision/fromLines (isolation-LineDs board) 0.001)
        divided-plane   (.division (Subdivision/intersection cutout isolation-lines))
        face            (fn [[x y]]
                          (.findFace divided-plane (PointD. x y)))]
    (not (identical? (face location1) (face location2)))))

(deftest junctures
  (testing "that aren't connected"
    (testing "are isolated"
      (let [GND-coordinates [7 8]
            VCC-coordinates [5 5]
            abc-coordinates [1 2]
            board           [:open-circuitry/board {:width 10 :height 20}
                             [:juncture {:at GND-coordinates, :trace "GND"}]
                             [:juncture {:at VCC-coordinates, :trace "VCC"}]
                             [:juncture {:at abc-coordinates, :trace "abc"}]]]
        (is (isolated? GND-coordinates VCC-coordinates board))
        (is (isolated? GND-coordinates abc-coordinates board))
        (is (isolated? abc-coordinates VCC-coordinates board)))))
  (testing "that share a Voronoi edge and share a trace"
    (testing "are not isolated"
      (let [GND-coordinates [7 8]
            VCC-start       [5 5]
            VCC-end         [1 2]
            board           [:open-circuitry/board {:width 10 :height 20}
                             [:juncture {:at GND-coordinates, :trace "GND"}]
                             [:juncture {:at VCC-start, :trace "VCC"}]
                             [:juncture {:at VCC-end, :trace "VCC"}]]]
        (is (isolated? GND-coordinates VCC-start board))
        (is (isolated? GND-coordinates VCC-end board))
        (is (not (isolated? VCC-start VCC-end board)))))))
