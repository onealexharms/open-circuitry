(ns open-circuitry.isolation-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [open-circuitry.test-helpers :refer :all]))

(deftest an-isolation-toolpath
  (testing "for a board with no junctures"
    (testing "has no isolation paths"
      (let [board [:open-circuitry/board {:width 10 :height 20}]
            paths (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (empty? paths)))))
  (testing "for a board with two unconnected junctures"
    (testing "has isolation paths"
      (let [board [:open-circuitry/board {:width 10 :height 20}
                   [:juncture {:at [2.0 2.0]}]
                   [:juncture {:at [5.0 5.0]}]]
            paths (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (not (empty? paths)))))))
