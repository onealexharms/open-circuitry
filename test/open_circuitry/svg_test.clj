(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.core :refer :all]
  [open-circuitry.svg]))

(deftest board-complains-if-no-size-is-given
  (is (thrown? Exception (open-circuitry.svg/rendering [:open-circuitry/board]))))
