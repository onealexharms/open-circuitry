(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.core :refer :all]
  [open-circuitry.svg]))

(deftest empty-board-makes-empty-svg-rendering
  (is (= [:dali/page] (open-circuitry.svg/rendering [:open-circuitry/board]))))
