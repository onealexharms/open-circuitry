(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest is]]
  [open-circuitry.data-tree :as data]
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

(deftest a-50x100-board-rendering-is-50mm-wide-100mm-high
  (let [{:keys [width height]}
        (-> (dali-rendering [:open-circuitry/board 
                             {:width 50,
                              :height 100}])
            data/attributes)]
    (is (= "50mm" width))
    (is (= "100mm" height))))

(deftest viewbox-ensures-rendered-units-are-millimeters
  (let [view-box (-> (dali-rendering [:open-circuitry/board
                                      {:width 57,
                                       :height 142}])
                     data/attributes
                     :view-box)]
    (is (= "0 0 57 142" view-box))))

(deftest a-board-rendering-is-a-dali-page
  (let [kind-of-node (-> (dali-rendering [:open-circuitry/board
                                          {:width 50,
                                           :height 100}])
                         first)]
    (is (= :dali/page kind-of-node))))
