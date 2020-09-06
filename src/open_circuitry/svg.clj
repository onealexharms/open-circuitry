(ns open-circuitry.svg)

(defn attributes [node]
  (second node))

(defn rendering
  [board]
  (if (:width (attributes board))
    [nil {:width (:width (attributes board))}]
    (throw (Exception. "A board needs a :width attribute"))))
