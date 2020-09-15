(ns open-circuitry.data-tree)

(defn attributes [node]
  (second node))

(defn children [node]
  (drop 2 node))
