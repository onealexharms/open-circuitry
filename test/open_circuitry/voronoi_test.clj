(ns open-circuitry.voronoi-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [open-circuitry.test-helpers :refer :all]
    [open-circuitry.voronoi :refer [voronoi]]))

(deftest voronoi-diagrams
  (testing "edges"
    (testing "list their generator points"
      (let [diagram          (voronoi [[2.5 2] [5 5.0]] [[0 0] [10 10]])
            generator-points (:generator-points (first (:edges diagram)))]
        (is (= (sort generator-points) [[2.5 2.0] [5.0 5.0]]))))))
