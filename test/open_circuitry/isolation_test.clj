(ns open-circuitry.isolation-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [open-circuitry.test-helpers :refer :all]))

(defn- has-negative-x? [path]
  (let [{:keys [x1 x2]} (:attrs path)]
    (or (neg? (read-string x1))
        (neg? (read-string x2)))))

(deftest an-isolation-toolpath
  (testing "for a board with no junctures"
    (testing "has no isolation paths"
      (let [board [:open-circuitry/board {:width 10 :height 20}]
            paths (elements-by-selector [:#isolation-toolpath :> :line] board)]
        (is (empty? paths)))))
  (testing "for a board with two unconnected junctures"
    (let [board [:open-circuitry/board {:width 10 :height 20}
                 [:juncture {:at [0.5 1.5]}]
                 [:juncture {:at [9.0 18.5]}]]
          paths (elements-by-selector [:#isolation-toolpath :> :line] board)]
      (testing "has isolation paths"
        (is (not (empty? paths))))
      (testing "has no isolation paths with a negative x"
        (is (empty? (filter has-negative-x? paths)))))))
