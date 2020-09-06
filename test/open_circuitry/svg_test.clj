(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.core :refer :all]
  [open-circuitry.svg]))

(deftest rendering-fails-if-no-size-is-given 
  (is (thrown? Exception (open-circuitry.svg/rendering [:open-circuitry/board]))))

(deftest a-50-wide-board-rendering-is-50-wide
  (let [dali-code (open-circuitry.svg/rendering [:open-circuitry/board {:width 50, :height 100}])
        attributes (second dali-code)]
    (is (= 50 (:width attributes)))))

