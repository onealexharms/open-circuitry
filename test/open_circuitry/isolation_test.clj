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
                   [:juncture {:x 2, :y 2}]
                   [:juncture {:x 5, :y 5}]]
            cuts (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (not (empty? cuts)))))))

(defn line->LineD [line]
  (let [x1 (read-string (:x1 (:attrs line)))
        y1 (read-string (:y1 (:attrs line)))
        x2 (read-string (:x2 (:attrs line)))
        y2 (read-string (:y2 (:attrs line)))]
    (LineD. x1 y1 x2 y2)))

(defn cutout-LineDs [board]
  (let [rectangle (element-by-selector [:#cutout-toolpath :> :rect] board)
        {:keys [x y width height]} (:attrs rectangle)
        top         y
        right       (str (+ (read-string x) (read-string width)))
        bottom      (str (+ (read-string y) (read-string height)))
        left        x
        top-line    {:x1 left, :y1 top, :x2 right, :y2 top} 
        right-line  {:x1 right, :y1 top, :x2 right, :y2 bottom} 
        bottom-line {:x1 right, :y1 bottom, :x2 left, :y2 bottom}
        left-line   {:x1 left, :y1 bottom, :x2 left, :y2 top}]
    (map line->LineD 
      [{:tag :line, :attrs top-line}
       {:tag :line, :attrs right-line}
       {:tag :line, :attrs bottom-line}
       {:tag :line, :attrs left-line}]))) 

(defn juncture->PointD [juncture]
  (PointD. (first juncture) (last juncture)))

(defn isolation-LineDs [board]
  (let [isolation-lines (elements-by-selector [:#isolation-toolpath :> :line] board)
        LineDs          (map line->LineD isolation-lines)]
     (into-array LineD LineDs)))
  
(defn isolated? [juncture1 juncture2 board]
  (let [cutout           (Subdivision/fromLines (into-array LineD (cutout-LineDs board)) 0.001)
        isolation-lines  (Subdivision/fromLines (isolation-LineDs board) 0.001)
        divided-plane    (.division (Subdivision/intersection cutout isolation-lines)) 
        juncture1-PointD (juncture->PointD juncture1)
        juncture2-PointD (juncture->PointD juncture2)
        face             (.findFace divided-plane juncture1-PointD)
        nother-face      (.findFace divided-plane juncture2-PointD)]
    (not (identical? face nother-face))))
  
(deftest junctures
  (testing "that aren't connected"
    (testing "are isolated"
      (let [board [:open-circuitry/board {:width 10 :height 20}
                   [:juncture {:x 2, :y 2 :trace "GND"}]
                   [:juncture {:x 5, :y 5 :trace "+5v"}]]]
        (is (isolated? [2 2] [5 5] board))))))
