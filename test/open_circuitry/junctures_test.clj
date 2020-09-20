(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]))

(defn- board-with-juncture [attributes]
  [:open-circuitry/board {:width 10, :height 10}
   [:juncture attributes]])

(defn- toolpath-named [board toolpath-name]
  (test/svg-element board [[:g (enlive/attr= :id toolpath-name)]]))

(deftest drilled-juncture
  (let [drill-toolpath (enlive/attr= :id "drill-2.3mm")]
    (testing "has a drill toolpath"
      (is (toolpath-named (board-with-juncture {:drill 2.3}) "drill-2.3mm"))
      (is (not (toolpath-named (board-with-juncture {:drill 2.3}) "drill-1.9mm")))
      (is (toolpath-named (board-with-juncture {:drill 1.9}) "drill-1.9mm")))
    (testing "is a circle"
      (is (test/svg-element (board-with-juncture {:drill 2.3}) [drill-toolpath :> :circle])))
    (testing "drill-hole circle has a radius of 0.02"
      (let [radius (:r (test/svg-attributes (board-with-juncture {:drill 2.3}) [drill-toolpath :> :circle]))]
        (is (= "0.02" radius))))))
