(ns open-circuitry.svg-test
 (:require
  [clojure.java.io :as io]
  [clojure.test :refer [deftest is]]
  [dali.io]
  [net.cgrand.enlive-html :as enlive]
  [open-circuitry.svg :refer [dali-rendering]]))

(deftest rendering-fails-if-no-width-is-given
  (is (thrown-with-msg? Exception
                        #"A board needs a :width attribute"
                        (dali-rendering [:open-circuitry/board
                                         {:height 7}]))))

(deftest rendering-fails-if-no-height-is-given
  (is (thrown-with-msg? Exception
                        #"A board needs a :height attribute"
                        (dali-rendering [:open-circuitry/board
                                         {:width 9}]))))

(defn svg-element [board selector]
  (-> board
      dali-rendering
      dali.io/render-svg-string
      (.getBytes)
      io/input-stream
      enlive/xml-parser
      (enlive/select selector)
      first))

(deftest has-a-cutout-toolpath
  (let [board [:open-circuitry/board {:width 10 :height 10}]
        cutout-toolpath (svg-element board [:g#cutout-toolpath])]
   (is cutout-toolpath)))

(defn svg-attributes [board]
  (:attrs (svg-element board [:svg])))

(deftest a-50x100-board-rendering-is-50mm-wide-100mm-high
  (let [board [:open-circuitry/board {:width 50 :height 100}]
        {:keys [width height]} (svg-attributes board)]
    (is (= "50mm" width))
    (is (= "100mm" height))))

(deftest viewbox-ensures-rendered-units-are-millimeters
  (let [board [:open-circuitry/board {:width 57 :height 142}]
        {:keys [viewBox]} (svg-attributes board)]
    (is (= "0 0 57 142" viewBox))))
