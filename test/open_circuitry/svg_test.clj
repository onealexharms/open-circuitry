(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.data-tree :as data]
  [open-circuitry.svg :refer [rendered-board]]))

(deftest rendering-fails-if-no-width-is-given 
  (is (thrown-with-msg? Exception
                        #"A board needs a :width attribute"
                        (rendered-board [:open-circuitry/board
                                         {:height 7}]))))

(deftest rendering-fails-if-no-height-is-given
  (is (thrown-with-msg? Exception
                        #"A board needs a :height attribute"
                        (rendered-board [:open-circuitry/board
                                         {:width 9}]))))

(deftest a-50-wide-board-rendering-is-50mm-wide
  (let [width (-> (rendered-board [:open-circuitry/board 
                                   {:width 50, 
                                    :height 100}])
                  data/attributes
                  :width)]
    (is (= "50mm" width))))

(deftest a-100-high-board-rendering-is-100mm-high
  (let [height (-> (rendered-board [:open-circuitry/board 
                                    {:width 50,
                                     :height 100}])
                   data/attributes
                   :height)]
    (is (= "100mm" height))))

(deftest viewbox-ensures-rendered-units-are-millimeters
  (let [view-box (-> (rendered-board [:open-circuitry/board
                                      {:width 57,
                                       :height 142}])
                     data/attributes
                     :view-box)]
    (is (= "0 0 57 142" view-box))))

(deftest a-board-rendering-is-a-dali-page
  (let [kind-of-node (-> (rendered-board [:open-circuitry/board
                                          {:width 50,
                                           :height 100}])
                         first)]
    (is (= :dali/page kind-of-node))))
