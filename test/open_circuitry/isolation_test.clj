(ns open-circuitry.isolation-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [open-circuitry.test-helpers :refer :all]))

(deftest an-isolation-toolpath
  (testing "for a board with no junctures"
    (testing "has no isolation cuts"
      (let [board [:open-circuitry/board {:width 10 :height 20}]
            cuts (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (empty? cuts))))))
