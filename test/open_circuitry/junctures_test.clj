(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]))

(deftest a-single-juncture
  (testing "describes a toolpath"
    (let [board [:open-circuitry/board {:width 10, :height 10}
                 [:juncture {:drill 2.3}]]]
      (is (test/svg-element board [[:g (enlive/attr= :id "drill-2.3mm")]])))))
