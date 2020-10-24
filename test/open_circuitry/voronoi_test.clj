(ns open-circuitry.voronoi-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [open-circuitry.test-helpers :refer :all]
    [open-circuitry.voronoi :refer [voronoi]]))

(deftest voronoi-diagrams
  (testing "edges"
    (testing "have two generator points"
      (let [diagram (voronoi [[2 2] [5 5]] [[0 0] [10 10]])
            {:keys [edges]} diagram]
        (exists (every? :generator-points edges))))))
