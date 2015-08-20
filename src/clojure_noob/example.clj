(ns clojure-noob.example
  (:gen-class)
  (:use [clojure-noob.distn]
        [clojure-noob.huffman]))

(defn -main
  [& args]
  (let [source-text "iamtheverymodelofamodernmajorgeneral"
        source-coll (map #(symbol (str %)) (seq source-text))
        source-freqs (count-instances source-coll)
        my-tree (huffman-build source-freqs)
        my-paths (paths my-tree)
        huff-codes (into (sorted-map) (map #(vector (apply str (map name (% 0))) (% 1)) my-paths)) ]
    (println huff-codes)))
