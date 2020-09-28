(ns open-circuitry.rendered-board-test
 (:require
  [clojure.test :refer [deftest testing is]]
  [open-circuitry.test-helpers :as test]
  [open-circuitry.svg :refer [dali-rendering]]))

(defmacro exists [thing]
  `(is ~thing))

(deftest a-rendered-board
  (testing "that's 50 x 100"
    (let [board [:open-circuitry/board {:width 50 :height 100}]
          {:keys [width height viewBox]} (test/attributes-by-selector [:svg] board)]
      (testing "has width of 50mm"
        (is (= "50mm" width)))
      (testing "has height of 100mm"
        (is (= "100mm" height)))
      (testing "uses millimeters"
        (is (= "0 0 50 100" viewBox)))))
  (testing "is only possible if"
    (testing "it has a width"
      (is (thrown-with-msg? Exception
                            #"A board needs attribute: :width"
                            (dali-rendering [:open-circuitry/board
                                             {:height 7}]))))
    (testing "it has a height"
      (is (thrown-with-msg? Exception
                            #"A board needs attribute: :height"
                            (dali-rendering [:open-circuitry/board
                                             {:width 9}]))))))

(deftest the-cutout-toolpath
  (testing "exists"
    (let [board [:open-circuitry/board {:width 10 :height 10}]
          cutout-toolpath (test/element-by-selector [:g#cutout-toolpath] board)]
     (exists cutout-toolpath)))
  (testing "has a zero origin"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [x y]} (test/attributes-by-selector [:g#cutout-toolpath :rect] board)]
      (is (= "0" x))
      (is (= "0" y))))
  (testing "size matches board size"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [width height]} (test/attributes-by-selector [:g#cutout-toolpath :rect] board)]
      (is (= "10" width))
      (is (= "20" height))))
  (testing "has white fill"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [fill]} (test/attributes-by-selector [:g#cutout-toolpath :rect] board)]
      (is (= "white" fill))))
  (testing "has visible stroke"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [stroke]} (test/attributes-by-selector [:g#cutout-toolpath :rect] board)]
      (is (= "cornflowerblue" stroke)))))
