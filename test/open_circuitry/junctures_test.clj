(ns open-circuitry.junctures-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer [deftest testing is]]
    [dali.io]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.svg :refer [dali-rendering]]))

(defn svg-element [board selector]
  (-> board
      dali-rendering
      dali.io/render-svg-string
      (.getBytes)
      io/input-stream
      enlive/xml-parser
      (enlive/select selector)
      first))

(deftest a-single-juncture
  (testing "describes a toolpath"
    (let [board [:open-circuitry/board {:width 10, :height 10}
                 [:juncture {:drill 2.3}]]]
      (is (svg-element board [[:g (enlive/attr= :id "drill-2.3mm")]])))))
