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

(defn- drill-hole-attributes [juncture]
  (test/svg-attributes (board-with-juncture juncture)
                       [(enlive/attr|= :id "drill") :> :circle]))

(defn- radius-of-drill-hole [juncture]
  (:r (drill-hole-attributes juncture)))

(defn- center-of-drill-hole [juncture]
  (let [{:keys [cx cy]} (drill-hole-attributes juncture)]
    [cx cy]))

(deftest drilled-juncture
  (testing "has a drill toolpath"
    (is (toolpath-named (board-with-juncture {:drill 2.3}) "drill-2.3mm"))
    (is (not (toolpath-named (board-with-juncture {:drill 2.3}) "drill-1.9mm")))
    (is (toolpath-named (board-with-juncture {:drill 1.9}) "drill-1.9mm")))
  (testing "is a circle"
    (is (test/svg-element (board-with-juncture {:drill 2.3}) [(enlive/attr= :id "drill-2.3mm") :> :circle])))
  (testing "drill-hole has a radius of 0.02"
    (is (= "0.02" (radius-of-drill-hole {:drill 2.3}))))
  (testing "drill hole has a zero center"
    (is (= ["0" "0"] (center-of-drill-hole {:drill 2.3})))))
