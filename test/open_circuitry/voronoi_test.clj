(ns open-circuitry.voronoi-test)
#_
  (:require
    [clojure.test :refer [deftest is]])

#_(deftest voronoi-diagrams
    (testing "edges"
      (testing "have two generator points"
        (let [result (voronoi [[2 2] [5 5]] [[0 0] [10 10]])]
          (is (every?))))))
