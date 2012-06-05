(in-ns 'porter)
(clojure.core/refer 'clojure.core)

(defn pop-stemmer-on
  [predicate stemmer]
  (if (and (seq (:word stemmer)) (predicate stemmer))
    (recur predicate (pop-word stemmer))
    stemmer))

(def vowel-letter? #{\a \e \i \o \u})

(defn consonant?
  "Returns true if the ith character in a stemmer is a consonant. i defaults to :index."
  ([stemmer]
   (consonant? stemmer (get-index stemmer)))
  ([stemmer i]
   (let [c (nth (:word stemmer) i)]
     (cond (vowel-letter? c) false
           (=c \y) (if (zero? i)
                     true
                     (not (consonant? stemmer (dec i))))
           :else true))))

(def vowel? (complement consonant?))

(defn vowel-in-stem?
  "true iff 0...j contains a vowel"
  [stemmer]
  (let [j (get-index stemmer)]
    (loop [i 0]
      (cond (> i j) false
            (consonant? stemmer i) (recur (inc i))
            :else true))))

(defn double-c?
  ([stemmer]
   (double-c? stemmer (get-index stemmer)))
  ([stemmer j]
   (and (>= j 1)
        (= (nth (:word stemmer) j)
           (nth (:word stemmer) (dec j)))
        (consonant? stemmer j))))

(defn cvc?
  ([stemmer]
   (cvc? stemmer (get-index stemmer)))
  ([stemmer i]
   (and (>= i 2)
        (consonant? stemmer (- i 2))
        (vowel? stemmer (dec i))
        (consonant? stemmer i)
        (not (#{\w \x \y} (nth (:word stemmer) i))))))


