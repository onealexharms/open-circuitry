(ns open-circuitry.junctures-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.test-helpers :as test]
    [open-circuitry.svg :refer [dali-rendering]]))

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
    (testing "has a well-formed toolpath"
      (exists (toolpath-with-id "drill-2.3mm" (board-with-juncture {:x 1, :y 2, :drill 2.3})))
      (exists (not (toolpath-with-id "drill-1.9mm" (board-with-juncture {:x 2, :y 3, :drill 2.3}))))
      (exists (toolpath-with-id "drill-1.9mm" (board-with-juncture {:x 1, :y 0, :drill 1.9}))))
    (testing "creates a hole that accomodates LaserWeb's hole weirdness"
      (exists (test/svg-element (board-with-juncture {:x 11, :y 12, :drill 2.3}) [(toolpath "drill") :> :circle]))
      (is (= "0.02" (:r (drill-hole-attributes {:x 63, :y 65, :drill 2.3})))))
    (testing "at the juncture's location"
      (is (= ["5" "10"] (center-of-drill-hole {:x 5 :y 10
                                               :drill 2.3}))))
    (testing "only if it has"
      (testing "an x coordinate"
        (is (thrown-with-msg? Exception
                              #"A juncture needs attribute: :x"
                              (dali-rendering (board-with-juncture {:y 6 :drill 22.7})))))
      (testing "an y coordinate"
        (is (thrown-with-msg? Exception
                              #"A juncture needs attribute: :y"
                              (dali-rendering (board-with-juncture {:x 418 :drill 4.8}))))))))
