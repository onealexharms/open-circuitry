(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]))

(defmacro exists [thing]
  `(is ~thing))

(defn- board-with-juncture [attributes]
  [:open-circuitry/board {:width 10, :height 10}
   [:juncture attributes]])

(defn- toolpath-with-id [toolpath-id board]
  (test/svg-element board [[:g (enlive/attr= :id toolpath-id)]]))

(defn- toolpath [toolpath-type]
  (enlive/attr|= :id toolpath-type))

(defn- drill-hole-attributes [juncture]
  (test/svg-attributes (board-with-juncture juncture)
                       [(enlive/attr|= :id "drill") :> :circle]))

(defn- center-of-drill-hole [juncture]
  (let [{:keys [cx cy]} (drill-hole-attributes juncture)]
    [cx cy]))

(deftest a-juncture-that-contains-a-drill
  (testing "has a well-formed drill toolpath"
    (exists (toolpath-with-id "drill-2.3mm" (board-with-juncture {:drill 2.3})))
    (exists (not (toolpath-with-id "drill-1.9mm" (board-with-juncture {:drill 2.3}))))
    (exists (toolpath-with-id "drill-1.9mm" (board-with-juncture {:drill 1.9}))))
  (testing "is a circle"
    (exists (test/svg-element (board-with-juncture {:drill 2.3}) [(toolpath "drill") :> :circle])))
  (testing "has a radius of 0.02"
    (is (= "0.02" (:r (drill-hole-attributes {:drill 2.3})))))
  (testing "has a zero center"
    (is (= ["0" "0"] (center-of-drill-hole {:drill 2.3})))))
