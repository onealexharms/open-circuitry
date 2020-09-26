(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]))

(defmacro exists [thing]
  `(is ~thing))

(defn- board-with-juncture [juncture-attributes]
  [:open-circuitry/board {:width 10, :height 10}
   [:juncture juncture-attributes]])

(defn- toolpath-with-id [toolpath-id board]
  (test/svg-element board [[:g (enlive/attr= :id toolpath-id)]]))

(defn- toolpath [toolpath-type]
  (enlive/attr|= :id toolpath-type))

(defn- drill-hole-attributes [juncture-attributes]
  (test/svg-attributes (board-with-juncture juncture-attributes)
                       [(enlive/attr|= :id "drill") :> :circle]))

(defn- center-of-drill-hole [juncture-attributes]
  (let [{:keys [cx cy]} (drill-hole-attributes juncture-attributes)]
    [cx cy]))

(deftest a-rendered-board
  (testing "when a juncture has a drill"
    (testing "it has a well-formed drill toolpath"
      (exists (toolpath-with-id "drill-2.3mm" (board-with-juncture {:drill 2.3})))
      (exists (not (toolpath-with-id "drill-1.9mm" (board-with-juncture {:drill 2.3}))))
      (exists (toolpath-with-id "drill-1.9mm" (board-with-juncture {:drill 1.9}))))
    (testing "is has hole"
      (exists (test/svg-element (board-with-juncture {:drill 2.3}) [(toolpath "drill") :> :circle])))
    (testing "has a radius of 0.02"
      (is (= "0.02" (:r (drill-hole-attributes {:drill 2.3})))))
    (testing "describes a hole at the juncture's location"
      (is (= ["5" "10"] (center-of-drill-hole {:x 5 :y 10
                                               :drill 2.3}))))))
