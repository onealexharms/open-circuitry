(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]))

(deftest a-single-juncture
  (let [board [:open-circuitry/board {:width 10, :height 10}
               [:juncture {:drill 2.3}]]]
    (testing "describes a toolpath"
      (is (test/svg-element board [[:g (enlive/attr= :id "drill-2.3mm")]])))
    (testing "drill hole is a circle"
      (is (test/svg-element board [(enlive/attr= :id "drill-2.3mm") :> :circle])))
    (testing "drill hole circle has a radius of 0.02"
      (let [radius (:r (test/svg-attributes board [(enlive/attr= :id "drill-2.3mm") :> :circle]))]
        (is (= "0.02" radius))))))
