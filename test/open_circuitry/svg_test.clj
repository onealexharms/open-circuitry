(ns open-circuitry.svg-test
 (:require
  [clojure.test :refer [deftest testing is]]
  [open-circuitry.test-helpers :as test]
  [open-circuitry.svg :refer [dali-rendering]]))

(deftest renders
  (testing "a 50x100 board"
    (let [board [:open-circuitry/board {:width 50 :height 100}]
          {:keys [width height]} (test/svg-attributes board [:svg])]
      (testing "as 50mm wide"
        (is (= "50mm" width)))
      (testing "as 100mm high"
        (is (= "100mm" height)))))

  (testing "in millimeters"
    (let [board [:open-circuitry/board {:width 57 :height 142}]
          {:keys [viewBox]} (test/svg-attributes board [:svg])]
      (is (= "0 0 57 142" viewBox))))
  (testing "only if it has"
    (testing "a width"
      (is (thrown-with-msg? Exception
                            #"A board needs attribute: :width"
                            (dali-rendering [:open-circuitry/board
                                             {:height 7}]))))
    (testing "a height"
      (is (thrown-with-msg? Exception
                            #"A board needs attribute: :height"
                            (dali-rendering [:open-circuitry/board
                                             {:width 9}]))))))

(deftest cutout
  (testing "has a cutout toolpath"
    (let [board [:open-circuitry/board {:width 10 :height 10}]
          cutout-toolpath (test/svg-element board [:g#cutout-toolpath])]
     (is cutout-toolpath)))
  (testing "has a zero origin"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [x y]} (test/svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "0" x))
      (is (= "0" y))))
  (testing "size matches board size"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [width height]} (test/svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "10" width))
      (is (= "20" height))))
  (testing "has white fill"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [fill]} (test/svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "white" fill))))
  (testing "has visible stroke"
    (let [board [:open-circuitry/board {:width 10 :height 20}]
          {:keys [stroke]} (test/svg-attributes board [:g#cutout-toolpath :rect])]
      (is (= "cornflowerblue" stroke)))))
