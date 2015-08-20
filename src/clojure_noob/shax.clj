(ns clojure-noob.shax
  (:require
    [clojure.zip :as zip]
    [clojure.data.zip :as dzip]
    [clojure.data.xml :as xml]
    [clojure.data.zip.xml :as zipxml]
    [clojure.xml :as xmll]
    [clojure.string :as s]))

(defn force-parse [source]
  (let [result (xml/parse source)
        ignored (.toString result)]
    result))


(def tree
  (with-open [source (java.io.FileReader. "./as_you_like_it.xml")]
    (zip/xml-zip (force-parse source))))

(defn at-tag-of [tag-kw]
  (fn [zipper] (= (:tag (zip/node zipper)) tag-kw)))

(defn descendants-of-tag [tag-kw zipper]
  (->> zipper
      (dzip/descendants)
      (filter (at-tag-of tag-kw))))

(defn first-tag-up [tag-kw zipper]
  (first (filter (at-tag-of tag-kw) (dzip/ancestors zipper))))

(defn first-tag-down [tag-kw zipper]
  (first (filter (at-tag-of tag-kw) (dzip/descendants zipper))))

(defn zipp [& stuff]
  (apply map (conj stuff vector)))

(defn with-act [init zipper]
  (let [acts (descendants-of-tag :ACT zipper)
        get-title #(->> %
                        (first-tag-down :TITLE)
                        (zip/node)
                        (:content)
                        (:apply str))
        ctx-of #(->> %
                     (get-title)
                     (assoc init :ACT))]
    (zipp acts (map ctx-of acts))))

(defn with-scene [init zipper]
  (let [scenes (descendants-of-tag :SCENE zipper)
        get-title #(->> %
                        (first-tag-down :TITLE)
                        (zip/node)
                        (:content)
                        (:apply str))
        ctx-of #(->> %
                     (get-title)
                     (assoc init :SCENE))]
    (zipp scenes (map ctx-of scenes))))

(defn with-speech [init zipper]
  (let [speeches (descendants-of-tag :SPEECH zipper)
        get-speaker #(->> %
                          (first-tag-down :SPEAKER)
                          (zip/node)
                          (:content)
                          (apply str))
        ctx-of #(->> %
                     (get-speaker)
                     (assoc init :SPEAKER))]
    (zipp speeches (map ctx-of speeches))))

(defn get-line-text [zipper]
  (let [lines (descendants-of-tag :LINE zipper)
        get-line #(->> %
                       (zip/node)
                       (:content)
                       (apply str))]
    (apply str (interpose " " (map get-line lines)))))

(defn lines-with-context [zipper]
  (->> zipper
       (with-act {})
       (mapcat #(with-scene (% 1) (% 0)))
       (mapcat #(with-speech (% 1) (% 0)))
       (map #(assoc (% 1)
                    :LINE
                    (get-line-text (% 0))))))
