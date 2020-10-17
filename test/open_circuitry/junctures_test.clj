(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.data-tree :as data]
    [open-circuitry.test-helpers :refer :all]
    [open-circuitry.svg :refer [dali-rendering]]))

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
          (is (= "0.02" (:r (:attrs (first holes)))))
          (is (= "0.02" (:r (:attrs (second holes)))))))))
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
