(ns open-circuitry.isolation-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [open-circuitry.test-helpers :refer :all]))

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

