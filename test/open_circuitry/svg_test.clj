(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.core :refer :all]
  [open-circuitry.svg]))

(deftest rendering-fails-if-no-width-is-given 
  (is (thrown-with-msg? Exception
                        #"A board needs a :width attribute"
                        (open-circuitry.svg/rendering [:open-circuitry/board]))))

(deftest a-50-wide-board-rendering-is-50-wide
  (let [rendered-board (open-circuitry.svg/rendering [:open-circuitry/board {:width 50, :height 100}])
        attributes (second rendered-board)]
    (is (= 50 (:width attributes)))))

