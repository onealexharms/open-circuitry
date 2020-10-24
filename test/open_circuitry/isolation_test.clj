(ns open-circuitry.isolation-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [open-circuitry.test-helpers :refer :all])
  (:import
   (org.kynosarges.tektosyne.subdivision Subdivision)
   (org.kynosarges.tektosyne.geometry LineD PointD)))

(deftest an-isolation-toolpath
  (testing "for a board with no junctures"
    (testing "has no isolation cuts"
      (let [board [:open-circuitry/board {:width 10 :height 20}]
            cuts (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (empty? cuts)))))
  (testing "for a board with two unconnected junctures"
    (testing "has isolation cuts"
      (let [board [:open-circuitry/board {:width 10 :height 20}
                   [:juncture {:at [2 2]}]
                   [:juncture {:at [5 5]}]]
            cuts (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (not (empty? cuts)))))))

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
        (is (isolated? abc-coordinates VCC-coordinates board))))))
