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

(def drill-toolpath (enlive/attr= :id "drill-2.3mm"))

(defn- radius-of-drill-hole [juncture]
  (:r
    (test/svg-attributes (board-with-juncture juncture)
                         [drill-toolpath :> :circle])))

(deftest drilled-juncture
  (testing "has a drill toolpath"
    (is (toolpath-named (board-with-juncture {:drill 2.3}) "drill-2.3mm"))
    (is (not (toolpath-named (board-with-juncture {:drill 2.3}) "drill-1.9mm")))
    (is (toolpath-named (board-with-juncture {:drill 1.9}) "drill-1.9mm")))
  (testing "is a circle"
    (is (test/svg-element (board-with-juncture {:drill 2.3}) [drill-toolpath :> :circle])))
  (testing "drill-hole is/has a circle has a radius of 0.02"
    (is (= "0.02" (radius-of-drill-hole {:drill 2.3})))))
