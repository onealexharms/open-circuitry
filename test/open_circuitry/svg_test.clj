(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.data-tree :as data]
  [open-circuitry.svg :refer [rendered-board]]))

(deftest rendering-fails-if-no-width-is-given 
  (is (thrown-with-msg? Exception
                        #"A board needs a :width attribute"
                        (rendered-board [:open-circuitry/board]))))

(deftest a-50-wide-board-rendering-is-50-wide
  (let [width (-> (rendered-board [:open-circuitry/board 
                                   {:width 50, 
                                    :height 100}])
                  data/attributes
                  :width)]
    (is (= 50 width))))
