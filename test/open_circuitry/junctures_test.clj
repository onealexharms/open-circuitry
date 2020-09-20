(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]))

(deftest drilled-juncture
  (let [board [:open-circuitry/board {:width 10, :height 10}
               [:juncture {:drill 2.3}]]
        drill-toolpath (enlive/attr= :id "drill-2.3mm")]
    (testing "has a drill toolpath"
      (is (test/svg-element board [[:g drill-toolpath]])))
    (testing "is a circle"
      (is (test/svg-element board [drill-toolpath :> :circle])))
    (testing "drill-hole circle has a radius of 0.02"
      (let [radius (:r (test/svg-attributes board [drill-toolpath :> :circle]))]
        (is (= "0.02" radius))))))
