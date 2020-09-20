(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest testing is]]
  [open-circuitry.test-helpers :as test]
  [open-circuitry.svg :refer [dali-rendering]]))

(defn svg-attributes [board selector]
  (:attrs (test/svg-element board selector)))

(deftest rendering-open-circuitry-as-dali
  (testing "fails if no width is given"
    (is (thrown-with-msg? Exception
                          #"A board needs a :width attribute"
                          (dali-rendering [:open-circuitry/board
                                           {:height 7}]))))
  (testing "fails if no height is given"
    (is (thrown-with-msg? Exception
                          #"A board needs a :height attribute"
                          (dali-rendering [:open-circuitry/board
                                           {:width 9}])))))

(deftest cutout
  (testing "has a cutout toolpath"
    (let [board [:open-circuitry/board {:width 10 :height 10}]
          cutout-toolpath (test/svg-element board [:g#cutout-toolpath])]
     (is cutout-toolpath)))
  (testing "has a zero origin"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [x y]} (svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "0" x))
      (is (= "0" y))))
  (testing "size matches board size"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [width height]} (svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "10" width))
      (is (= "20" height))))
  (testing "has white fill"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [fill]} (svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "white" fill))))
  (testing "has red stroke"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [stroke]} (svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "red" stroke)))))
  
(deftest board-size-is-you-know-correct-lol
  (testing "a 50x100 board rendering is 50 wide x 100 high"
    (let [board [:open-circuitry/board {:width 50 :height 100}]
          {:keys [width height]} (svg-attributes board [:svg])]
      (is (= "50mm" width))
      (is (= "100mm" height))))

  (testing "viewbox ensures rendered units are millimeters"
    (let [board [:open-circuitry/board {:width 57 :height 142}]
          {:keys [viewBox]} (svg-attributes board [:svg])]
      (is (= "0 0 57 142" viewBox)))))
